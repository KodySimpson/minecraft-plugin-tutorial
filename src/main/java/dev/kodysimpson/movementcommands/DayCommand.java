package dev.kodysimpson.movementcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public final class DayCommand {

    public static LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("day")
                // An executor on the root makes /day work without a subcommand.
                .executes(context -> setTime(context.getSource(), 6000L, "noon"))
                .then(Commands.literal("sunrise")
                        .executes(context -> setTime(context.getSource(), 23000L, "sunrise")))
                .then(Commands.literal("sunset")
                        .executes(context -> setTime(context.getSource(), 12000L, "sunset")))
                .build();
    }

    private static int setTime(CommandSourceStack source, long time, String timeName) {
        if (!(source.getExecutor() instanceof Player player)) {
            source.getSender().sendMessage(Component.text(
                    "Only a player can use this command.", NamedTextColor.RED
            ));
            return 0;
        }

        // Change the shared world's time, not just this player's view of the sky.
        // A Minecraft day has 24,000 ticks; these are fixed presets, not user arguments.
        player.getWorld().setTime(time);
        player.sendMessage(Component.text("Set the time to " + timeName + "!", NamedTextColor.GREEN));
        return Command.SINGLE_SUCCESS;
    }
}
