package dev.kodysimpson.welcomemessages;

import org.bukkit.plugin.java.JavaPlugin;

public final class WelcomeMessages extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ConnectionListener(), this);
    }
}
