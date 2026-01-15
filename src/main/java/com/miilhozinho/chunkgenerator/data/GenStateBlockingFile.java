package com.miilhozinho.chunkgenerator.data;

import com.hypixel.hytale.logger.HytaleLogger;
import com.miilhozinho.chunkgenerator.util.FileUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hypixel.hytale.server.core.util.io.BlockingDiskFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;

public class GenStateBlockingFile extends BlockingDiskFile {

    private GenState genState;
    private HytaleLogger logger = HytaleLogger.getLogger().getSubLogger("ChunkGenerator");

    public GenStateBlockingFile() {
        super(Path.of(FileUtils.SESSION_PATH));
        this.genState = new GenState();
    }

    @Override
    protected void read(BufferedReader bufferedReader) throws IOException {
        var rootElement = JsonParser.parseReader(bufferedReader);
        if (rootElement == null || !rootElement.isJsonObject()) return;
        var root = rootElement.getAsJsonObject();
        this.genState = new GenState(
                root.get("WorldName").getAsString(),
                root.get("CenterX").getAsInt(),
                root.get("CenterZ").getAsInt(),
                root.get("TargetRadius").getAsInt()
        );
        this.genState.setCurrentIndex(root.get("currentIndex").getAsInt());
        this.genState.setPaused(root.get("isPaused").getAsBoolean());
    }

    @Override
    protected void write(BufferedWriter bufferedWriter) throws IOException {
        JsonObject root = new JsonObject();
        root.addProperty("WorldName", genState.getWorldName());
        root.addProperty("CenterX", genState.getCenterX());
        root.addProperty("CenterZ", genState.getCenterZ());
        root.addProperty("TargetRadius", genState.getTargetRadius());
        root.addProperty("CurrentIndex", genState.getCurrentIndex());
        root.addProperty("IsPaused", genState.isPaused());
        bufferedWriter.write(root.toString());
    }

    @Override
    protected void create(BufferedWriter bufferedWriter) throws IOException {
        this.genState = new GenState();
        write(bufferedWriter);
    }

    public GenState getGenState() {
        return genState;
    }

    public void setGenState(GenState genState) {
        this.genState = genState;
    }
}
