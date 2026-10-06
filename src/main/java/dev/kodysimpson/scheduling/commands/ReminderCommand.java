package dev.kodysimpson.scheduling.commands;

import dev.kodysimpson.scheduling.SchedulingPlugin;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public final class ReminderCommand {
    public static LiteralCommandNode<CommandSourceStack> create(SchedulingPlugin plugin) {
        return Commands.literal("remind")
                .requires(source -> source.getSender().hasPermission("schedulingexamples.use"))
                // Reuse our argument lesson: whole seconds, with a minimum of 1.
                // Brigadier rejects missing, non-integer or out-of-range input for us.
                .then(Commands.argument("seconds", IntegerArgumentType.integer(1))
                        .executes(context -> {
                            int seconds = IntegerArgumentType.getInteger(context, "seconds");
                            // The user types seconds; the scheduler expects ticks.
                            // 5 seconds * 20 = 100 ticks. The L makes multiplication use long.
                            long delayTicks = seconds * 20L;
                            // Whoever ran the command: a player or the console.
                            CommandSender sender = context.getSource().getSender();

                            // This task is small enough to keep as an inline lambda.
                            // Example 1: plugin instance, code to run later, delay in ticks.
                            // The lambda remembers sender, so the reminder goes back to them.
                            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                                sender.sendMessage(Component.text("Reminder: take a stretch break!"));
                            }, delayTicks);

                            sender.sendMessage(Component.text(
                                    "Reminder scheduled in " + seconds + " second(s)!"));
                            // This returns now; the callback runs later on the main thread.
                            // Never Thread.sleep() here: that would freeze the server.
                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }

    // Each invocation schedules one independent reminder for its sender, not repeated reminders.
    // If a player logs out before it is due, the message simply isn't delivered.
    // 20 ticks = 1 second at 20 TPS; lag can stretch the wait.
    // Docs: https://docs.papermc.io/paper/dev/scheduler/
}
