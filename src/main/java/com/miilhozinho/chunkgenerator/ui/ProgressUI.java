package com.miilhozinho.chunkgenerator.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class ProgressUI extends InteractiveCustomUIPage<ProgressUI.ProgressData> {

    public ProgressUI(PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, ProgressData.CODEC);
    }

    @Override
    public void handleDataEvent(Ref<EntityStore> ref, Store<EntityStore> store, ProgressData data) {
        super.handleDataEvent(ref, store, data);
        // Handle any events if needed
        this.sendUpdate();
    }

    @Override
    public void build(Ref<EntityStore> ref, UICommandBuilder uiCommandBuilder, UIEventBuilder uiEventBuilder, Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/miilhozinho_ChunkGenerator_Progress.ui");
        // Set initial values
        uiCommandBuilder.set("#ProgressText.Text", "0/0");
        uiCommandBuilder.set("#SpeedText.Text", "0 chunks/sec");
        uiCommandBuilder.set("#ETAText.Text", "00:00");
    }

    public void updateProgress(int current, int total, double speed, String eta) {
        // This would be called from GenerationManager to update the UI
        // For simplicity, assume we have a way to send update
    }

    public static class ProgressData {
        static final String KEY_UPDATE = "Update";

        @SuppressWarnings("deprecation")
        public static final BuilderCodec<ProgressData> CODEC = BuilderCodec.<ProgressData>builder(ProgressData.class, ProgressData::new)
                .addField(new KeyedCodec<>(KEY_UPDATE, Codec.STRING), (data, s) -> data.update = s, data -> data.update)
                .build();

        private String update;
    }
}
