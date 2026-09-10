package dev.kodysimpson.admintoolkit;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class AdminToolkit extends JavaPlugin {

    @Override
    public void onEnable() {
        // Register the completed command tree during Paper's command lifecycle.
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
                event.registrar().register(
                        AdminCommand.create(),
                        "Heal players and change their game mode"
                )
        );
    }
}
