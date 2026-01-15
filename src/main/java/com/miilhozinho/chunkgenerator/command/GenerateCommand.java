package com.miilhozinho.chunkgenerator.command;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.console.ConsoleSender;
import com.miilhozinho.chunkgenerator.ChunkGenerator;
import com.miilhozinho.chunkgenerator.manager.GenerationManager;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;

import java.io.Console;
import java.util.concurrent.CompletableFuture;

public class GenerateCommand extends AbstractAsyncCommand {

    public GenerateCommand() {
        super("chunk-generate", "Manages chunk generation");
        this.addSubCommand(new RadiusCommand());
        this.addSubCommand(new StartCommand());
        this.addSubCommand(new PauseCommand());
        this.addSubCommand(new ResumeCommand());
    }

    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext commandContext) {
        CommandSender sender = commandContext.sender();
        if (sender instanceof Player player) {
            player.sendMessage(Message.raw("Use /chunk-generate radius <value>, start <x> <z>, pause, or resume"));
            return CompletableFuture.completedFuture(null);
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }

    private static class RadiusCommand extends AbstractAsyncCommand {

        private RequiredArg<Integer> radius;

        public RadiusCommand() {
            super("radius", "Sets the target generation radius");
            this.setPermissionGroup(GameMode.Adventure);
            this.radius = this.withRequiredArg("radius", "Sets the target generation radius", ArgTypes.INTEGER);
        }

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

    private static class StartCommand extends AbstractAsyncCommand {

        private RequiredArg<Integer> x;
        private RequiredArg<Integer> z;

        public StartCommand() {
            super("start", "Starts generation at the specified center");
            this.setPermissionGroup(GameMode.Adventure);
            this.x = this.withRequiredArg("x", "Sets the x target generation radius", ArgTypes.INTEGER);
            this.z = this.withRequiredArg("z", "Sets the z target generation radius", ArgTypes.INTEGER);
        }

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
                GenerationManager.getInstance().startGeneration(worldName, sender, chunkX, chunkZ);
            });
        }
    }


    private static class PauseCommand extends AbstractAsyncCommand {

        public PauseCommand() {
            super("pause", "Pauses generation");
        }

        @Override
        protected CompletableFuture<Void> executeAsync(CommandContext commandContext) {
            CommandSender sender = commandContext.sender();
            if (sender instanceof Player player) {
                GenerationManager.getInstance().pauseGeneration();
                player.sendMessage(Message.raw("Chunk generation paused"));
                return CompletableFuture.completedFuture(null);
            } else {
                return CompletableFuture.completedFuture(null);
            }
        }
    }

    private static class ResumeCommand extends AbstractAsyncCommand {

        public ResumeCommand() {
            super("resume", "Resumes generation");
        }

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
}
