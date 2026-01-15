package com.miilhozinho.chunkgenerator.command;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.miilhozinho.chunkgenerator.manager.GenerationManager;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ResumeCommand extends AbstractAsyncCommand {

    public ResumeCommand() {
        super("resume", "Resumes generation");
    }

    @NotNull
    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext commandContext) {
        CommandSender sender = commandContext.sender();
        if (sender instanceof Player player) {
            GenerationManager.getInstance().resumeGeneration(player);
            player.sendMessage(Message.raw("Chunk generation resumed"));
            return CompletableFuture.completedFuture(null);
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }
}