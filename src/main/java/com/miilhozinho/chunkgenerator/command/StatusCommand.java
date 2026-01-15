package com.miilhozinho.chunkgenerator.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.miilhozinho.chunkgenerator.ui.ProgressUI;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class StatusCommand extends AbstractAsyncCommand {

    public StatusCommand() {
        super("status", "Status chunk generation");
        this.setPermissionGroup(GameMode.Adventure);
    }

    @Override
    protected @NotNull CompletableFuture<Void> executeAsync(@NotNull CommandContext commandContext) {
        var sender = commandContext.sender();
        if (sender instanceof Player player) {
            Ref<EntityStore> ref = player.getReference();
            if (ref != null && ref.isValid()) {
                try {
                    var holder = player.toHolder();
                    var playerRef = holder.getComponent(PlayerRef.getComponentType());
                    var progressUI = new ProgressUI(playerRef);
                    player.getHudManager().setCustomHud(playerRef, progressUI);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println();
                    throw new RuntimeException(e);
                }
            }

        }
        return null;
    }

//    @Override
//    protected void execute(@NotNull CommandContext commandContext,
//                           @NotNull Store<EntityStore> store,
//                           @NotNull Ref<EntityStore> ref,
//                           @NotNull PlayerRef playerRef,
//                           @NotNull World world) {
//
//        var playerComponent = store.getComponent(ref, Player.getComponentType());
//        var hudManager = playerComponent.getHudManager();
//        var progressBarHud = new ProgressUI(playerRef);
//        hudManager.setCustomHud(playerRef, progressBarHud);
//    }
}
