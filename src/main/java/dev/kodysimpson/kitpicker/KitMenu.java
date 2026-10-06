package dev.kodysimpson.kitpicker;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Our menu. Being the inventory's holder is what lets the listener recognize it:
 * a title can be copied by any other plugin, but only our code creates a KitMenu.
 */
public final class KitMenu implements InventoryHolder {

    // Slots count from 0, left to right and top to bottom. In a three-row (27-slot)
    // chest, slot 13 is the middle of the middle row.
    static final int TRAILBLAZER_SLOT = 13;

    private final Inventory inventory;

    public KitMenu(KitPickerPlugin plugin) {
        // Passing "this" makes this KitMenu the holder of the new inventory.
        // The size must be a multiple of 9 (one row of slots), from 9 up to 54.
        this.inventory = plugin.getServer().createInventory(this, 27, Component.text("Choose a kit"));
        inventory.setItem(TRAILBLAZER_SLOT, createTrailblazerIcon());
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    // The button is an ordinary ItemStack, built exactly like Episode 16's items.
    private static ItemStack createTrailblazerIcon() {
        var icon = ItemStack.of(Material.DIAMOND_SWORD);
        icon.editMeta(meta -> {
            meta.customName(Component.text("Trailblazer Kit", NamedTextColor.GOLD)
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                    Component.text("A sword and 16 trail rations.", NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false),
                    Component.text("Click to receive it!", NamedTextColor.YELLOW)
                            .decoration(TextDecoration.ITALIC, false)
            ));
        });
        return icon;
    }
}
