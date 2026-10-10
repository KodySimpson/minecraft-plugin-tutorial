package dev.kodysimpson.cooldowns;

import org.bukkit.plugin.java.JavaPlugin;

public final class CooldownsPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // /smite takes no arguments, so a BasicCommand is all we need.
        registerCommand("smite", new SmiteCommand());

        // Example 2: the same kind of cooldown, on a listener.
        // It needs the plugin to schedule the task that drains its boss bar.
        getServer().getPluginManager().registerEvents(new SnowballWandListener(this), this);
    }
}
