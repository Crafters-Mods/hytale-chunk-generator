package com.miilhozinho.chunkgenerator.ui

import com.hypixel.hytale.math.util.ChunkUtil
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.miilhozinho.chunkgenerator.events.ProgressUpdatedEvent
import com.miilhozinho.chunkgenerator.util.LogUtil

class ProgressUpdatedEventHandler(playerRef: PlayerRef) : CustomUIHud(playerRef) {

    fun handle(event: ProgressUpdatedEvent) {
        val builder = UICommandBuilder()
        if (event.enabled) {
            val processBarWidth = 420
            val barsToColor = event.percentage / 100.0
            if (event.percentage == 100.0) {
                LogUtil.logInfo("${event.log()} Chunk Generator finished")
                update(true, builder)
                return
            }
            builder.append("Hud/Miilhozinho_ChunkGenerator_Progress.ui")

            builder.set(
                "#LabelChunkGenerator.Text",
                String.format(
                    "Progress: %.1f%% (%d/%d pos) (%d/%d chunks)",
                    event.percentage,
                    event.currentChunks,
                    event.totalChunks,
                    ChunkUtil.chunkCoordinate(event.currentChunks),
                    ChunkUtil.chunkCoordinate(event.totalChunks)
                )
            )

            val printBarQuantity = processBarWidth * barsToColor

            builder.appendInline("#Bar",
                "Group {\n" +
                    "    LayoutMode: Left;\n" +
                    "    Anchor: (Width: $printBarQuantity, Height: 25);\n" +
                    "    Background: #46b447(0.7);\n" +
                    "\n" +
                    "}")
        }
        build(builder)
    }

    override fun build(builder: UICommandBuilder) {
        update(true, builder)
    }
}
