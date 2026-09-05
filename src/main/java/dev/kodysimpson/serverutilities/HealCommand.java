package dev.kodysimpson.serverutilities;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class HealCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack source, @NotNull String[] args) {
        // getSender() is whoever started the command: a player, the console, or a command block.
        // We could use it if /heal should only affect a player who typed the command directly:
        /*
        if (!(source.getSender() instanceof Player player)) {
            return;
        }
        */

        // getExecutor() is the in-world entity this command runs as. It is null when the
        // console runs /heal directly, but it can be a player when using /execute as.
        if (!(source.getExecutor() instanceof Player player)) {
            source.getSender().sendMessage(
                    Component.text("Only a player can use /heal.", NamedTextColor.RED)
            );
            return;
        }

        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setSaturation(20.0F);
        player.setFireTicks(0);
        player.sendMessage(Component.text("You have been healed.", NamedTextColor.GREEN));
    }
}
