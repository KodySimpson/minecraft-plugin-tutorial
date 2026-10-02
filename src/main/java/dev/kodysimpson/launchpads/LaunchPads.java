package dev.kodysimpson.launchpads;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class LaunchPads extends JavaPlugin {
    @Override
    public void onEnable() {
        // Same configuration workflow as Parts 11 and 12.
        saveDefaultConfig(); // Copy the bundled file only if the server copy is missing.
        getConfig(); // Load at startup; later getters read the in-memory configuration.

        getServer().getPluginManager().registerEvents(new LaunchListener(this), this);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
                event.registrar().register(LaunchCommand.create(this)));
    }
}
