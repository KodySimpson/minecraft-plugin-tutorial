package dev.kodysimpson.movementcommands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

// Opening comparison only: unregistered. SpawnCommand is the command-tree version.
public final class BasicSpawnCommand implements BasicCommand {

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getExecutor() instanceof Player player)) {
            source.getSender().sendMessage(Component.text("Only a player can use this command.", NamedTextColor.RED));
            return;
        }

        // Check before reading args[0]: /spawn alone has no arguments.
        if (args.length != 1) {
            source.getSender().sendMessage(Component.text("Usage: /spawn <zombie|creeper>", NamedTextColor.RED));
            return;
        }

        // /spawn zombie gives us the string "zombie". We choose the route manually.
        EntityType entityType;
        if (args[0].equals("zombie")) {
            entityType = EntityType.ZOMBIE;
        } else if (args[0].equals("creeper")) {
            entityType = EntityType.CREEPER;
        } else {
            source.getSender().sendMessage(Component.text("Choose zombie or creeper.", NamedTextColor.RED));
            return;
        }

        // The gameplay action is identical to the tree version.
        player.getWorld().spawnEntity(player.getLocation(), entityType);
        player.sendMessage(Component.text("Spawned a " + entityType.key().value() + "!", NamedTextColor.GREEN));
    }
}
