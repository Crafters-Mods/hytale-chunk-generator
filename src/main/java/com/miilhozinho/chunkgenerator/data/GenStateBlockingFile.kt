package com.miilhozinho.chunkgenerator.data

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.hypixel.hytale.server.core.util.io.BlockingDiskFile
import com.miilhozinho.chunkgenerator.util.FileUtils
import com.miilhozinho.chunkgenerator.util.LogUtil
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.nio.file.Path

class GenStateBlockingFile : BlockingDiskFile(Path.of(FileUtils.SESSION_PATH)) {
    var genState: GenState

    init {
        this.genState = GenState()
    }

    @Throws(IOException::class)
    override fun read(bufferedReader: BufferedReader) {
        val rootElement = JsonParser.parseReader(bufferedReader)
        if (rootElement == null || !rootElement.isJsonObject) return
        val root = rootElement.asJsonObject

        try {
            val worldName = if (root.has("WorldName") && !root.get("WorldName").isJsonNull) root.get("WorldName").asString else ""
            val centerX = if (root.has("CenterX") && !root.get("CenterX").isJsonNull) root.get("CenterX").asInt else 0
            val centerZ = if (root.has("CenterZ") && !root.get("CenterZ").isJsonNull) root.get("CenterZ").asInt else 0
            val targetRadius = if (root.has("TargetRadius") && !root.get("TargetRadius").isJsonNull) root.get("TargetRadius").asInt else 0

            this.genState = GenState(
                worldName,
                centerX,
                centerZ,
                targetRadius
            )

            if (root.has("CurrentIndex") && !root.get("CurrentIndex").isJsonNull) {
                try {
                    this.genState.setCurrentIndex(root.get("CurrentIndex").asInt)
                } catch (ex: Exception) {
                    LogUtil.logSevere("Invalid CurrentIndex in session.json: ${ex.message}")
                }
            }

            if (root.has("IsPaused") && !root.get("IsPaused").isJsonNull) {
                try {
                    this.genState.setPaused(root.get("IsPaused").asBoolean)
                } catch (ex: Exception) {
                    LogUtil.logSevere("Invalid IsPaused in session.json: ${ex.message}")
                }
            }
        } catch (ex: Exception) {
            LogUtil.logSevere("Failed to parse session.json: ${ex.message}")
        }
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