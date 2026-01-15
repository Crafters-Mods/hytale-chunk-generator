package com.miilhozinho.chunkgenerator.util;

import com.hypixel.hytale.server.core.Constants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtils {

    public static String MAIN_PATH = Constants.UNIVERSE_PATH.resolve("ChunkGenerator").toAbsolutePath().toString();
    public static String SESSION_PATH = MAIN_PATH + File.separator + "session.json";

    public static void ensureMainDirectory(){
        var file = new File(MAIN_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static File ensureFile(String path, String defaultContent){
        var file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
                var writer = new FileWriter(file);
                writer.write(defaultContent);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

}
