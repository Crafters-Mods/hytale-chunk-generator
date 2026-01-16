package com.miilhozinho.chunkgenerator.events

class ProgressUpdatedEvent(
    var percentage: Double,
    var currentChunks: Long,
    var totalChunks: Long,
    var enabled: Boolean = true) : BaseEvent()