package dev.kodysimpson.dialogs.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class RulesCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (args.length != 0) {
            source.getSender().sendMessage(Component.text("Usage: /rules", NamedTextColor.RED));
            return;
        }
        if (!(source.getExecutor() instanceof Player player)) {
            source.getSender().sendMessage(Component.text("Only a player can open a dialog.", NamedTextColor.RED));
            return;
        }

        // Start here: a title, a notice type, then showDialog. Add body() next.
        // Paper docs: https://docs.papermc.io/paper/dev/dialogs/#creating-dialogs-dynamically
        Dialog dialog = Dialog.create(builder -> builder.empty()
                // The base holds the title, body, and (later) input fields.
                .base(DialogBase.builder(Component.text("Server Rules", NamedTextColor.GOLD))
                        .body(List.of(
                                // Add this after demonstrating plainMessage: bodies can display items too.
                                // Display only: this does not give the player an item or open a book.
                                // Docs: https://jd.papermc.io/paper/26.3/io/papermc/paper/registry/data/dialog/body/ItemDialogBody.Builder.html
                                DialogBody.item(ItemStack.of(Material.BOOK))
                                        .description(DialogBody.plainMessage(Component.text("Our server handbook", NamedTextColor.GOLD)))
                                        .showTooltip(true) // Hover over the item to see its tooltip.
                                        .build(),
                                DialogBody.plainMessage(Component.text("Be kind to other players.")),
                                DialogBody.plainMessage(Component.text("Do not grief other people's builds.")),
                                DialogBody.plainMessage(Component.text("Have fun, and ask staff if you need help."))
                        ))
                        .build())
                // A notice supplies a single OK button. There is no callback yet.
                .type(DialogType.notice())
        );

        // Build the dialog first, then send it to the Minecraft client to display.
        // This is an ordinary dynamic dialog; no bootstrapper or registry entry is needed.
        player.showDialog(dialog);
    }
}
