package com.miilhozinho.chunkgenerator;

import com.miilhozinho.chunkgenerator.command.GenerateCommand;
import com.miilhozinho.chunkgenerator.config.ChunkGeneratorConfig;
import com.miilhozinho.chunkgenerator.manager.GenerationManager;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.events.AddWorldEvent;
import com.hypixel.hytale.server.core.universe.world.events.RemoveWorldEvent;
import com.hypixel.hytale.server.core.util.Config;

import java.util.concurrent.*;

import java.util.HashMap;
import java.util.logging.Level;

public class ChunkGenerator extends JavaPlugin {

    public static Config<ChunkGeneratorConfig> CONFIG;
    public static HashMap<String, World> WORLDS = new HashMap<>();

    public ChunkGenerator(JavaPluginInit init) {
        super(init);
        CONFIG = this.withConfig("ChunkGenerator", ChunkGeneratorConfig.CODEC);
    }

    @Override
    protected void setup() {
        super.setup();

        // Register the generation manager as a system or ticking entity
        // For now, we'll initialize it
        GenerationManager.getInstance();

        this.getCommandRegistry().registerCommand(new GenerateCommand());

        this.getEventRegistry().registerGlobal(AddWorldEvent.class, (event) -> {
            WORLDS.put(event.getWorld().getName(), event.getWorld());
            this.getLogger().at(Level.INFO).log("Registered world: " + event.getWorld().getName());
        });

        this.getEventRegistry().registerGlobal(RemoveWorldEvent.class, (event) -> {
            WORLDS.remove(event.getWorld().getName());
        });
    }
}
