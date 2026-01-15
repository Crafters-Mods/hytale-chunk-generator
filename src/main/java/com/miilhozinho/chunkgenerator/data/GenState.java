package com.miilhozinho.chunkgenerator.data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class GenState {

    public static final BuilderCodec<GenState> CODEC = BuilderCodec.builder(GenState.class, GenState::new)
            .append(new KeyedCodec<String>("WorldName", Codec.STRING),
                    (state, value, extraInfo) -> state.WorldName = value,
                    (state, extraInfo) -> state.WorldName).add()
            .append(new KeyedCodec<Integer>("CenterX", Codec.INTEGER),
                    (state, value, extraInfo) -> state.centerX = value,
                    (state, extraInfo) -> state.centerX).add()
            .append(new KeyedCodec<Integer>("CenterZ", Codec.INTEGER),
                    (state, value, extraInfo) -> state.centerZ = value,
                    (state, extraInfo) -> state.centerZ).add()
            .append(new KeyedCodec<Integer>("TargetRadius", Codec.INTEGER),
                    (state, value, extraInfo) -> state.targetRadius = value,
                    (state, extraInfo) -> state.targetRadius).add()
            .append(new KeyedCodec<Integer>("CurrentIndex", Codec.INTEGER),
                    (state, value, extraInfo) -> state.currentIndex = value,
                    (state, extraInfo) -> state.currentIndex).add()
            .append(new KeyedCodec<Boolean>("IsPaused", Codec.BOOLEAN),
                    (state, value, extraInfo) -> state.isPaused = value,
                    (state, extraInfo) -> state.isPaused).add()
            .build();

    private String WorldName = "";
    private int centerX = 0;
    private int centerZ = 0;
    private int targetRadius = 0;
    private int currentIndex = 0;
    private boolean isPaused = true;

    public GenState() {
    }

    public GenState(String worldName, int centerX, int centerZ, int targetRadius) {
        this.WorldName = worldName;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.targetRadius = targetRadius;
        this.currentIndex = 0;
        this.isPaused = false;
    }

    public String getWorldName() {
        return WorldName;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterZ() {
        return centerZ;
    }

    public int getTargetRadius() {
        return targetRadius;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public void setTargetRadius(int targetRadius) {
        this.targetRadius = targetRadius;
    }
}
