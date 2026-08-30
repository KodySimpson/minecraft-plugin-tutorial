package dev.kodysimpson.hellopaper;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class HelloPaper extends JavaPlugin {

    @Override
    public void onLoad() {
        getComponentLogger().info(
                Component.text("HelloPaper is loading...", NamedTextColor.GRAY)
        );
    }

    @Override
    public void onEnable() {
        getComponentLogger().info(
                Component.text("HelloPaper is enabled!", NamedTextColor.GREEN)
        );

        getServer().sendMessage(
                Component.text("Hello from your first Paper plugin!", NamedTextColor.AQUA)
        );
    }

    @Override
    public void onDisable() {
        getComponentLogger().info(
                Component.text("HelloPaper is disabled.", NamedTextColor.YELLOW)
        );
    }
}
