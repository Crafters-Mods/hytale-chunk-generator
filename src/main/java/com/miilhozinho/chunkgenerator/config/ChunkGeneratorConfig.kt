package com.miilhozinho.chunkgenerator.config

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.ExtraInfo
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec

class ChunkGeneratorConfig {
    var maxChunksPerTick: Int = 5
        private set
    var tpsThreshold: Double = 18.0
        private set
    var saveIntervalChunks: Int = 100
        private set

    companion object {
        val CODEC: BuilderCodec<ChunkGeneratorConfig?> = BuilderCodec.builder(
            ChunkGeneratorConfig::class.java
        ) { ChunkGeneratorConfig() }
            .append(
                KeyedCodec("MaxChunksPerTick", Codec.INTEGER),
                { config: ChunkGeneratorConfig?, value: Int?, _: ExtraInfo? ->
                    config!!.maxChunksPerTick = value!!
                },
                { config: ChunkGeneratorConfig?, _: ExtraInfo? -> config!!.maxChunksPerTick }).add()
            .append(
                KeyedCodec("TpsThreshold", Codec.DOUBLE),
                { config: ChunkGeneratorConfig?, value: Double?, _: ExtraInfo? ->
                    config!!.tpsThreshold = value!!
                },
                { config: ChunkGeneratorConfig?, _: ExtraInfo? -> config!!.tpsThreshold }).add()
            .append(
                KeyedCodec("SaveIntervalChunks", Codec.INTEGER),
                { config: ChunkGeneratorConfig?, value: Int?, _: ExtraInfo? ->
                    config!!.saveIntervalChunks = value!!
                },
                { config: ChunkGeneratorConfig?, _: ExtraInfo? -> config!!.saveIntervalChunks })
            .add()
            .build()
    }
}