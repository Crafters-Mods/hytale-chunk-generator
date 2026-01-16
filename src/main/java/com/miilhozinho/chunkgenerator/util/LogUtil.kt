package com.miilhozinho.chunkgenerator.util

import com.hypixel.hytale.logger.HytaleLogger
import java.util.logging.Level

object LogUtil {
    private val logger: HytaleLogger = HytaleLogger.getLogger().getSubLogger("ChunkGenerator")
    fun logInfo(message: String) {
        logger
            .at(Level.INFO)
            .log(message)
    }

    fun logSevere(message: String) {
        logger
            .at(Level.SEVERE)
            .log(message)
    }
}