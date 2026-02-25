package com.miilhozinho.chunkgenerator.data

import com.hypixel.hytale.server.core.HytaleServer
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.miilhozinho.chunkgenerator.events.BaseEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap


data class PlayerConnectEvent(val playerRef: PlayerRef, val player: Player)

object PlayerManager {
    private val eventBus = HytaleServer.get().eventBus

    private val playersOnline: MutableMap<UUID, PlayerConnectEvent> = ConcurrentHashMap()

    fun addPlayer(playerRef: PlayerRef, player: Player) {
        playersOnline[playerRef.uuid] = PlayerConnectEvent(playerRef, player)
    }

    fun removePlayer(playerUuid: UUID) {
        playersOnline.remove(playerUuid)
    }

    fun broadcastEvent(event: BaseEvent) {
        if (playersOnline.isEmpty())
            return

//        LogUtil.logInfo("Broadcast ${event.javaClass.name} to ${playersOnline.size} players")
        for (player in playersOnline){
            event.playerConnectEvent = player.value
            eventBus.dispatchFor(event.javaClass).dispatch(event)
        }
    }
}