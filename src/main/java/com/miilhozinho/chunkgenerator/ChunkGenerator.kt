package com.miilhozinho.chunkgenerator

import com.hypixel.hytale.server.core.HytaleServer
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.events.AddWorldEvent
import com.hypixel.hytale.server.core.universe.world.events.RemoveWorldEvent
import com.hypixel.hytale.server.core.util.Config
import com.miilhozinho.chunkgenerator.command.GenerateCommand
import com.miilhozinho.chunkgenerator.config.ChunkGeneratorConfig
import com.miilhozinho.chunkgenerator.data.PlayerManager.addPlayer
import com.miilhozinho.chunkgenerator.data.PlayerManager.removePlayer
import com.miilhozinho.chunkgenerator.events.ProgressUpdatedEvent
import com.miilhozinho.chunkgenerator.manager.GenerationManager
import com.miilhozinho.chunkgenerator.ui.ProgressUpdatedEventHandler
import java.util.logging.Level


class ChunkGenerator(init: JavaPluginInit) : JavaPlugin(init) {
    init {
        CONFIG = this.withConfig<ChunkGeneratorConfig?>("ChunkGenerator", ChunkGeneratorConfig.CODEC)
    }

    // Make generationManager a property so event handlers can access it
    private lateinit var generationManager: GenerationManager

    override fun setup() {
        super.setup()
        this.generationManager = GenerationManager()

        this.commandRegistry.registerCommand(GenerateCommand(generationManager))

        this.eventRegistry
            .registerGlobal(AddWorldEvent::class.java) { event: AddWorldEvent? ->
                WORLDS[event!!.world.name] = event.world
                this.logger.at(Level.INFO).log("Registered world: " + event.world.name)

                // Try to auto-resume generation for this world if a saved session exists
                try {
                    generationManager.tryAutoResumeForWorld(event.world.name)
                } catch (ex: Exception) {
                    this.logger.at(Level.WARNING)
                        .log("Failed to auto-resume generation for world ${event.world.name}: ${ex.message}")
                }
            }

        this.eventRegistry.registerGlobal(
            RemoveWorldEvent::class.java
        ) { event: RemoveWorldEvent? ->
            WORLDS.remove(event!!.world.name)
        }

        registerServiceBusEvents()
    }

    fun registerServiceBusEvents() {
        val eventBus = HytaleServer.get().eventBus

        eventBus
            .register(PlayerConnectEvent::class.java) { event: PlayerConnectEvent? ->
                addPlayer(event!!.playerRef)
            }

        eventBus
            .register(PlayerDisconnectEvent::class.java) { event: PlayerDisconnectEvent? ->
                removePlayer(event!!.getPlayerRef().uuid)
            }

        eventBus
            .register(ProgressUpdatedEvent::class.java, { event: ProgressUpdatedEvent ->
                ProgressUpdatedEventHandler(event.playerRef!!).handle(event)
            })
//        eventBus
//            .register(ProgressUpdatedEvent::class.java) { event: ProgressUpdatedEvent ->
//                val playerRef: PlayerRef? = event.playerRef
//                val player: Player? =
//                    playerRef!!.reference!!.store.getComponent(playerRef.reference!!, Player.getComponentType())
//                if (player!!.hasPermission("chunkgenerator.ui.see")) {
//                    ProgressUpdatedEventHandler(playerRef).handle(event)
//                }
//            }
    }

    companion object {
        var CONFIG: Config<ChunkGeneratorConfig?>? = null
        var WORLDS: HashMap<String?, World?> = HashMap()
    }
}