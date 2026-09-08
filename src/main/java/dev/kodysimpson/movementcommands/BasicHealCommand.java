package dev.kodysimpson.movementcommands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

// Teaching example only: not registered. Compare this with HealCommand's tree.
public final class BasicHealCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getExecutor() instanceof Player player)) {
            source.getSender().sendMessage(
                    Component.text("Only a player can use /heal.", NamedTextColor.RED)
            );
            return;
        }

        // args contains the words AFTER /heal. No words means a full heal.
        if (args.length == 0) {
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setSaturation(20.0F);
            player.sendMessage(Component.text("You have been fully healed.", NamedTextColor.GREEN));
            return;
        }

        // We must check the command's shape ourselves: /heal accepts only one amount.
        if (args.length > 1) {
            source.getSender().sendMessage(Component.text("Usage: /heal [amount]", NamedTextColor.RED));
            return;
        }

        double amount;
        try {
            // For /heal 6, args[0] is the STRING "6". Convert it to a number ourselves.
            amount = Double.parseDouble(args[0]);
        } catch (NumberFormatException exception) {
            source.getSender().sendMessage(Component.text("Enter a number, like /heal 6.", NamedTextColor.RED));
            return;
        }

        // Parsing alone does not enforce our range (or reject special values like NaN).
        if (!Double.isFinite(amount) || amount < 1.0 || amount > 20.0) {
            source.getSender().sendMessage(Component.text("Amount must be between 1 and 20.", NamedTextColor.RED));
            return;
        }

        // The actual healing is the same as in the Brigadier version.
        player.setHealth(amount);
        player.sendMessage(
                Component.text("Your health is now ", NamedTextColor.GRAY)
                        .append(Component.text(amount, NamedTextColor.AQUA))
                        .append(Component.text(".", NamedTextColor.GRAY))
        );
    }
}
