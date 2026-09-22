package me.kodysimpson.permissions;

import org.bukkit.plugin.java.JavaPlugin;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.kodysimpson.permissions.commands.HealCommand;
import me.kodysimpson.permissions.commands.SpawnCommand;

public final class Permissions extends JavaPlugin {

    @Override
    public void onEnable() {
        registerCommand("heal", new HealCommand());

        // Register the Brigadier tree when Paper builds its commands.
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
                event.registrar().register(SpawnCommand.create())
        );
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
