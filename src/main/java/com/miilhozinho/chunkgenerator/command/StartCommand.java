package com.miilhozinho.chunkgenerator.command;


import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.miilhozinho.chunkgenerator.manager.GenerationManager;
import com.miilhozinho.chunkgenerator.ui.ProgressUI;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class StartCommand extends AbstractAsyncCommand {

    private RequiredArg<Integer> x;
    private RequiredArg<Integer> z;

    public StartCommand() {
        super("start", "Starts generation at the specified center");
        this.setPermissionGroup(GameMode.Adventure);
        this.x = this.withRequiredArg("x", "Sets the x target generation radius", ArgTypes.INTEGER);
        this.z = this.withRequiredArg("z", "Sets the z target generation radius", ArgTypes.INTEGER);
    }

    @NotNull
    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext commandContext) {

        CommandSender sender = commandContext.sender();
        if (sender instanceof ConsoleSender consoleSender){
            execute(commandContext, "default", sender);
        }
        if (sender instanceof Player player) {
            Ref<EntityStore> ref = player.getReference();
            if (ref != null && ref.isValid()) {
                Store<EntityStore> store = ref.getStore();
                World world = store.getExternalData().getWorld();
                var holder = player.toHolder();
                var playerRef = holder.getComponent(PlayerRef.getComponentType());
                var progressUI = new ProgressUI(playerRef);
                execute(commandContext, world.getName(), sender);
            }
            return CompletableFuture.completedFuture(null);
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }
    private CompletableFuture<Void> execute(CommandContext commandContext, String worldName, CommandSender sender){

        return CompletableFuture.runAsync(() -> {
//                            int chunkX = ChunkUtil.chunkCoordinate(this.x.get(commandContext));
//                            int chunkZ = ChunkUtil.chunkCoordinate(this.z.get(commandContext));
            int chunkX = this.x.get(commandContext);
            int chunkZ = this.z.get(commandContext);
            var generationManager = GenerationManager.getInstance();
            generationManager.startGeneration(worldName, sender, chunkX, chunkZ);
        });
    }
}