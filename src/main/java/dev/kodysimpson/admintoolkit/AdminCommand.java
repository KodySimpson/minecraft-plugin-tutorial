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

    private AdminCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("admin")
                .then(Commands.literal("heal")
                        // Stopping at /admin heal applies the action to the executing player.
                        .executes(context -> {
                            // Without targets, we need a player executor to use as "self".
                            if (!(context.getSource().getExecutor() instanceof Player player)) {
                                context.getSource().getSender().sendMessage(Component.text(
                                        "Specify a player name or selector when using this from the console.",
                                        NamedTextColor.RED
                                ));
                                return 0;
                            }
                            // A one-player list lets us reuse the same healing code.
                            return healTargets(context, List.of(player));
                        })
                        // This typed argument accepts player names and selectors, not arbitrary text.
                        .then(Commands.argument("targets", ArgumentTypes.players())
                                .executes(context -> healTargets(context, resolveTargets(context)))))
                .then(Commands.literal("gamemode")
                        // Paper parses the input directly into a Bukkit GameMode value.
                        .then(Commands.argument("mode", ArgumentTypes.gameMode())
                                // The mode is required, but targets are optional because this node executes too.
                                .executes(context -> {
                                    // As with heal, omitted targets mean the executing player.
                                    if (!(context.getSource().getExecutor() instanceof Player player)) {
                                        context.getSource().getSender().sendMessage(Component.text(
                                                "Specify a player name or selector when using this from the console.",
                                                NamedTextColor.RED
                                        ));
                                        return 0;
                                    }
                                    return changeGameMode(context, List.of(player));
                                })
                                .then(Commands.argument("targets", ArgumentTypes.players())
                                        .executes(context -> changeGameMode(context, resolveTargets(context))))))
                .build();
    }

    private static int healTargets(CommandContext<CommandSourceStack> context, List<Player> targets) {

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

    private static int changeGameMode(CommandContext<CommandSourceStack> context, List<Player> targets) {
        GameMode gameMode = context.getArgument("mode", GameMode.class);

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
        // The resolver uses the live command source, which is important for selectors such as @s and @p.
        PlayerSelectorArgumentResolver resolver = context.getArgument(
                "targets",
                PlayerSelectorArgumentResolver.class
        );
        // Let CommandSyntaxException reach Brigadier so the player keeps Minecraft's useful error cursor.
        return resolver.resolve(context.getSource());
    }

    private static String describePlayers(int count) {
        return count + (count == 1 ? " player" : " players");
    }

    private static String formatGameMode(GameMode gameMode) {
        return gameMode.name().toLowerCase(Locale.ROOT);
    }
}
