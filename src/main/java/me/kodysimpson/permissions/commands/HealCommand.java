package me.kodysimpson.permissions.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class HealCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack source, @NotNull String[] args) {
        if (!(source.getExecutor() instanceof Player player)) {
            source.getSender().sendMessage(Component.text(
                    "Only a player can be healed with this command.", NamedTextColor.RED
            ));
            return;
        }

        player.setHealth(20.0);
        player.sendMessage(Component.text("You have been healed.", NamedTextColor.GREEN));
    }

    @Override
    public @Nullable String permission() {
        return "commandpermissions.heal";
    }
}
