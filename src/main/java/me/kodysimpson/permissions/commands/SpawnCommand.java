package me.kodysimpson.permissions.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class SpawnCommand {

    public static LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("spawn")
                .requires(commandSourceStack -> {
                    return commandSourceStack.getSender().hasPermission("commandpermissions.spawn") &&
                            commandSourceStack.getSender() instanceof Player;
                })
                .then(Commands.literal("zombie")
                        .executes(context -> spawnMob(context.getSource(), EntityType.ZOMBIE)))
                .then(Commands.literal("creeper")
                        .executes(context -> spawnMob(context.getSource(), EntityType.CREEPER)))
                .build();
    }

    private static int spawnMob(CommandSourceStack source, EntityType entityType) {
        var player = (Player) source.getExecutor();

        player.getWorld().spawnEntity(player.getLocation(), entityType);
        player.sendMessage(Component.text(
                "Spawned a " + entityType.key().value() + "!", NamedTextColor.GREEN
        ));
        return Command.SINGLE_SUCCESS;
    }
}
