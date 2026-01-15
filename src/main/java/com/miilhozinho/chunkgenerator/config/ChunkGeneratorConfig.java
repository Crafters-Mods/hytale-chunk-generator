package com.miilhozinho.chunkgenerator.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class ChunkGeneratorConfig {

    public static final BuilderCodec<ChunkGeneratorConfig> CODEC = BuilderCodec.builder(ChunkGeneratorConfig.class, ChunkGeneratorConfig::new)
            .append(new KeyedCodec<Integer>("MaxChunksPerTick", Codec.INTEGER),
                    (config, value, extraInfo) -> config.MaxChunksPerTick = value,
                    (config, extraInfo) -> config.MaxChunksPerTick).add()
            .append(new KeyedCodec<Double>("TpsThreshold", Codec.DOUBLE),
                    (config, value, extraInfo) -> config.TpsThreshold = value,
                    (config, extraInfo) -> config.TpsThreshold).add()
            .append(new KeyedCodec<Integer>("SaveIntervalChunks", Codec.INTEGER),
                    (config, value, extraInfo) -> config.SaveIntervalChunks = value,
                    (config, extraInfo) -> config.SaveIntervalChunks).add()
            .build();

    private int MaxChunksPerTick = 5;
    private double TpsThreshold = 18.0;
    private int SaveIntervalChunks = 100;

    public ChunkGeneratorConfig() {
    }

    public int getMaxChunksPerTick() {
        return MaxChunksPerTick;
    }

    public double getTpsThreshold() {
        return TpsThreshold;
    }

    public int getSaveIntervalChunks() {
        return SaveIntervalChunks;
    }
}
