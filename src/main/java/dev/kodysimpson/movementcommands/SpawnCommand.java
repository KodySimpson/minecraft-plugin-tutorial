package dev.kodysimpson.movementcommands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public final class SpawnCommand {

    private SpawnCommand() {
    }

    public static LiteralCommandNode<CommandSourceStack> create() {
        // Each exact word after /spawn is a literal branch in the command tree.
        return Commands.literal("spawn")
                .then(Commands.literal("zombie")
                        .executes(context -> spawnMob(context.getSource(), EntityType.ZOMBIE)))
                .then(Commands.literal("creeper")
                        .executes(context -> spawnMob(context.getSource(), EntityType.CREEPER)))
                .build();
    }

    private static int spawnMob(CommandSourceStack source, EntityType entityType) {
        if (!(source.getExecutor() instanceof Player player)) {
            source.getSender().sendMessage(Component.text(
                    "Only a player can use this command.",
                    NamedTextColor.RED
            ));
            return 0;
        }

        // Keep this first tree focused on literals; no amount argument is needed yet.
        player.getWorld().spawnEntity(player.getLocation(), entityType);
        player.sendMessage(Component.text("Spawned a " + entityType.key().value() + "!", NamedTextColor.GREEN));
        return Command.SINGLE_SUCCESS;
    }
}
