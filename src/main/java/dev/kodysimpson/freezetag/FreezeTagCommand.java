package dev.kodysimpson.freezetag;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public final class FreezeTagCommand {
    // /freezetag start and /freezetag stop. Bare /freezetag is incomplete, like /spawn was.
    // Players and the console can both run it, so there's no player check.
    public static LiteralCommandNode<CommandSourceStack> create(FreezeTagGame game) {
        return Commands.literal("freezetag")
                .requires(source -> source.getSender().hasPermission("freezetag.admin"))
                .then(Commands.literal("start")
                        .executes(context -> {
                            game.start(context.getSource().getSender());
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("stop")
                        .executes(context -> {
                            game.stop(context.getSource().getSender());
                            return Command.SINGLE_SUCCESS;
                        }))
                .build();
    }
}
