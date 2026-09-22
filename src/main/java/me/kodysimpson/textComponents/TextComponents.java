package me.kodysimpson.textComponents;

import org.bukkit.plugin.java.JavaPlugin;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.kodysimpson.textComponents.commands.AnnounceCommand;

public final class TextComponents extends JavaPlugin {

    @Override
    public void onEnable() {
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(new AnnounceCommand().create());
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
