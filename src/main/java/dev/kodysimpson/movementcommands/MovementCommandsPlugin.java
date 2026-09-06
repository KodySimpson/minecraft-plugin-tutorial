package dev.kodysimpson.movementcommands;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class MovementCommandsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Opening comparison: to run this version, uncomment it and disable the
        // SpawnCommand registration below so only one version owns /spawn.
        // registerCommand("spawn", new BasicSpawnCommand());

        // Brigadier trees are registered when Paper fires its command lifecycle event.
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(SpawnCommand.create());
            event.registrar().register(DayCommand.create());
        });
    }
}
