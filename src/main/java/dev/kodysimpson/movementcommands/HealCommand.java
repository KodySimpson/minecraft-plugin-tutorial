package dev.kodysimpson.movementcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public final class HealCommand {

    private HealCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("heal")
                // The root still works by itself, just like the BasicCommand from Part 5.
                .executes(context -> fullyHeal(context.getSource()))
                // An argument is a variable slot. Brigadier turns it into a validated double.
                // The second parameter chooses the input type. Brigadier's built-in options are:
                //   IntegerArgumentType.integer()    -> int (whole number)
                //   LongArgumentType.longArg()       -> long (larger whole number)
                //   FloatArgumentType.floatArg()     -> float (decimal number)
                //   DoubleArgumentType.doubleArg()   -> double (higher-precision decimal number)
                //   BoolArgumentType.bool()          -> boolean (true or false)
                //   StringArgumentType.word()        -> String (one unquoted word)
                //   StringArgumentType.string()      -> String (one word or a quoted phrase)
                //   StringArgumentType.greedyString() -> String (all remaining text; must go last)
                // Number types also accept (min) or (min, max), as below.
                // Paper adds Minecraft-specific types such as players and locations; these are not the full API.
                // When changing the type, also change the getter and the Java value your handler accepts.
                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(1.0, 20.0))
                        .executes(context -> healByAmount(
                                context.getSource(),
                                DoubleArgumentType.getDouble(context, "amount")
                        )))
                .build();
    }

    private static int fullyHeal(CommandSourceStack source) {
        if (!(source.getExecutor() instanceof Player player)) {
            source.getSender().sendMessage(
                    Component.text("Only a player can use /heal.", NamedTextColor.RED)
            );
            return 0;
        }

        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setSaturation(20.0F);
        player.sendMessage(Component.text("You have been fully healed.", NamedTextColor.GREEN));
        return Command.SINGLE_SUCCESS;
    }

    private static int healByAmount(CommandSourceStack source, double amount) {
        if (!(source.getExecutor() instanceof Player player)) {
            source.getSender().sendMessage(
                    Component.text("Only a player can use /heal.", NamedTextColor.RED)
            );
            return 0;
        }

        // Set health to the validated amount, as demonstrated in the episode.
        player.setHealth(amount);

        player.sendMessage(
                Component.text("Your health is now ", NamedTextColor.GRAY)
                        .append(Component.text(amount, NamedTextColor.AQUA))
                        .append(Component.text(".", NamedTextColor.GRAY))
        );
        return Command.SINGLE_SUCCESS;
    }
}
