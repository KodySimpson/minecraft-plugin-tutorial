package dev.kodysimpson.dialogs;

import dev.kodysimpson.dialogs.commands.HealMenuCommand;
import dev.kodysimpson.dialogs.commands.RulesCommand;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;

public final class DialogExamplesPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // Populate Minecraft's built-in Server Links screen, accessible from the Escape menu.
        // Replace this example URL with your server's website.
        // Docs: https://jd.papermc.io/paper/26.3/org/bukkit/ServerLinks.html
        getServer().getServerLinks().addLink(
                Component.text("Our Website"),
                URI.create("https://example.com")
        );

        // These commands have no arguments or branches, so BasicCommand is enough.
        registerCommand("rules", "Read the server rules.", new RulesCommand());
        registerCommand("healmenu", "Confirm healing yourself.", new HealMenuCommand());
    }
}
