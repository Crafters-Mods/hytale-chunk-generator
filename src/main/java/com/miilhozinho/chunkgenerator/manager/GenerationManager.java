package com.miilhozinho.chunkgenerator.manager;

import com.google.gson.GsonBuilder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.types.RelativeChunkPosition;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.miilhozinho.chunkgenerator.ChunkGenerator;
import com.miilhozinho.chunkgenerator.data.GenState;
import com.miilhozinho.chunkgenerator.data.GenStateBlockingFile;
import com.miilhozinho.chunkgenerator.util.FileUtils;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.World;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.LongPredicate;
import java.util.logging.Level;
import com.hypixel.hytale.server.core.command.commands.world.chunk.ChunkLoadCommand;

import javax.annotation.Nonnull;

public class GenerationManager {
    @Nonnull
    private static final Message MESSAGE_COMMANDS_CHUNK_LOAD_ALREADY_LOADED = Message.translation("server.commands.chunk.load.alreadyLoaded");
    @Nonnull
    private static final Message MESSAGE_COMMANDS_CHUNK_LOAD_LOADING = Message.translation("server.commands.chunk.load.loading");
    @Nonnull
    private static final Message MESSAGE_COMMANDS_CHUNK_LOAD_LOADED = Message.translation("server.commands.chunk.load.loaded");

    private static final GenerationManager INSTANCE = new GenerationManager();

    private GenStateBlockingFile genStateFile;
    private HytaleLogger logger = HytaleLogger.getLogger().getSubLogger("ChunkGenerator");
    private ScheduledExecutorService scheduler;
    private Thread savingThread;
    private boolean isDirty = false;

    public static GenerationManager getInstance() {
        return INSTANCE;

    }

    private GenerationManager() {
        FileUtils.ensureMainDirectory();
        this.genStateFile = new GenStateBlockingFile();
        this.scheduler = Executors.newScheduledThreadPool(1);
        try {
            this.genStateFile.syncLoad();
        } catch (Exception e) {
            logger.at(Level.SEVERE).log("Failed to load generation state: " + e.getMessage());
        }

        this.savingThread = new Thread(() -> {
            while (true) {
                if (isDirty) {
                    isDirty = false;
                    try {
                        genStateFile.syncSave();
                        logger.at(Level.INFO).log("Saved generation state");
                    } catch (Exception e) {
                        logger.at(Level.SEVERE).log("Failed to save: " + e.getMessage());
                    }
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        this.savingThread.start();

        // Resume if not paused
        if (!genStateFile.getGenState().isPaused() && !genStateFile.getGenState().getWorldName().isEmpty()) {
            startGenerationInternal(null);
        }
    }

    public void startGeneration(String worldName, CommandSender player, int centerX, int centerZ) {
        // Use the current configured radius, default to 100 if not set
        int radius = genStateFile.getGenState().getTargetRadius();
        if (radius <= 0) {
            radius = 100; // Default radius
        }
        player.sendMessage(Message.raw("Started chunk generation at position (" + centerX + ", " + centerZ + ") with "+ radius +" radius"));
        GenState state = new GenState(worldName, centerX, centerZ, radius);
        genStateFile.setGenState(state);
        markDirty();
        startGenerationInternal(player);
    }

    public void setTargetRadius(int radius) {
        genStateFile.getGenState().setTargetRadius(radius);
        markDirty();
    }

    public void pauseGeneration() {
        genStateFile.getGenState().setPaused(true);
//        if (generationTask != null) {
//            generationTask.cancel();
//            generationTask = null;
//        }
        markDirty();
    }

    public void resumeGeneration(Player player) {
        genStateFile.getGenState().setPaused(false);
        markDirty();
        startGenerationInternal(player);
    }

    private void startGenerationInternal(CommandSender player) {
        GenState state = genStateFile.getGenState();
        if (state.isPaused() || state.getWorldName().isEmpty()) return;

        World world = ChunkGenerator.WORLDS.get(state.getWorldName());
        if (world == null) return;

        // Schedule the generation task using Java's ScheduledExecutorService
        scheduler.scheduleAtFixedRate(() -> processGeneration(world, player), 0, 1, TimeUnit.SECONDS);
    }

    private void processGeneration(World world, CommandSender player) {
        GenState state = genStateFile.getGenState();

        if (state.isPaused()) {
            return;
        }

        // Calculate total chunks for percentage display
        int radius = state.getTargetRadius();
        long totalChunks = (2L * radius + 1) * (2L * radius + 1);

        // TODO: Replace with Hytale TPS monitoring API
        // Example: double tps = Server.getTPS(); or world.getServer().getTPS();
        double tps = 20.0; // Placeholder - assume good TPS

        // TODO: Replace with your config access
        // int maxChunks = (tps < ChunkGenerator.CONFIG.get().getTpsThreshold()) ? 1 : ChunkGenerator.CONFIG.get().getMaxChunksPerTick();
        int maxChunks = (tps < 18.0) ? 1 : 5; // Placeholder values

        for (int i = 0; i < maxChunks; i++) {
            int[] coords = getSpiralCoordinates(state.getCurrentIndex());

            int chunkX = state.getCenterX() + coords[0];
            int chunkZ = state.getCenterZ() + coords[1];

            // Check radius
            int dist = Math.max(Math.abs(coords[0]), Math.abs(coords[1]));
            if (dist > state.getTargetRadius()) {
                pauseGeneration();
                logger.at(Level.INFO).log("Generation completed for radius " + state.getTargetRadius());
                return;
            }

            try {
                ChunkStore chunkStore = world.getChunkStore();
                long chunkIndex = ChunkUtil.indexChunk(chunkX, chunkZ);
                Ref<ChunkStore> chunkRef = chunkStore.getChunkReference(chunkIndex);
                if (chunkRef != null && chunkRef.isValid()) {
                    logger.at(Level.INFO).log("Já existe "+ chunkX + " " + chunkZ);
                } else {
                    world.getChunkAsync(chunkX, chunkZ).thenAccept((worldChunk) -> world.execute(() -> {
                        worldChunk.markNeedsSaving();
//                        logger.at(Level.INFO).log("Carregado "+ chunkX + " " + chunkZ);
                    }));
                }

            } catch (Exception e) {
                logger.at(Level.SEVERE).log("Failed to generate chunk " + chunkX + "," + chunkZ + ": " + e.getMessage());
            }
            long currentIndex = state.getCurrentIndex();
            if (currentIndex % 50 == 0) {
                double percentage = (currentIndex * 100.0) / totalChunks;
                player.sendMessage(Message.raw(String.format("Progress: %.1f%% (%d/%d pos) (%d/%d chunks)", percentage, currentIndex, totalChunks, ChunkUtil.chunkCoordinate(currentIndex), ChunkUtil.chunkCoordinate(totalChunks))));
            }

            state.setCurrentIndex(state.getCurrentIndex() + 1);

            // Save progress every 100 chunks
            if (state.getCurrentIndex() % 100 == 0) {
                markDirty();
            }
        }

        // Save progress at the end of each batch
        markDirty();
    }

    private int[] getSpiralCoordinates(int index) {
        if (index == 0) return new int[]{0, 0};

        int x = 0, y = 0;
        int dx = 0, dy = -1;
        for (int i = 0; i < index; i++) {
            if (x == y || (x < 0 && x == -y) || (x > 0 && x == 1 - y)) {
                int temp = dx;
                dx = -dy;
                dy = temp;
            }
            x += dx;
            y += dy;
        }
        return new int[]{x, y};
    }

    private void markDirty() {
        isDirty = true;
    }
}
