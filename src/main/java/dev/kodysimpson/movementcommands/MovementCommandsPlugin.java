package dev.kodysimpson.movementcommands;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class MovementCommandsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Optional comparison: use this instead of the HealCommand tree below.
        // registerCommand("heal", new BasicHealCommand());

        // Reuse Part 6's registration pattern; this lesson focuses on typed input.
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(HealCommand.create());
            event.registrar().register(MovementCommand.create());
            event.registrar().register(GlowCommand.create());
            event.registrar().register(AnnounceCommand.create());
        });
    }
}
