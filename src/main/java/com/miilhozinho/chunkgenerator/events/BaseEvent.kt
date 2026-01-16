package com.miilhozinho.chunkgenerator.events

import com.hypixel.hytale.event.IEvent
import com.hypixel.hytale.server.core.universe.PlayerRef
import java.time.Instant
import java.util.UUID

open class BaseEvent : IEvent<Void> {
    val eventId: UUID = UUID.randomUUID()
    val occurredAt: Instant = Instant.now()
    var playerRef: PlayerRef? = null

    fun log(): String{
        return "[$occurredAt] [$eventId] [${playerRef?.username}] -"
    }
}