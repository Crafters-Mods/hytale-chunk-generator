package com.miilhozinho.chunkgenerator.data

import com.hypixel.hytale.server.core.HytaleServer
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.miilhozinho.chunkgenerator.events.BaseEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap


object PlayerManager {
    private val eventBus = HytaleServer.get().eventBus

    private val playersOnline: MutableMap<UUID, PlayerRef> = ConcurrentHashMap()

    fun addPlayer(player: PlayerRef) {
        playersOnline[player.uuid] = player
    }

    fun removePlayer(playerUuid: UUID) {
        playersOnline.remove(playerUuid)
    }

    fun broadcastEvent(event: BaseEvent) {
        if (playersOnline.isEmpty())
            return

//        LogUtil.logInfo("Broadcast ${event.javaClass.name} to ${playersOnline.size} players")
        for (player in playersOnline){
            event.playerRef = player.value
            eventBus.dispatchFor(event.javaClass).dispatch(event)
        }
    }
}