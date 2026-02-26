package com.miilhozinho.chunkgenerator.ui

import au.ellie.hyui.builders.HudBuilder
import au.ellie.hyui.builders.HyUIHud
import au.ellie.hyui.elements.UIType
import au.ellie.hyui.html.TemplateProcessor
import com.hypixel.hytale.math.util.ChunkUtil
import com.miilhozinho.chunkgenerator.data.PlayerConnectEvent
import com.miilhozinho.chunkgenerator.events.ProgressUpdatedEvent
import com.miilhozinho.chunkgenerator.util.LogUtil
import java.util.concurrent.ConcurrentHashMap

data class ChunkGeneratorProgressDto(
    val hud: HyUIHud,
    var percentage: Double
)

class ProgressUpdatedEventHandler(playerConnectEvent: PlayerConnectEvent) {


    companion object{
        protected var hudsByPlayerId: ConcurrentHashMap<String, ChunkGeneratorProgressDto> = ConcurrentHashMap()
        val htmlFile = "Hud/ChunkGenerator_Progress.html"
    }

    fun handle(event: ProgressUpdatedEvent) {
//        val builder = UICommandBuilder()
        val templateProcessor = TemplateProcessor()
        templateProcessor.setVariable("percentage", event.percentage / 100)
        templateProcessor.setVariable("percentageLabel",
            String.format(
                    "Progress: %.1f%% (%d/%d pos) (%d/%d chunks)",
                    event.percentage,
                    event.currentChunks,
                    event.totalChunks,
                    ChunkUtil.chunkCoordinate(event.currentChunks),
                    ChunkUtil.chunkCoordinate(event.totalChunks)
                ))

        val builder = HudBuilder.detachedHud()
            .loadHtml(htmlFile,
                templateProcessor,
                UIType.HYWIND)

        val hudData = hudsByPlayerId.getOrPut(event.playerConnectEvent!!.playerRef.uuid.toString()) {
            ChunkGeneratorProgressDto(builder.show(event.playerConnectEvent!!.playerRef), event.percentage)
        }

        if (!event.enabled){
            hudData.hud.hide()
//            hudBuilder.remove()
            hudsByPlayerId.remove(event.playerConnectEvent!!.playerRef.uuid.toString())
        }
        else{

            val processBarWidth = 420
            val barsToColor = event.percentage / 100.0
            if (event.percentage == 100.0) {
                LogUtil.logInfo("${event.log()} Chunk Generator finished")
                hudData.hud.hide()
//                hudBuilder.remove()
                hudsByPlayerId.remove(event.playerConnectEvent!!.playerRef.uuid.toString())
                return
            }


//            val player = event.playerConnectEvent!!.playerRef.holder!!.getComponent(Player.getComponentType())
//            val build = HudBuilder.hudForPlayer(event.playerConnectEvent!!.playerRef)
//                .loadHtml(
//                    htmlFile,
//                    templateProcessor,
//                    UIType.HYWIND
//                )
            hudData.percentage = event.percentage
            hudData.hud.update(builder)
//            hud.loadHtml(htmlFile,
//                templateProcessor,
//                UIType.HYWIND)
//            hudBuilder.update(hud)
//            hudBuilder.show()
//
//            hudBuilder.update(build)

//            builder.append("Hud/Miilhozinho_ChunkGenerator_Progress.ui")
//
//            builder.set(
//                "#LabelChunkGenerator.Text",
//                String.format(
//                    "Progress: %.1f%% (%d/%d pos) (%d/%d chunks)",
//                    event.percentage,
//                    event.currentChunks,
//                    event.totalChunks,
//                    ChunkUtil.chunkCoordinate(event.currentChunks),
//                    ChunkUtil.chunkCoordinate(event.totalChunks)
//                )
//            )
//
//            val printBarQuantity = processBarWidth * barsToColor
//
//            builder.appendInline("#Bar",
//                "Group {\n" +
//                    "    LayoutMode: Left;\n" +
//                    "    Anchor: (Width: $printBarQuantity, Height: 25);\n" +
//                    "    Background: #46b447(0.7);\n" +
//                    "\n" +
//                    "}")
//            val player = event.playerConnectEvent!!.playerRef.holder!!.getComponent(Player.getComponentType())
//            MultipleHUD.getInstance().setCustomHud(player!!, event.playerConnectEvent!!.playerRef, "ChunkGenerator_HUD", this)
        }
//        build(builder)
    }

//    override fun build(builder: UICommandBuilder) {
//        update(true, builder)
//    }
}
