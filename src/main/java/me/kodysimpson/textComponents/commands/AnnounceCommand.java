package me.kodysimpson.textComponents.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.kodysimpson.textComponents.TextComponents;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;

public class AnnounceCommand {

    public LiteralCommandNode<CommandSourceStack> create() {
        return Commands.literal("announce")
                .requires(source -> source.getSender().hasPermission("announcements.use"))
                .executes(context -> {
                    sendUsage(context.getSource());
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.literal("chat")
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(context -> {
                                    sendChatAnnouncement(StringArgumentType.getString(context, "message"));
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("actionbar")
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(context -> {
                                    sendActionBarAnnouncement(context.getSource(), StringArgumentType.getString(context, "message"));
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("title")
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(context -> {
                                    sendTitleAnnouncement(context.getSource(), StringArgumentType.getString(context, "message"));
                                    return Command.SINGLE_SUCCESS;
                                })))
                .build();
    }

    private void sendChatAnnouncement(String message) {
        var item = ItemStack.of(Material.GOLD_INGOT, 12);
        var itemMeta = item.getItemMeta();
        itemMeta.displayName(Component.text("GOLD!!!", NamedTextColor.YELLOW));
        item.setItemMeta(itemMeta);
        var announcement = Component.text()
                .append(Component.text("ANNOUNCEMENT")
                        .color(NamedTextColor.GOLD)
                        .decoration(TextDecoration.BOLD, true)
                        .hoverEvent(HoverEvent.showItem(item.asHoverEvent().value())))
                .append(Component.text("  •  ")
                        .color(NamedTextColor.DARK_GRAY))
                .append(Component.text(message))
                .append(Component.text("[Join]")
                        .color(NamedTextColor.AQUA)
                        .hoverEvent(HoverEvent.showText(Component.text("Ready? Click to prepare /warp event")))
                        .clickEvent(ClickEvent.suggestCommand("/warp event")))
                .build();

        Bukkit.getServer().sendMessage(announcement);
    }

    private void sendActionBarAnnouncement(CommandSourceStack source, String message) {
        Component actionBar = Component.text("◆ ", NamedTextColor.GOLD)
                .append(Component.text(message).color(TextColor.color(0x55CCFF)));

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendActionBar(actionBar);
        }
    }

    private void sendTitleAnnouncement(CommandSourceStack source, String message) {
        Component title = Component.text(message, NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD);
        Component subtitle = Component.text("Server announcement", NamedTextColor.GRAY);

        Title.Times times = Title.Times.times(Duration.ofSeconds(1L), Duration.ofSeconds(5L), Duration.ofMillis(500));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(Title.title(title, subtitle, times));
        }
    }

    private void sendUsage(CommandSourceStack source) {
//        var usage = Component.text("Usage", NamedTextColor.RED).decorate(TextDecoration.BOLD)
//                .appendNewline()
//                .append(Component.text("/announce chat <message>").color(NamedTextColor.GRAY))
//                .appendNewline()
//                .append(Component.text("/announce actionbar <message>").color(NamedTextColor.GRAY))
//                .appendNewline()
//                .append(Component.text("/announce title <message>").color(NamedTextColor.GRAY));

        var usage = Component.text()
                .append(Component.text("Usage", NamedTextColor.RED).decorate(TextDecoration.BOLD))
                .appendNewline()
                .append(Component.text("/announce chat <message>").color(NamedTextColor.GRAY))
                .appendNewline()
                .append(Component.text("/announce actionbar <message>").color(NamedTextColor.GRAY))
                .appendNewline()
                .append(Component.text("/announce title <message>").color(NamedTextColor.GRAY))
                .build();

        source.getSender().sendMessage(usage);
    }
}
