import org.gradle.kotlin.dsl.tasks
import kotlin.text.set

plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.3.1"
    kotlin("jvm")
    id("maven-publish") // Add this line
}

kotlin {
    jvmToolchain(25)
}

group = findProperty("pluginGroup") as String? ?: "com.miilhozinho"
val pluginName = findProperty("pluginName") as String? ?: "PluginName"
val githubRef = System.getenv("GITHUB_REF")
version = if (githubRef != null && githubRef.startsWith("refs/tags/")) {
    githubRef.replace("refs/tags/", "").replace("v", "") // Clean "v1.0.0" to "1.0.0"
} else {
    findProperty("pluginVersion") as String? ?: "1.0.0"
}
description = findProperty("pluginDescription") as String? ?: "A Hytale plugin template"
val serverVersion = findProperty("serverVersion") as String? ?: "2026.02.19-1a311a592"


java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

repositories {
    mavenCentral()

    maven {
        name = "hytale-release"
        url = uri("https://maven.hytale.com/release")
    }

    maven {
        name = "hytale-pre-release"
        url = uri("https://maven.hytale.com/pre-release")
    }
    exclusiveContent {
        forRepository {
            maven {
                url = uri("https://cursemaven.com")
            }
        }
        filter {
            includeGroup("curse.maven")
        }
    }
    // Local repository for the license engine
    mavenLocal()
}
val runtimeServer: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
    extendsFrom(configurations.compileOnly.get())
}

dependencies {
    // All dependencies available for compilation and runtime
    compileOnly("com.hypixel.hytale:Server:2026.02.19-1a311a592")

    compileOnly("curse.maven:Multiplehud-1423634:7530266")
    compileOnly("curse.maven:HyUI-1431415:7667069")

    // Testing - JUnit 4
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")

//    implementation(kotlin("reflect"))
}

tasks {
    processResources {
        filteringCharset = Charsets.UTF_8.name()

        // Replace placeholders in manifest.json
        val props = mapOf(
            "Group" to project.group,
            "Name" to pluginName,
            "Version" to project.version,
            "Description" to project.description,
            "ServerVersion" to serverVersion
        )
        inputs.properties(props)

        filesMatching("manifest.json") {
            expand(props)
        }
    }

    shadowJar {
        archiveClassifier.set("")
        archiveBaseName.set(pluginName)

        // O shadowJar usa o runtimeClasspath, que por padrão NÃO inclui compileOnly
        configurations = listOf(project.configurations.runtimeClasspath.get())

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        mergeServiceFiles()

        // 1. Relocate all external dependencies to your internal shadow package
        val shadowPackage = "${project.group}.shadow"

        minimize()

        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
        exclude("META-INF/maven/**")
        exclude("org/jetbrains/annotations/**")
//        exclude("kotlin/**")
    }

    // Make build depend on shadowJar
    build {
        dependsOn(shadowJar)
    }

    // Configure JUnit 4 for tests
    test {
        useJUnit()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    register<JavaExec>("runHytale") {
        dependencies {
            // All dependencies available for compilation and runtime
            runtimeOnly("com.hypixel.hytale:Server:2026.02.19-1a311a592")
            runtimeOnly("curse.maven:Multiplehud-1423634:7530266")
            runtimeOnly("curse.maven:HyUI-1431415:7667069")
        }
        val appData = System.getenv("APPDATA")
        val defaultAssetsPath = "$appData\\Hytale\\install\\release\\package\\game\\latest\\Assets.zip"

        val hytaleAssets = project.findProperty("hytaleAssets") as String? ?: defaultAssetsPath

        group = "application"
        mainClass.set("com.hypixel.hytale.Main")

        classpath = sourceSets.main.get().runtimeClasspath + runtimeServer

        standardInput = System.`in`
        workingDir = file("run")
        args(
            "--allow-op",
            "--assets=$hytaleAssets",
            "--disable-sentry",
            "--bind=0.0.0.0:5525"
        )

        jvmArgs("-XX:+ClassUnloading", "-Xmx2G")
//        jvmArgs("-XX:+AllowEnhancedClassRedefinition", "-XX:+ClassUnloading")
    }
}

publishing {
    publications {
        create<MavenPublication>("gpr") {
            artifact(tasks.shadowJar.get())

            groupId = project.group.toString()
            artifactId = "chunk-generator"
            version = project.version.toString()
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            // Replace USERNAME/REPO with your actual GitHub info
            url = uri("https://maven.pkg.github.com/Crafters-Mods/hytale-chunk-generator")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
