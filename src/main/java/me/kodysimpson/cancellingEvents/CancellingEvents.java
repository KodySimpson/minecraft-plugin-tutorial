package me.kodysimpson.cancellingEvents;

import me.kodysimpson.cancellingEvents.listeners.EatingListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class CancellingEvents extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new EatingListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
