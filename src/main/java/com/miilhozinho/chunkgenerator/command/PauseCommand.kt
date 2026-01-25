package com.miilhozinho.chunkgenerator.command

import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.CommandSender
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand
import com.hypixel.hytale.server.core.entity.entities.Player
import com.miilhozinho.chunkgenerator.manager.GenerationManager
import java.util.concurrent.CompletableFuture

class PauseCommand(val generationManager: GenerationManager) : AbstractAsyncCommand("pause", "Pauses generation") {
    init {
        this.requirePermission("chunkgenerator.command.start")
    }

    override fun executeAsync(commandContext: CommandContext): CompletableFuture<Void?> {
        val sender = commandContext.sender()
        if (sender is Player) {
            val ref = sender.getReference()
            if (ref != null && ref.isValid)
                pause(sender)
        } else
            pause(sender)

        return CompletableFuture.completedFuture<Void?>(null)
    }

    private fun pause(sender: CommandSender) {
        generationManager.pauseGeneration()
        sender.sendMessage(Message.raw("Chunk generation paused"))
    }
}