package dev.kodysimpson.movementcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

public final class AnnounceCommand {

    public static LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("announce")
                // greedyString takes all remaining text, including spaces, so this must be the last argument.
                // Compare: word() reads one word; string() also allows a phrase wrapped in quotes.
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(context -> {
                            // "message" is our argument's name, not a word the player has to type.
                            String message = StringArgumentType.getString(context, "message");

                            // No Player check: the console can make an announcement too.
                            // Component.text keeps the supplied message as plain text, not formatting tags.
                            Bukkit.getServer().broadcast(
                                    Component.text("[Announcement] ", NamedTextColor.GOLD)
                                            .append(Component.text(message, NamedTextColor.WHITE))
                            );
                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }
}
