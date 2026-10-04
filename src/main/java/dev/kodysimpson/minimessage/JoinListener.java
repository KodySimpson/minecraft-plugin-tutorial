package dev.kodysimpson.minimessage;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class JoinListener implements Listener {
    private final JoinMessages plugin;

    public JoinListener(JoinMessages plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var config = plugin.getConfig();
        if (!config.getBoolean("join-effects.enabled", true)) return;

        var player = event.getPlayer();
        // LEGACY COMPARISON ONLY: older Bukkit/Spigot tutorials commonly built colored strings.
        // This predates widespread Adventure usage, not Paper itself.
        // player.sendMessage(org.bukkit.ChatColor.GOLD + "Welcome, "
        //         + org.bukkit.ChatColor.BOLD + player.getName()
        //         + org.bukkit.ChatColor.RESET + org.bukkit.ChatColor.GOLD + "!");
        // Config messages often used & codes instead: &6 = gold, &l = bold, &r = reset.
        // String legacyTemplate = "&6Welcome, &l" + player.getName() + "&r&6!";
        // player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', legacyTemplate));
        // & codes need that translation step; MiniMessage does NOT interpret them as tags.
        // Modern equivalent: <gold>Welcome, <bold><player></bold>!</gold>

        String template = config.getString("join-effects.message", "<gold>Welcome, <player>!</gold>");
        // deserialize turns the administrator's markup into an Adventure Component.
        // DEMO: start with "<gold>Welcome!</gold>" and deserialize(template).
        // Then add <player> in config.yml and the resolver below to personalize it.
        // <gold> is built in; <player> is our own placeholder, supplied by this code.
        // The resolver name is "player", WITHOUT the angle brackets used in the template.
        // unparsed inserts the name as literal text, never as additional formatting tags.
        // It still inherits the surrounding <gold> style from the template.
        // https://docs.papermc.io/adventure/minimessage/dynamic-replacements/
        var message = MiniMessage.miniMessage().deserialize(
                template,
                Placeholder.unparsed("player", player.getName())
        );
        player.sendMessage(message);
    }
}
