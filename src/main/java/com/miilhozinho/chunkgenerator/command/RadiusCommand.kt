package com.miilhozinho.chunkgenerator.command

import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand
import com.hypixel.hytale.server.core.entity.entities.Player
import com.miilhozinho.chunkgenerator.manager.GenerationManager
import java.util.concurrent.CompletableFuture

class RadiusCommand(val generationManager: GenerationManager) : AbstractAsyncCommand("radius", "Sets the target generation radius") {
    private val radius: RequiredArg<Int?>

    init {
        this.requirePermission("chunkgenerator.command.radius")
        this.radius = this.withRequiredArg("radius", "Sets the target generation radius", ArgTypes.INTEGER)
    }

    override fun executeAsync(commandContext: CommandContext): CompletableFuture<Void?> {
        val sender = commandContext.sender()
        if (sender is Player) {
            val radiusValue: Int = this.radius.get(commandContext)!!
            generationManager.setTargetRadius(radiusValue)
            sender.sendMessage(Message.raw("Target generation radius set to $radiusValue"))
            return CompletableFuture.completedFuture<Void?>(null)
        } else {
            return CompletableFuture.completedFuture<Void?>(null)
        }
    }
}