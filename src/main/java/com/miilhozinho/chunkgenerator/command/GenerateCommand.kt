package com.miilhozinho.chunkgenerator.command

import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand
import com.hypixel.hytale.server.core.entity.entities.Player
import com.miilhozinho.chunkgenerator.manager.GenerationManager
import java.util.concurrent.CompletableFuture

class GenerateCommand(generationManager: GenerationManager) : AbstractAsyncCommand("chunk-generator", "Manages chunk generation") {
    init {
        this.addSubCommand(RadiusCommand(generationManager))
        this.addSubCommand(StartCommand(generationManager))
        this.addSubCommand(PauseCommand(generationManager))
        this.addSubCommand(ResumeCommand(generationManager))
    }

    override fun executeAsync(commandContext: CommandContext): CompletableFuture<Void?> {
        commandContext.sendMessage(Message.raw("Use /chunk-generator radius <value>, start <x> <z>, pause, or resume"))
        return CompletableFuture.completedFuture<Void?>(null)
    }
}