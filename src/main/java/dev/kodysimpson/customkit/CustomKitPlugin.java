package dev.kodysimpson.customkit;

import org.bukkit.plugin.java.JavaPlugin;

public final class CustomKitPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // /kit takes no arguments, so a BasicCommand is all we need.
        registerCommand("kit", new KitCommand());
    }
}
