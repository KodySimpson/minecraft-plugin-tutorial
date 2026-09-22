package me.kodysimpson.configtutorial.listeners;

import me.kodysimpson.configtutorial.ConfigTutorial;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final ConfigTutorial plugin;

    public JoinListener(ConfigTutorial plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!this.plugin.getConfig().getBoolean("join-effects.enabled", true)) {
            return;
        }

        String message = this.plugin.getConfig().getString("join-effects.message", "Welcome to the server!");
        var component = Component.text(message).color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD);
        e.getPlayer().sendMessage(component);
    }

}
