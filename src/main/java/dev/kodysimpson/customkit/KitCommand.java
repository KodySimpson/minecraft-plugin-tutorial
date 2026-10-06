package dev.kodysimpson.customkit;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class KitCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getExecutor() instanceof Player player)) {
            source.getSender().sendMessage(Component.text("Only a player can receive a kit."));
            return;
        }

        // Build new items on every run. An ItemStack can be changed after it is created,
        // so we don't share one object between players.
        var sword = createSword();
        var rations = createRations();

        // addItem accepts one or more items and returns whatever could not fit.
        var leftovers = player.getInventory().addItem(sword, rations);
        if (!leftovers.isEmpty()) {
            player.sendMessage(Component.text("Some of your kit didn't fit. Make some room!", NamedTextColor.RED));
            return;
        }
        player.sendMessage(Component.text("You received the Trailblazer kit!", NamedTextColor.GREEN));
    }

    @Override
    public String permission() {
        return "customkit.kit";
    }

    private static ItemStack createSword() {
        // Material says WHAT the item is. An ItemStack is one stack of it; the amount defaults to 1.
        var sword = ItemStack.of(Material.DIAMOND_SWORD);

        // getItemMeta() returns a COPY of the item's extra data (name, lore, enchantments...).
        // We change the copy, then put it back with setItemMeta() at the end.
        var meta = sword.getItemMeta();

        // Minecraft shows custom names in italics unless we turn italics off.
        meta.customName(Component.text("Trailblazer", NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false));

        // Lore is a list with one Component per tooltip line.
        // Unstyled lore is purple and italic, so we pick our own color and turn italics off.
        meta.lore(List.of(
                Component.text("A starter blade for new adventurers.", NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, false),
                Component.text("Its edge hides a little extra bite.", NamedTextColor.DARK_GRAY)
                        .decoration(TextDecoration.ITALIC, false)
        ));

        // Sharpness III. The final true allows levels above the normal maximum
        // (Sharpness V), such as Sharpness 10. Level 3 is normal, so it doesn't matter here.
        meta.addEnchant(Enchantment.SHARPNESS, 3, true);

        // Hide the "Sharpness III" tooltip line. The glint and the extra damage remain.
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        // Forget this line and none of the changes above reach the item.
        sword.setItemMeta(meta);
        return sword;
    }

    private static ItemStack createRations() {
        var rations = ItemStack.of(Material.BREAD, 16);

        // editMeta is the shorter form: it gets the meta copy, runs our code, and sets it back.
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
