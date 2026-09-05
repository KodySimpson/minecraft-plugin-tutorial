package dev.kodysimpson.serverutilities;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public final class PotatoListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPotatoPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        // For this simple example, every potato is dangerous.
        if (event.getItem().getItemStack().getType() != Material.POTATO) {
            return;
        }

        player.sendMessage(
                Component.text("You should not have picked up that potato.", NamedTextColor.RED)
        );
        player.setHealth(0.0);
    }
}
