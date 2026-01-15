package com.miilhozinho.chunkgenerator.command;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.miilhozinho.chunkgenerator.manager.GenerationManager;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class RadiusCommand extends AbstractAsyncCommand {

    private RequiredArg<Integer> radius;

    public RadiusCommand() {
        super("radius", "Sets the target generation radius");
        this.setPermissionGroup(GameMode.Adventure);
        this.radius = this.withRequiredArg("radius", "Sets the target generation radius", ArgTypes.INTEGER);
    }

    @NotNull
    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext commandContext) {
        CommandSender sender = commandContext.sender();
        if (sender instanceof Player player) {
            int radiusValue = this.radius.get(commandContext);
            GenerationManager.getInstance().setTargetRadius(radiusValue);
            player.sendMessage(Message.raw("Target generation radius set to " + radiusValue));
            return CompletableFuture.completedFuture(null);
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }
}