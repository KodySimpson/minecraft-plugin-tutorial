package dev.kodysimpson.spawnprotection;

import org.bukkit.plugin.java.JavaPlugin;

public final class SpawnProtectionPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(
                new SpawnProtectionListener(),
                this
        );
    }
}
