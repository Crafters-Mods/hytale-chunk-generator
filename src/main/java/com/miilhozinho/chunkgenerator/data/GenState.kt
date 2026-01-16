package com.miilhozinho.chunkgenerator.data

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.ExtraInfo
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.function.consumer.TriConsumer
import java.util.function.BiFunction
import java.util.function.Supplier

class GenState {
    var worldName: String = ""
        private set
    var centerX: Int = 0
        private set
    var centerZ: Int = 0
        private set
    private var targetRadius = 0
    private var currentIndex = 0
    private var isPaused = true

    constructor()

    constructor(worldName: String, centerX: Int, centerZ: Int, targetRadius: Int) {
        this.worldName = worldName
        this.centerX = centerX
        this.centerZ = centerZ
        this.targetRadius = targetRadius
        this.currentIndex = 0
        this.isPaused = false
    }

    fun getTargetRadius(): Int {
        return targetRadius
    }

    fun getCurrentIndex(): Int {
        return currentIndex
    }

    fun setCurrentIndex(currentIndex: Int) {
        this.currentIndex = currentIndex
    }

    fun isPaused(): Boolean {
        return isPaused
    }

    fun setPaused(paused: Boolean) {
        isPaused = paused
    }

    fun setTargetRadius(targetRadius: Int) {
        this.targetRadius = targetRadius
    }

    companion object {
        val CODEC: BuilderCodec<GenState?> =
            BuilderCodec.builder<GenState?>(GenState::class.java, Supplier { GenState() })
                .append<String?>(
                    KeyedCodec<String>("WorldName", Codec.STRING),
                    { state: GenState?, value: String, extraInfo: ExtraInfo? ->
                        state!!.worldName = value
                    },
                    { state: GenState?, extraInfo: ExtraInfo? -> state!!.worldName }).add()
                .append<Int?>(
                    KeyedCodec<Int?>("CenterX", Codec.INTEGER),
                    { state: GenState?, value: Int?, extraInfo: ExtraInfo? -> state!!.centerX = value!! },
                    { state: GenState?, extraInfo: ExtraInfo? -> state!!.centerX }).add()
                .append<Int?>(
                    KeyedCodec<Int?>("CenterZ", Codec.INTEGER),
                    { state: GenState?, value: Int?, extraInfo: ExtraInfo? -> state!!.centerZ = value!! },
                    { state: GenState?, extraInfo: ExtraInfo? -> state!!.centerZ }).add()
                .append<Int?>(
                    KeyedCodec<Int?>("TargetRadius", Codec.INTEGER),
                    { state: GenState?, value: Int?, extraInfo: ExtraInfo? ->
                        state!!.targetRadius = value!!
                    },
                    { state: GenState?, extraInfo: ExtraInfo? -> state!!.targetRadius }).add()
                .append<Int?>(
                    KeyedCodec<Int?>("CurrentIndex", Codec.INTEGER),
                    { state: GenState?, value: Int?, extraInfo: ExtraInfo? ->
                        state!!.currentIndex = value!!
                    },
                    { state: GenState?, extraInfo: ExtraInfo? -> state!!.currentIndex }).add()
                .append<Boolean?>(
                    KeyedCodec<Boolean?>("IsPaused", Codec.BOOLEAN),
                    { state: GenState?, value: Boolean?, extraInfo: ExtraInfo? ->
                        state!!.isPaused = value!!
                    },
                    { state: GenState?, extraInfo: ExtraInfo? -> state!!.isPaused }).add()
                .build()
    }
}