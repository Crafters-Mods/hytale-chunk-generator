package com.example.templateplugin;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import org.jetbrains.annotations.NotNull;

public class MyTemplatePlugin extends JavaPlugin {

    public MyTemplatePlugin(@NotNull JavaPluginInit init) {
        super(init);
    }

    public void onEnable() {
        System.out.println("[TemplatePlugin] My Hytale Plugin has been enabled!");
    }

    public void onDisable() {
        System.out.println("[TemplatePlugin] Goodbye, Hytale!");
    }
}