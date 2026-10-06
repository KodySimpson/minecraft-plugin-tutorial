package dev.kodysimpson.scheduling.tasks;

import dev.kodysimpson.scheduling.SchedulingPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.scheduler.BukkitRunnable;

// The anonymous BukkitRunnable from CountdownCommand, moved into a named class.
// The task owns its counter and behavior; the command decides when to start it.
public final class CountdownTask extends BukkitRunnable {
    private final SchedulingPlugin plugin;
    private int secondsLeft = 5;

    public CountdownTask(SchedulingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (secondsLeft == 0) {
            plugin.getServer().broadcast(Component.text("Go!"));
            cancel(); // Stops future runs; return exits this run() call.
            return;
        }

        plugin.getServer().broadcast(Component.text("Starting in " + secondsLeft + "..."));
        secondsLeft--;
    }
}
