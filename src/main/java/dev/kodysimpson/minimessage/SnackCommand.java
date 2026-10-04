package dev.kodysimpson.minimessage;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class SnackCommand {
    public static LiteralCommandNode<CommandSourceStack> create(JoinMessages plugin) {
        // No command argument: the server owner chooses the amount in config.yml.
        return Commands.literal("snack")
                .executes(context -> {
                    var source = context.getSource();
                    if (!(source.getExecutor() instanceof Player player)) {
                        source.getSender().sendMessage(Component.text("Only a player can receive a snack."));
                        return 0;
                    }

                    // Read a whole number from the cached configuration; 5 is the fallback.
                    int amount = plugin.getConfig().getInt("snack.amount", 5);
                    // A typed getter does not enforce a range. Keep this example to one stack.
                    if (amount < 1 || amount > 64) {
                        player.sendMessage(Component.text("snack.amount must be between 1 and 64. Check config.yml."));
                        return 0;
                    }

                    // addItem returns anything that could not fit. Do not silently claim full success.
                    var leftovers = player.getInventory().addItem(ItemStack.of(Material.COOKIE, amount));
                    if (!leftovers.isEmpty()) {
                        player.sendMessage(Component.text("Your inventory could not fit all the cookies. Make some room!"));
                        return 0;
                    }
                    // Only parse trusted administrator-authored markup, not concatenated user input.
                    String template = plugin.getConfig().getString("snack.message",
                            "<green>Enjoy your <amount> cookies!</green>");
                    // The config controls the styling; code provides the actual amount.
                    // Same idea as <player>: <amount> is a custom placeholder, not a built-in tag.
                    // unparsed takes a String, so convert the validated number to text.
                    var message = MiniMessage.miniMessage().deserialize(
                            template,
                            Placeholder.unparsed("amount", Integer.toString(amount))
                    );
                    player.sendMessage(message);
                    return Command.SINGLE_SUCCESS;
                })
                .build();
    }
}
