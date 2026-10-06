package dev.kodysimpson.scheduling;

import dev.kodysimpson.scheduling.commands.CountdownCommand;
import dev.kodysimpson.scheduling.commands.ReminderCommand;
import dev.kodysimpson.scheduling.commands.RepeatCommand;
import dev.kodysimpson.scheduling.commands.ServerReportCommand;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class SchedulingPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // The reminder has a typed argument, so register its tree as in earlier episodes.
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(ReminderCommand.create(this));
        });
        // These commands need no arguments: BasicCommand keeps them small.
        registerCommand("repeat", new RepeatCommand(this));
        registerCommand("countdown", new CountdownCommand(this));
        // Teach this last, after explaining the main thread and why slow work can block it.
        registerCommand("serverreport", new ServerReportCommand(this));
    }

    // Paper cancels pending/repeating tasks on disable; an already-running async
    // operation may still finish. These examples have no player effects to restore.
}
