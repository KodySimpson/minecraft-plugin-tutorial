package dev.kodysimpson.admintoolkit;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public final class AdminCommand {

    private static final String USE_PERMISSION = "admintoolkit.use";
    private static final String SELECTOR_PERMISSION = "minecraft.command.selector";

    private AdminCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("admin")
                .requires(source -> source.getSender().hasPermission(USE_PERMISSION)
                        && source.getSender().hasPermission(SELECTOR_PERMISSION))
                .then(Commands.literal("heal")
                        .then(Commands.argument("targets", ArgumentTypes.players())
                                .executes(AdminCommand::healTargets)))
                .then(Commands.literal("gamemode")
                        .then(Commands.argument("mode", ArgumentTypes.gameMode())
                                .then(Commands.argument("targets", ArgumentTypes.players())
                                        .executes(AdminCommand::changeGameMode))))
                .build();
    }

    private static int healTargets(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        List<Player> targets = resolveTargets(context);

        for (Player target : targets) {
            AttributeInstance maxHealth = target.getAttribute(Attribute.MAX_HEALTH);
            if (maxHealth != null) {
                target.setHealth(maxHealth.getValue());
            }

            target.setFoodLevel(20);
            target.setSaturation(20.0F);
            target.setFireTicks(0);
            target.sendMessage(Component.text("You have been healed.", NamedTextColor.GREEN));
        }

        context.getSource().getSender().sendMessage(
                Component.text("Healed " + describePlayers(targets.size()) + ".", NamedTextColor.GREEN)
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int changeGameMode(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        GameMode gameMode = context.getArgument("mode", GameMode.class);
        List<Player> targets = resolveTargets(context);

        for (Player target : targets) {
            target.setGameMode(gameMode);
            target.sendMessage(
                    Component.text("Your game mode is now ", NamedTextColor.GRAY)
                            .append(Component.text(formatGameMode(gameMode), NamedTextColor.AQUA))
                            .append(Component.text(".", NamedTextColor.GRAY))
            );
        }

        context.getSource().getSender().sendMessage(
                Component.text("Set " + describePlayers(targets.size()) + " to ", NamedTextColor.GREEN)
                        .append(Component.text(formatGameMode(gameMode), NamedTextColor.AQUA))
                        .append(Component.text(".", NamedTextColor.GREEN))
        );
        return Command.SINGLE_SUCCESS;
    }

    private static List<Player> resolveTargets(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {
        PlayerSelectorArgumentResolver resolver = context.getArgument(
                "targets",
                PlayerSelectorArgumentResolver.class
        );
        return resolver.resolve(context.getSource());
    }

    private static String describePlayers(int count) {
        return count + (count == 1 ? " player" : " players");
    }

    private static String formatGameMode(GameMode gameMode) {
        return gameMode.name().toLowerCase(Locale.ROOT);
    }
}
