package dev.kodysimpson.kitpicker;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public final class KitsCommand implements BasicCommand {
    private final KitPickerPlugin plugin;

    public KitsCommand(KitPickerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getExecutor() instanceof Player player)) {
            source.getSender().sendMessage(Component.text("Only a player can open the kit menu."));
            return;
        }

        // A brand-new menu for every /kits, so two players never share one inventory.
        player.openInventory(new KitMenu(plugin).getInventory());
    }

    @Override
    public String permission() {
        return "kitpicker.use";
    }
}
