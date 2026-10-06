package dev.kodysimpson.kitpicker;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * The Trailblazer kit from Episode 16, pasted in as supporting code.
 * Nothing in this file is new; this episode is about the menu that hands it out.
 */
final class KitItems {

    private KitItems() {
    }

    static ItemStack createSword() {
        var sword = ItemStack.of(Material.DIAMOND_SWORD);
        sword.editMeta(meta -> {
            meta.customName(Component.text("Trailblazer", NamedTextColor.GOLD)
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                    Component.text("A starter blade for new adventurers.", NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
            meta.addEnchant(Enchantment.SHARPNESS, 3, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        });
        return sword;
    }

    static ItemStack createRations() {
        var rations = ItemStack.of(Material.BREAD, 16);
        rations.editMeta(meta -> {
            meta.customName(Component.text("Trail Rations", NamedTextColor.YELLOW)
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(List.of(
                    Component.text("Enough bread for a long journey.", NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, false)
            ));
        });
        return rations;
    }
}
