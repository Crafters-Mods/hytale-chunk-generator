package com.miilhozinho.chunkgenerator.ui;

import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.miilhozinho.chunkgenerator.events.ProgressUpdateEvent;
import com.miilhozinho.chunkgenerator.manager.GenerationManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ProgressUI extends CustomUIHud {

    private static boolean canDisplay = true;
    private ProgressUpdateEvent latestEvent;

    public ProgressUI(PlayerRef playerRef) {
        super(playerRef);

        // Register listener that receives uiCommandBuilder directly
        GenerationManager.getInstance().addProgressListener(event -> {
            this.latestEvent = event;
            updateProgressBars(event);
        });
    }

    @Override
    public void build(@NotNull UICommandBuilder uiCommandBuilder) {
        if (canDisplay) {
            // UI is being displayed - register as progress listener
            uiCommandBuilder.append("Hud/Miilhozinho_ChunkGenerator_Progress.ui");

            // Build with current/latest progress
            if (latestEvent != null) {
                updateProgressBars(latestEvent);
            }
        }
        canDisplay = !canDisplay;
    }

    public void updateProgressBars(ProgressUpdateEvent event) {
        // Calculate how many bars should be green (each bar represents 10%)
        System.out.println("Atualizou: " + event.percentage());
        int barsToColor = (int) Math.floor(event.percentage() / 10.0);
        var uiCommandBuilder = new UICommandBuilder();
        if (event.percentage() == 100){
            update(true, uiCommandBuilder);
            return;
        }
        uiCommandBuilder.append("Hud/Miilhozinho_ChunkGenerator_Progress.ui");
        uiCommandBuilder.set("#LabelChunkGenerator.Text", String.format("Progress: %.1f%% (%d/%d pos) (%d/%d chunks)", event.percentage(), event.currentChunks(), event.totalChunks(), ChunkUtil.chunkCoordinate(event.currentChunks()), ChunkUtil.chunkCoordinate(event.totalChunks())));

        // Update each bar's color
        for (int i = 1; i <= 10; i++) {
            String barId = "#Bar" + i;
            if (i <= barsToColor) {
                // Green for completed bars
                uiCommandBuilder.set(barId + ".Background.Color", "#46b447");
            } else {
                // Dark for incomplete bars
                uiCommandBuilder.set(barId + ".Background.Color", "#1a1a2e");
            }
        }
        update(true, uiCommandBuilder);
    }
}
