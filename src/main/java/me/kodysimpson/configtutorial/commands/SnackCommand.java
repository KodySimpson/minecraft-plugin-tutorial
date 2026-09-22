package me.kodysimpson.configtutorial.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.kodysimpson.configtutorial.ConfigTutorial;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SnackCommand {

    public static LiteralCommandNode<CommandSourceStack> create(ConfigTutorial plugin){
        return Commands.literal("snack")
                .executes(context -> {
                    var source = context.getSource();
                    if (!(source.getExecutor() instanceof Player player)) {
                        source.getSender().sendMessage(Component.text("Only a player can receive a snack."));
                        return 0;
                    }

                    //read a number from the config
                    var snackAmount = plugin.getConfig().getInt("snack-amount");
                    if (snackAmount <= 0 || snackAmount > 100) {
                        player.sendMessage(Component.text("Snack amount must be greater than 0 or less than 100."));
                        return 0;
                    }

                    var leftovers = player.getInventory().addItem(ItemStack.of(Material.COOKIE, snackAmount));
                    if (!leftovers.isEmpty()) {
                        player.sendMessage(Component.text("Your inventory could not fit all the cookies. Make some room!"));
                        return 0;
                    }
                    player.sendMessage(Component.text("Enjoy your " + snackAmount + " cookies!"));
                    return Command.SINGLE_SUCCESS;
                }).build();
    }

}
