package me.kodysimpson.configtutorial;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.kodysimpson.configtutorial.commands.SnackCommand;
import me.kodysimpson.configtutorial.listeners.JoinListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConfigTutorial extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig(); //creates it if it doesnt exist
        getConfig(); //load the values

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(SnackCommand.create(this));
        });

        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
