package dev.kodysimpson.welcomemessages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class ConnectionListener implements Listener {

    private static final Component WELCOME_MESSAGE = Component.text(
            "Welcome! We are glad you are here.",
            NamedTextColor.GREEN
    );

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.sendMessage(WELCOME_MESSAGE);
        event.joinMessage(
                Component.text(player.getName(), NamedTextColor.AQUA)
                        .append(Component.text(" joined the server.", NamedTextColor.GRAY))
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.quitMessage(
                Component.text(player.getName(), NamedTextColor.AQUA)
                        .append(Component.text(" left the server.", NamedTextColor.GRAY))
        );
    }
}
