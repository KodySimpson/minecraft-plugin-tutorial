package dev.kodysimpson.scheduling.commands;

import dev.kodysimpson.scheduling.SchedulingPlugin;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.scheduler.BukkitTask;

public final class RepeatCommand implements BasicCommand {
    private final SchedulingPlugin plugin;
    private BukkitTask announcementTask; // null means our announcement is stopped.

    public RepeatCommand(SchedulingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (args.length != 0) {
            source.getSender().sendMessage(Component.text("Usage: /repeat"));
            return;
        }

        // Example 3: add this stop branch AFTER demonstrating repetition below.
        // The same command toggles one shared announcement, not one per player.
        if (announcementTask != null) {
            announcementTask.cancel();
            announcementTask = null;
            source.getSender().sendMessage(Component.text("Announcement stopped."));
            return;
        }

        // Example 2: run repeatedly. First teach this call without storing its result.
        // 0L = no initial waiting period (next scheduler opportunity, not inline).
        // 100L = repeat every 100 ticks, normally 5 seconds.
        // Then store the returned handle so the stop branch can cancel this task.
        announcementTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            plugin.getServer().broadcast(Component.text("Remember to be kind to other players!"));
        }, 0L, 100L);

        source.getSender().sendMessage(Component.text("Announcement started. Run /repeat again to stop."));
    }

    @Override
    public String permission() { return "schedulingexamples.use"; }
}
