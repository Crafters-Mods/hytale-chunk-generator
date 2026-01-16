package com.miilhozinho.chunkgenerator.data

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.hypixel.hytale.logger.HytaleLogger
import com.hypixel.hytale.server.core.util.io.BlockingDiskFile
import com.miilhozinho.chunkgenerator.util.FileUtils
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.nio.file.Path

class GenStateBlockingFile : BlockingDiskFile(Path.of(FileUtils.SESSION_PATH)) {
    var genState: GenState
    private val logger = HytaleLogger.getLogger().getSubLogger("ChunkGenerator")

    init {
        this.genState = GenState()
    }

    @Throws(IOException::class)
    override fun read(bufferedReader: BufferedReader) {
        val rootElement = JsonParser.parseReader(bufferedReader)
        if (rootElement == null || !rootElement.isJsonObject) return
        val root = rootElement.asJsonObject
        this.genState = GenState(
            root.get("WorldName").asString,
            root.get("CenterX").asInt,
            root.get("CenterZ").asInt,
            root.get("TargetRadius").asInt
        )
        this.genState.setCurrentIndex(root.get("currentIndex").asInt)
        this.genState.setPaused(root.get("isPaused").asBoolean)
    }

    @Throws(IOException::class)
    override fun write(bufferedWriter: BufferedWriter) {
        val root = JsonObject()
        root.addProperty("WorldName", genState.worldName)
        root.addProperty("CenterX", genState.centerX)
        root.addProperty("CenterZ", genState.centerZ)
        root.addProperty("TargetRadius", genState.getTargetRadius())
        root.addProperty("CurrentIndex", genState.getCurrentIndex())
        root.addProperty("IsPaused", genState.isPaused())
        bufferedWriter.write(root.toString())
    }

    @Throws(IOException::class)
    override fun create(bufferedWriter: BufferedWriter) {
        this.genState = GenState()
        write(bufferedWriter)
    }
}