package dev.kodysimpson.movementcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public final class GlowCommand {

    public static LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("glow")
                // One boolean argument accepts true or false; we do not need two literal branches.
                .then(Commands.argument("enabled", BoolArgumentType.bool())
                        .executes(context -> {
                            var source = context.getSource();
                            // We need a player to apply the outline to. A normal console command has no entity executor.
                            if (!(source.getExecutor() instanceof Player player)) {
                                source.getSender().sendMessage(Component.text(
                                        "Only a player can have their glow changed.", NamedTextColor.RED
                                ));
                                return 0;
                            }

                            // Read the same name we declared above. Brigadier already checked the input.
                            boolean enabled = BoolArgumentType.getBool(context, "enabled");
                            player.setGlowing(enabled);
                            player.sendMessage(Component.text(
                                    enabled ? "You are now glowing!" : "Your glow is off.",
                                    NamedTextColor.GREEN
                            ));
                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }
}
