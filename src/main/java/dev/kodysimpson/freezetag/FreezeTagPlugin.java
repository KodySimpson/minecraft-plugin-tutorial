package dev.kodysimpson.freezetag;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class FreezeTagPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // Copies config.yml to the plugin folder the first time. getConfig() also keeps the copy
        // inside the jar as defaults, so a message the owner deletes falls back to ours.
        saveDefaultConfig();

        // One game, shared by the listener and the command.
        FreezeTagGame game = new FreezeTagGame(this);
        getServer().getPluginManager().registerEvents(new FreezeTagListener(game), this);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
                event.registrar().register(FreezeTagCommand.create(game), "Start or stop a round of Freeze Tag"));
    }

    // No onDisable needed: Paper cancels our timer when the plugin is disabled,
    // and the round is only kept in memory, so there's nothing to save or undo.
}
