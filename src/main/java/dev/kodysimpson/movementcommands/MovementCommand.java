package dev.kodysimpson.movementcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public final class MovementCommand {

    private MovementCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> create() {
        // The root can execute by itself and still have branches beneath it.
        return Commands.literal("movement")
                .executes(context -> showUsage(context.getSource()))
                .then(Commands.literal("walk")
                        // Brigadier validates the number and range before our executor runs.
                        .then(Commands.argument("speed", FloatArgumentType.floatArg(0.0F, 1.0F))
                                .executes(context -> setSpeed(
                                        context.getSource(),
                                        FloatArgumentType.getFloat(context, "speed"),
                                        SpeedType.WALK
                                ))))
                .then(Commands.literal("fly")
                        .then(Commands.argument("speed", FloatArgumentType.floatArg(0.0F, 1.0F))
                                .executes(context -> setSpeed(
                                        context.getSource(),
                                        FloatArgumentType.getFloat(context, "speed"),
                                        SpeedType.FLY
                                ))))
                .build();
    }

    private static int showUsage(CommandSourceStack source) {
        source.getSender().sendMessage(
                Component.text("Use /movement walk <speed> or /movement fly <speed>.", NamedTextColor.AQUA)
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int setSpeed(CommandSourceStack source, float speed, SpeedType speedType) {
        // The effect belongs to the entity this command is running as, not always its sender.
        if (!(source.getExecutor() instanceof Player player)) {
            source.getSender().sendMessage(Component.text(
                    "Only a player can change their movement speed.",
                    NamedTextColor.RED
            ));
            return 0;
        }

        if (speedType == SpeedType.WALK) {
            player.setWalkSpeed(speed);
        } else {
            player.setFlySpeed(speed);
        }

        player.sendMessage(
                Component.text("Your ", NamedTextColor.GRAY)
                        .append(Component.text(speedType.displayName, NamedTextColor.AQUA))
                        .append(Component.text(" speed is now ", NamedTextColor.GRAY))
                        .append(Component.text(speed, NamedTextColor.GREEN))
                        .append(Component.text(".", NamedTextColor.GRAY))
        );

        // Brigadier executors return an integer result; one represents a normal success.
        return Command.SINGLE_SUCCESS;
    }

    private enum SpeedType {
        WALK("walk"),
        FLY("fly");

        private final String displayName;

        SpeedType(String displayName) {
            this.displayName = displayName;
        }
    }
}
