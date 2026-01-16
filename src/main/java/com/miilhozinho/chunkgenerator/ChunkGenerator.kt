package com.miilhozinho.chunkgenerator

import com.hypixel.hytale.server.core.HytaleServer
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.events.AddWorldEvent
import com.hypixel.hytale.server.core.universe.world.events.RemoveWorldEvent
import com.hypixel.hytale.server.core.util.Config
import com.miilhozinho.chunkgenerator.command.GenerateCommand
import com.miilhozinho.chunkgenerator.config.ChunkGeneratorConfig
import com.miilhozinho.chunkgenerator.data.PlayerManager.addPlayer
import com.miilhozinho.chunkgenerator.data.PlayerManager.removePlayer
import com.miilhozinho.chunkgenerator.events.ProgressUpdatedEvent
import com.miilhozinho.chunkgenerator.manager.GenerationManager
import com.miilhozinho.chunkgenerator.ui.ProgressUpdatedEventHandler
import java.util.function.Consumer
import java.util.logging.Level

class ChunkGenerator(init: JavaPluginInit) : JavaPlugin(init) {
    init {
        CONFIG = this.withConfig<ChunkGeneratorConfig?>("ChunkGenerator", ChunkGeneratorConfig.CODEC)
    }

    override fun setup() {
        super.setup()
        val generationManager = GenerationManager()

        this.commandRegistry.registerCommand(GenerateCommand(generationManager))

        this.eventRegistry
            .registerGlobal<String?, AddWorldEvent?>(AddWorldEvent::class.java, Consumer { event: AddWorldEvent? ->
                WORLDS.put(event!!.world.name, event.world)
                this.logger.at(Level.INFO).log("Registered world: " + event.world.name)
            })

        this.eventRegistry.registerGlobal<String?, RemoveWorldEvent?>(
            RemoveWorldEvent::class.java,
            Consumer { event: RemoveWorldEvent? ->
                WORLDS.remove(event!!.world.name)
            })

        registerServiceBusEvents()
    }

    fun registerServiceBusEvents() {
        val eventBus = HytaleServer.get().eventBus

        eventBus
            .register<PlayerConnectEvent?>(PlayerConnectEvent::class.java, Consumer { event: PlayerConnectEvent? ->
                addPlayer(event!!.playerRef)
            })

        eventBus
            .register<PlayerDisconnectEvent?>(PlayerDisconnectEvent::class.java, Consumer { event: PlayerDisconnectEvent? ->
                removePlayer(event!!.getPlayerRef().uuid)
            })

        eventBus
            .register<ProgressUpdatedEvent>(ProgressUpdatedEvent::class.java, { event: ProgressUpdatedEvent ->
                ProgressUpdatedEventHandler(event.playerRef!!).handle(event)
            })
    }

    companion object {
        var CONFIG: Config<ChunkGeneratorConfig?>? = null
        var WORLDS: HashMap<String?, World?> = HashMap<String?, World?>()
    }
}