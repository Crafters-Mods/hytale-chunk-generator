package com.miilhozinho.chunkgenerator.command

import com.hypixel.hytale.protocol.GameMode
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.CommandSender
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand
import com.hypixel.hytale.server.core.console.ConsoleSender
import com.hypixel.hytale.server.core.entity.entities.Player
import com.miilhozinho.chunkgenerator.manager.GenerationManager
import java.util.concurrent.CompletableFuture


class StartCommand(val generationManager: GenerationManager) : AbstractAsyncCommand("start", "Starts generation at the specified center") {
    private val x: RequiredArg<Int?>
    private val z: RequiredArg<Int?>

    init {
        this.setPermissionGroup(GameMode.Adventure)
        this.x = this.withRequiredArg<Int?>("x", "Sets the x target generation radius", ArgTypes.INTEGER)
        this.z = this.withRequiredArg<Int?>("z", "Sets the z target generation radius", ArgTypes.INTEGER)
    }

    override fun executeAsync(commandContext: CommandContext): CompletableFuture<Void?> {
        val sender = commandContext.sender()
        if (sender is ConsoleSender) {
            return execute(commandContext, "default", sender)
        }
        if (sender is Player) {
            val ref = sender.getReference()
            if (ref != null && ref.isValid) {
                val store = ref.getStore()
                val world = store.getExternalData().world
                return execute(commandContext, world.name, sender)
            }
        }
        return CompletableFuture.completedFuture<Void?>(null)
    }

    private fun execute(
        commandContext: CommandContext,
        worldName: String,
        sender: CommandSender
    ): CompletableFuture<Void?> {
        return CompletableFuture.runAsync(Runnable {
            val chunkX: Int = this.x.get(commandContext)!!
            val chunkZ: Int = this.z.get(commandContext)!!
            generationManager.startGeneration(worldName, sender, chunkX, chunkZ)
        })
    }
}