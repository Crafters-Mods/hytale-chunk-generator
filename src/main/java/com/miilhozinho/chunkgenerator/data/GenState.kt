package com.miilhozinho.chunkgenerator.data

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.ExtraInfo
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec

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
            BuilderCodec.builder(GenState::class.java) { GenState() }
                .append(
                    KeyedCodec("WorldName", Codec.STRING),
                    { state: GenState?, value: String, _: ExtraInfo? ->
                        state!!.worldName = value
                    },
                    { state: GenState?, _: ExtraInfo? -> state!!.worldName }).add()
                .append(
                    KeyedCodec("CenterX", Codec.INTEGER),
                    { state: GenState?, value: Int?, _: ExtraInfo? -> state!!.centerX = value!! },
                    { state: GenState?, _: ExtraInfo? -> state!!.centerX }).add()
                .append(
                    KeyedCodec("CenterZ", Codec.INTEGER),
                    { state: GenState?, value: Int?, _: ExtraInfo? -> state!!.centerZ = value!! },
                    { state: GenState?, _: ExtraInfo? -> state!!.centerZ }).add()
                .append(
                    KeyedCodec("TargetRadius", Codec.INTEGER),
                    { state: GenState?, value: Int?, _: ExtraInfo? ->
                        state!!.targetRadius = value!!
                    },
                    { state: GenState?, _: ExtraInfo? -> state!!.targetRadius }).add()
                .append(
                    KeyedCodec("CurrentIndex", Codec.INTEGER),
                    { state: GenState?, value: Int?, _: ExtraInfo? ->
                        state!!.currentIndex = value!!
                    },
                    { state: GenState?, _: ExtraInfo? -> state!!.currentIndex }).add()
                .append(
                    KeyedCodec("IsPaused", Codec.BOOLEAN),
                    { state: GenState?, value: Boolean?, _: ExtraInfo? ->
                        state!!.isPaused = value!!
                    },
                    { state: GenState?, _: ExtraInfo? -> state!!.isPaused }).add()
                .build()
    }
}