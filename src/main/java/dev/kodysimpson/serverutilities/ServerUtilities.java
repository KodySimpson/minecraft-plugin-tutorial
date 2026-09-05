package dev.kodysimpson.serverutilities;

import org.bukkit.plugin.java.JavaPlugin;

public final class ServerUtilities extends JavaPlugin {

    @Override
    public void onEnable() {
        // Basic commands can be registered directly without a commands section in plugin.yml.
        registerCommand("heal", new HealCommand());
        registerCommand("serverinfo", new ServerInfoCommand(this));
        registerCommand("potato", new PotatoCommand());

        // The command creates the potato; this listener reacts when someone picks it up.
        getServer().getPluginManager().registerEvents(new PotatoListener(), this);
    }
}
