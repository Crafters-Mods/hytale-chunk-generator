package com.miilhozinho.chunkgenerator.manager

import com.hypixel.hytale.math.util.ChunkUtil
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandSender
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk
import com.miilhozinho.chunkgenerator.ChunkGeneratorPlugin
import com.miilhozinho.chunkgenerator.data.GenState
import com.miilhozinho.chunkgenerator.data.GenStateBlockingFile
import com.miilhozinho.chunkgenerator.data.PlayerManager.broadcastEvent
import com.miilhozinho.chunkgenerator.events.ProgressUpdatedEvent
import com.miilhozinho.chunkgenerator.util.FileUtils
import com.miilhozinho.chunkgenerator.util.LogUtil
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import kotlin.math.abs
import kotlin.math.max

class GenerationManager  {
    private var genStateFile: GenStateBlockingFile
    private val scheduler: ScheduledExecutorService
    private val savingThread: Thread?
    private var isDirty = false

    init {
        FileUtils.ensureMainDirectory()
        this.genStateFile = GenStateBlockingFile()
        this.scheduler = Executors.newScheduledThreadPool(1)
        try {
            this.genStateFile.syncLoad()
        } catch (e: Exception) {
            LogUtil.logSevere("Failed to load generation state: ${e.message}")
        }

        this.savingThread = Thread(Runnable {
            while (true) {
                if (isDirty) {
                    isDirty = false
                    try {
                        genStateFile.syncSave()
                        LogUtil.logInfo("Saved generation state")
                    } catch (e: Exception) {
                        LogUtil.logInfo("Failed to save: ${e.message}")
                    }
                }
                try {
                    Thread.sleep(5000)
                } catch (e: InterruptedException) {
                    break
                }
            }
        })
        this.savingThread.start()

        // Resume if not paused
        if (!genStateFile.genState.isPaused() && !genStateFile.genState.worldName.isEmpty()) {
            startGenerationInternal(null)
        }
    }

    fun startGeneration(worldName: String, sender: CommandSender, centerX: Int, centerZ: Int) {
        // Use the current configured radius, default to 100 if not set
        var radius = genStateFile.genState.getTargetRadius()
        if (radius <= 0) {
            radius = 100 // Default radius
        }
        sender.sendMessage(Message.raw("Started chunk generation at position ($centerX, $centerZ) with radius $radius"))

        val state = GenState(worldName, centerX, centerZ, radius)
        genStateFile.genState = state
        markDirty()
        startGenerationInternal(sender)
    }

    fun setTargetRadius(radius: Int) {
        genStateFile.genState.setTargetRadius(radius)
        markDirty()
    }

    fun pauseGeneration() {
        broadcastEvent(
            ProgressUpdatedEvent(
                0.0,
                genStateFile.genState.getCurrentIndex().toLong(),
                totalChunks(),
                false
            )
        )
        genStateFile.genState.setPaused(true)
        markDirty()
    }

    fun resumeGeneration(player: Player?) {
        genStateFile.genState.setPaused(false)
        markDirty()
        startGenerationInternal(player)
    }

    private fun startGenerationInternal(player: CommandSender?) {
        val state = genStateFile.genState
        if (state.isPaused() || state.worldName.isEmpty()) return

        val world = ChunkGeneratorPlugin.WORLDS.get(state.worldName)
        if (world == null) return

        broadcastEvent(
            ProgressUpdatedEvent(
                0.0,
                state.getCurrentIndex().toLong(),
                totalChunks()
            )
        )
        scheduler.scheduleAtFixedRate(Runnable { processGeneration(world, player) }, 0, 1, TimeUnit.SECONDS)
    }

    private fun totalChunks(): Long {
        val radius = genStateFile.genState.getTargetRadius()
        return (2L * radius + 1) * (2L * radius + 1)
    }

    private fun processGeneration(world: World, player: CommandSender?) {
        val state = genStateFile.genState

        if (state.isPaused()) {
            return
        }

        // Calculate total chunks for percentage display
        val totalChunks = totalChunks()

        // TODO: Replace with Hytale TPS monitoring API
        // Example: double tps = Server.getTPS(); or world.getServer().getTPS();
        val tps = 20.0 // Placeholder - assume good TPS

        // TODO: Replace with your config access
        // int maxChunks = (tps < ChunkGenerator.CONFIG.get().getTpsThreshold()) ? 1 : ChunkGenerator.CONFIG.get().getMaxChunksPerTick();
        val maxChunks = if (tps < 18.0) 1 else 5 // Placeholder values

        for (i in 0..< maxChunks) {
            val coords = getSpiralCoordinates(state.getCurrentIndex())

            val chunkX = state.centerX + coords[0]
            val chunkZ = state.centerZ + coords[1]

            // Check radius
            val dist = max(abs(coords[0]), abs(coords[1]))
            if (dist > state.getTargetRadius()) {
                pauseGeneration()
                LogUtil.logInfo("Generation completed for radius ${state.getTargetRadius()}")
                broadcastEvent(
                    ProgressUpdatedEvent(
                        100.0,
                        state.getCurrentIndex().toLong(),
                        totalChunks
                    )
                )
                return
            }

            try {
                val chunkStore = world.chunkStore
                val chunkIndex = ChunkUtil.indexChunk(chunkX, chunkZ)
                val chunkRef = chunkStore.getChunkReference(chunkIndex)
                if (chunkRef != null && chunkRef.isValid) {
                    LogUtil.logInfo("Já existe $chunkX $chunkZ")
                } else {
                    world.getChunkAsync(chunkX, chunkZ).thenAccept(Consumer { worldChunk: WorldChunk? ->
                        world.execute(
                            Runnable {
                                worldChunk!!.markNeedsSaving()
                            })
                    })
                }

            } catch (e: Exception) {
                LogUtil.logSevere("Failed to generate chunk $chunkX, $chunkZ: ${e.message}")
            }

            val currentIndex = state.getCurrentIndex().toLong()
            val percentage = (currentIndex * 100.0) / totalChunks
            if (currentIndex % 10 == 0L || percentage >= 100) {
                broadcastEvent(
                    ProgressUpdatedEvent(
                        percentage,
                        currentIndex,
                        totalChunks
                    )
                )
            }

            state.setCurrentIndex(state.getCurrentIndex() + 1)

            // Save progress every 100 chunks
            if (state.getCurrentIndex() % 100 == 0) {
                markDirty()
            }
        }

        // Save progress at the end of each batch
        markDirty()
    }

    private fun getSpiralCoordinates(index: Int): IntArray {
        if (index == 0) return intArrayOf(0, 0)

        var x = 0
        var y = 0
        var dx = 0
        var dy = -1
        for (i in 0..<index) {
            if (x == y || (x < 0 && x == -y) || (x > 0 && x == 1 - y)) {
                val temp = dx
                dx = -dy
                dy = temp
            }
            x += dx
            y += dy
        }
        return intArrayOf(x, y)
    }

    private fun markDirty() {
        isDirty = true
    }
}