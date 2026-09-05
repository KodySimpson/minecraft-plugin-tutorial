package dev.kodysimpson.serverutilities;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

public final class ServerInfoCommand implements BasicCommand {

    // Keeping the plugin instance gives this command access to the running server.
    private final ServerUtilities plugin;

    public ServerInfoCommand(ServerUtilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull CommandSourceStack source, @NotNull String[] args) {
        int onlinePlayers = plugin.getServer().getOnlinePlayers().size();
        int maxPlayers = plugin.getServer().getMaxPlayers();
        int worlds = plugin.getServer().getWorlds().size();

        Component message = Component.text()
                .append(Component.text("Server information", NamedTextColor.GOLD))
                .appendNewline()
                .append(Component.text("Minecraft: ", NamedTextColor.GRAY))
                .append(Component.text(plugin.getServer().getMinecraftVersion(), NamedTextColor.WHITE))
                .appendNewline()
                .append(Component.text("Players: ", NamedTextColor.GRAY))
                .append(Component.text(onlinePlayers + "/" + maxPlayers, NamedTextColor.WHITE))
                .appendNewline()
                .append(Component.text("Loaded worlds: ", NamedTextColor.GRAY))
                .append(Component.text(worlds, NamedTextColor.WHITE))
                .build();

        // The sender may be a player or the console, so this command works for both.
        source.getSender().sendMessage(message);
    }
}
