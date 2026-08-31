package dev.kodysimpson.serverutilities;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class HealCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack source, @NotNull String[] args) {
        if (!(source.getExecutor() instanceof Player player)) {
            source.getSender().sendMessage(
                    Component.text("Only a player can use /heal.", NamedTextColor.RED)
            );
            return;
        }

        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth == null) {
            source.getSender().sendMessage(
                    Component.text("Paper could not read your maximum health.", NamedTextColor.RED)
            );
            return;
        }

        player.setHealth(maxHealth.getValue());
        player.setFoodLevel(20);
        player.setSaturation(20.0F);
        player.setFireTicks(0);
        player.sendMessage(Component.text("You have been healed.", NamedTextColor.GREEN));
    }

    @Override
    public String permission() {
        return "serverutilities.heal";
    }
}
