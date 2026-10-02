package dev.kodysimpson.launchpads;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;

public final class LaunchCommand {
    public static LiteralCommandNode<CommandSourceStack> create(LaunchPads plugin) {
        return Commands.literal("launchpad")
                .requires(source -> source.getSender().hasPermission("launchpads.manage"))
                .executes(context -> {
                    // Instructions only: this command never places or replaces blocks.
                    String material = plugin.getConfig().getString("launch-pad.material", "GOLD_BLOCK");
                    context.getSource().getSender().sendMessage(Component.text(
                            "Put a pressure plate on " + material + ". Leave space above and build a water landing area."));
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }
}
