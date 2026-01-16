package com.miilhozinho.chunkgenerator.command

import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand
import com.hypixel.hytale.server.core.entity.entities.Player
import com.miilhozinho.chunkgenerator.manager.GenerationManager
import java.util.concurrent.CompletableFuture

class ResumeCommand(val generationManager: GenerationManager) : AbstractAsyncCommand("resume", "Resumes generation") {
    override fun executeAsync(commandContext: CommandContext): CompletableFuture<Void?> {
        val sender = commandContext.sender()
        if (sender is Player) {
            generationManager.resumeGeneration(sender)
            sender.sendMessage(Message.raw("Chunk generation resumed"))
            return CompletableFuture.completedFuture<Void?>(null)
        } else {
            return CompletableFuture.completedFuture<Void?>(null)
        }
    }
}