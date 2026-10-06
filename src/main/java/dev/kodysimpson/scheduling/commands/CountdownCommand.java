package dev.kodysimpson.scheduling.commands;

import dev.kodysimpson.scheduling.SchedulingPlugin;
import dev.kodysimpson.scheduling.tasks.CountdownTask;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.scheduler.BukkitTask;

public final class CountdownCommand implements BasicCommand {
    private final SchedulingPlugin plugin;
    private BukkitTask countdownTask;

    public CountdownCommand(SchedulingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (args.length != 0) {
            source.getSender().sendMessage(Component.text("Usage: /countdown"));
            return;
        }
        // A cancelled countdown is finished, so it is safe to start a new one.
        if (countdownTask != null && !countdownTask.isCancelled()) {
            source.getSender().sendMessage(Component.text("A countdown is already running."));
            return;
        }

        // Show this anonymous subclass FIRST (import org.bukkit.scheduler.BukkitRunnable).
        // BukkitRunnable is abstract; the body below supplies its run() implementation.
        // countdownTask = new BukkitRunnable() {
        //     private int secondsLeft = 5;
        //
        //     @Override
        //     public void run() {
        //         if (secondsLeft == 0) {
        //             plugin.getServer().broadcast(Component.text("Go!"));
        //             cancel();
        //             return;
        //         }
        //         plugin.getServer().broadcast(Component.text("Starting in " + secondsLeft + "..."));
        //         secondsLeft--;
        //     }
        // }.runTaskTimer(plugin, 0L, 20L);

        // THEN move that counter and run() into a named CountdownTask class.
        // Use only one version at a time: both schedule the same countdown.
        // Create a fresh instance each time; a BukkitRunnable can only be scheduled once.
        countdownTask = new CountdownTask(plugin).runTaskTimer(plugin, 0L, 20L);

        // 5, 4, 3, 2, 1, Go! This is tick time, not a precision wall-clock timer.
    }

    @Override
    public String permission() { return "schedulingexamples.use"; }
}
