package dev.kodysimpson.minimessage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

class MiniMessageTest {
    private YamlConfiguration config() throws Exception {
        try (var reader = new InputStreamReader(
                getClass().getResourceAsStream("/config.yml"), StandardCharsets.UTF_8)) {
            return YamlConfiguration.loadConfiguration(reader);
        }
    }

    private Stream<Component> tree(Component component) {
        return Stream.concat(Stream.of(component), component.children().stream().flatMap(this::tree));
    }

    @Test void welcomeKeepsInsertedMarkupLiteral() throws Exception {
        var result = MiniMessage.miniMessage().deserialize(config().getString("join-effects.message"),
                Placeholder.unparsed("player", "<red>Alice"));
        assertEquals("Welcome, <red>Alice!", PlainTextComponentSerializer.plainText().serialize(result));
    }

    @Test void welcomeUsesTheCurrentPlayerName() throws Exception {
        String template = config().getString("join-effects.message");
        for (String name : new String[]{"Kody", "Alex"}) {
            var result = MiniMessage.miniMessage().deserialize(template,
                    Placeholder.unparsed("player", name));
            assertEquals("Welcome, " + name + "!",
                    PlainTextComponentSerializer.plainText().serialize(result));
        }
    }

    @Test void snackUsesTheSuppliedAmountRatherThanAHardcodedCount() throws Exception {
        String template = config().getString("snack.message");
        for (int amount : new int[]{1, 12, 64}) {
            var result = MiniMessage.miniMessage().deserialize(template,
                    Placeholder.unparsed("amount", Integer.toString(amount)));
            assertEquals("SNACK TIME!\n Enjoy your " + amount + " cookies! [More cookies]",
                    PlainTextComponentSerializer.plainText().serialize(result));
        }
    }

    @Test void snackShowpieceHasTextGradientAndInteractions() throws Exception {
        var config = config();
        assertEquals(5, config.getInt("snack.amount"));
        String template = config.getString("snack.message");
        // Strict parsing here verifies our example closes tags, without making runtime strict.
        var result = MiniMessage.builder().strict(true).build().deserialize(template,
                Placeholder.unparsed("amount", "5"));
        assertEquals("SNACK TIME!\n Enjoy your 5 cookies! [More cookies]",
                PlainTextComponentSerializer.plainText().serialize(result));
        assertTrue(tree(result).anyMatch(c -> c.hoverEvent() != null));
        assertTrue(tree(result).anyMatch(c ->
                ClickEvent.suggestCommand("/snack").equals(c.clickEvent())));
        assertTrue(tree(result).filter(c -> c.color() != null).map(Component::color).distinct().count() > 3);
    }
}
