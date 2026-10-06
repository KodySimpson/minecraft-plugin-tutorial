package dev.kodysimpson.kitpicker;

import org.bukkit.plugin.java.JavaPlugin;

public final class KitPickerPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // /kits takes no arguments, so a BasicCommand is all we need.
        // It passes the plugin on to each KitMenu, which uses it to create the inventory.
        registerCommand("kits", new KitsCommand(this));

        // The listener needs the plugin to schedule a task (see KitMenuListener).
        getServer().getPluginManager().registerEvents(new KitMenuListener(this), this);
    }
}
