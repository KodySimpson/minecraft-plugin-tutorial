package dev.kodysimpson.kitpicker;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public final class KitMenuListener implements Listener {
    private final KitPickerPlugin plugin;

    public KitMenuListener(KitPickerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        // getInventory() is the TOP inventory of the open window: the chest, not the
        // player's own inventory at the bottom. Checking the top inventory means we also
        // catch clicks down in the player's inventory, like shift-clicking an item up into
        // the menu. getHolder(false) skips copying block data we don't need.
        if (!(event.getInventory().getHolder(false) instanceof KitMenu)) {
            return;
        }

        // While our menu is open, nothing moves: no taking icons, no putting items in.
        event.setCancelled(true);

        // Raw slots number the whole window, top inventory first. Raw slot 13 can only be
        // the menu's slot 13, never a slot in the player's inventory.
        if (event.getRawSlot() != KitMenu.TRAILBLAZER_SLOT) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        var leftovers = player.getInventory().addItem(KitItems.createSword(), KitItems.createRations());
        if (!leftovers.isEmpty()) {
            player.sendMessage(Component.text("Some of your kit didn't fit. Make some room!", NamedTextColor.RED));
        } else {
            player.sendMessage(Component.text("You received the Trailblazer kit!", NamedTextColor.GREEN));
        }

        // Closing an inventory DURING its click event isn't safe (see the InventoryClickEvent
        // Javadocs), so we use Episode 15's runTask to close it on the next tick instead.
        plugin.getServer().getScheduler().runTask(plugin, () -> player.closeInventory());
    }

    @EventHandler
    public void onMenuDrag(InventoryDragEvent event) {
        // Holding the mouse and dragging a stack across slots is a separate event from clicking.
        if (event.getInventory().getHolder(false) instanceof KitMenu) {
            event.setCancelled(true);
        }
    }
}
