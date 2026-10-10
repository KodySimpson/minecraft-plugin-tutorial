package dev.kodysimpson.cooldowns;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SmiteCommandTest {
    final SmiteCommand command = new SmiteCommand();
    final UUID id = UUID.randomUUID();
    final Player player = mock(Player.class);
    final World world = mock(World.class);
    final Block block = mock(Block.class);
    final Location location = mock(Location.class);

    SmiteCommandTest() {
        setUp(player, id);
    }

    void setUp(Player who, UUID uuid) {
        when(who.getUniqueId()).thenReturn(uuid);
        when(who.getWorld()).thenReturn(world);
        when(who.getTargetBlockExact(50)).thenReturn(block);
        when(block.getLocation()).thenReturn(location);
    }

    void smite(Player who) {
        var source = mock(CommandSourceStack.class);
        when(source.getExecutor()).thenReturn(who);
        when(source.getSender()).thenReturn(who);
        command.execute(source, new String[0]);
    }

    @Test
    void smiteStrikesRealLightningAtTheTargetBlock() {
        smite(player);

        verify(world).strikeLightning(location);
        verify(world, never()).strikeLightningEffect(any());
        assertTrue(lastUsedTimes().containsKey(id));
    }

    @Test
    void aSecondSmiteIsRefusedWithTheTimeLeft() {
        smite(player);
        smite(player);

        verify(world, times(1)).strikeLightning(any());
        verify(player).sendMessage(Component.text(
                "You can smite again in " + SmiteCommand.COOLDOWN_SECONDS + "s.", NamedTextColor.RED));
    }

    @Test
    void partialSecondsLeftRoundUp() {
        lastUsedTimes().put(id, System.currentTimeMillis() - 9_800); // 0.2 seconds of the 10 left
        smite(player);

        verify(player).sendMessage(Component.text("You can smite again in 1s.", NamedTextColor.RED));
        verifyNoInteractions(world);
    }

    @Test
    void anExpiredCooldownAllowsAnotherSmite() {
        lastUsedTimes().put(id, System.currentTimeMillis() - 10_001); // just over 10 seconds ago
        smite(player);

        verify(world).strikeLightning(location);
        assertTrue(System.currentTimeMillis() - lastUsedTimes().get(id) < 1_000); // recorded again, just now
    }

    @Test
    void lookingAtTheSkyDoesNotUseUpTheCooldown() {
        when(player.getTargetBlockExact(50)).thenReturn(null);
        smite(player);

        verify(player).sendMessage(Component.text("Look at a block to smite it.", NamedTextColor.RED));
        verifyNoInteractions(world);
        assertTrue(lastUsedTimes().isEmpty());

        when(player.getTargetBlockExact(50)).thenReturn(block);
        smite(player);
        verify(world).strikeLightning(location);
    }

    @Test
    void rejoiningKeepsTheCooldownButOtherPlayersHaveTheirOwn() {
        smite(player);

        // A new Player object after rejoining, with the same account UUID.
        var rejoined = mock(Player.class);
        setUp(rejoined, id);
        smite(rejoined);
        verify(world, times(1)).strikeLightning(any());

        var other = mock(Player.class);
        setUp(other, UUID.randomUUID());
        smite(other);
        verify(world, times(2)).strikeLightning(any());
    }

    @Test
    void consoleCannotSmite() {
        var source = mock(CommandSourceStack.class);
        var console = mock(CommandSender.class);
        when(source.getSender()).thenReturn(console);
        when(source.getExecutor()).thenReturn(null); // the console is not an entity

        command.execute(source, new String[0]);

        verify(console).sendMessage(Component.text("Only a player can smite."));
        verifyNoInteractions(world);
        assertEquals("cooldowns.smite", command.permission());
    }

    // Read the command's private map so tests can simulate time passing without waiting
    // or adding a testing clock to the lesson code.
    @SuppressWarnings("unchecked")
    Map<UUID, Long> lastUsedTimes() {
        try {
            var field = SmiteCommand.class.getDeclaredField("lastUsedTimes");
            field.setAccessible(true);
            return (Map<UUID, Long>) field.get(command);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }
}
