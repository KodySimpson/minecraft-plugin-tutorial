package dev.kodysimpson.serverutilities;

import org.bukkit.plugin.java.JavaPlugin;

public final class ServerUtilities extends JavaPlugin {

    @Override
    public void onEnable() {
        registerCommand("heal", new HealCommand());
        registerCommand("serverinfo", new ServerInfoCommand(this));
    }
}
