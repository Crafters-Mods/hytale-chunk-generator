package com.miilhozinho.chunkgenerator.config

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.ExtraInfo
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.function.consumer.TriConsumer
import java.util.function.BiFunction
import java.util.function.Supplier

class ChunkGeneratorConfig {
    var maxChunksPerTick: Int = 5
        private set
    var tpsThreshold: Double = 18.0
        private set
    var saveIntervalChunks: Int = 100
        private set

    companion object {
        val CODEC: BuilderCodec<ChunkGeneratorConfig?> = BuilderCodec.builder<ChunkGeneratorConfig?>(
            ChunkGeneratorConfig::class.java,
            Supplier { ChunkGeneratorConfig() })
            .append<Int?>(
                KeyedCodec<Int?>("MaxChunksPerTick", Codec.INTEGER),
                { config: ChunkGeneratorConfig?, value: Int?, extraInfo: ExtraInfo? ->
                    config!!.maxChunksPerTick = value!!
                },
                { config: ChunkGeneratorConfig?, extraInfo: ExtraInfo? -> config!!.maxChunksPerTick }).add()
            .append<Double?>(
                KeyedCodec<Double?>("TpsThreshold", Codec.DOUBLE),
                { config: ChunkGeneratorConfig?, value: Double?, extraInfo: ExtraInfo? ->
                    config!!.tpsThreshold = value!!
                },
                { config: ChunkGeneratorConfig?, extraInfo: ExtraInfo? -> config!!.tpsThreshold }).add()
            .append<Int?>(
                KeyedCodec<Int?>("SaveIntervalChunks", Codec.INTEGER),
                { config: ChunkGeneratorConfig?, value: Int?, extraInfo: ExtraInfo? ->
                    config!!.saveIntervalChunks = value!!
                },
                { config: ChunkGeneratorConfig?, extraInfo: ExtraInfo? -> config!!.saveIntervalChunks })
            .add()
            .build()
    }
}