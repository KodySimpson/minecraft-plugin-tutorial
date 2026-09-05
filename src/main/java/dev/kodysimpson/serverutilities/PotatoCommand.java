package dev.kodysimpson.serverutilities;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public final class PotatoCommand implements BasicCommand {

    @Override
    public void execute(@NotNull CommandSourceStack source, @NotNull String[] args) {
        if (!(source.getExecutor() instanceof Player player)) {
            source.getSender().sendMessage(
                    Component.text("Only a player can summon the potato.", NamedTextColor.RED)
            );
            return;
        }

        // Place the potato four blocks ahead so the player has to walk over and pick it up.
        Location dropLocation = player.getLocation();
        Vector forward = dropLocation.getDirection().setY(0).normalize().multiply(4);
        dropLocation.add(forward).add(0, 0.5, 0);

        player.getWorld().dropItem(
                dropLocation,
                ItemStack.of(Material.POTATO)
        );

        player.sendMessage(
                Component.text("A potato has appeared. What could go wrong?", NamedTextColor.YELLOW)
        );
    }
}
