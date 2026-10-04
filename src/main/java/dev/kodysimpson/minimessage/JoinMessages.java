package dev.kodysimpson.minimessage;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class JoinMessages extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig();
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
                event.registrar().register(SnackCommand.create(this)));
    }
}
