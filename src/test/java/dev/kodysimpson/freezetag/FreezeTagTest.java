package dev.kodysimpson.freezetag;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FreezeTagTest {
    final FreezeTagPlugin plugin = mock(FreezeTagPlugin.class);
    final Server server = mock(Server.class);
    final BukkitScheduler scheduler = mock(BukkitScheduler.class);
    final BukkitTask timer = mock(BukkitTask.class);
    final CommandSender admin = mock(CommandSender.class);
    final YamlConfiguration config = bundledConfig();
    final Map<UUID, Player> online = new LinkedHashMap<>();
    final List<Runnable> scheduledTimers = new ArrayList<>();
    final FreezeTagGame game;
    final FreezeTagListener listener;

    FreezeTagTest() {
        when(plugin.getServer()).thenReturn(server);
        when(plugin.getConfig()).thenReturn(config);
        when(server.getScheduler()).thenReturn(scheduler);
        doAnswer(invocation -> List.copyOf(online.values())).when(server).getOnlinePlayers();
        when(server.getPlayer(any(UUID.class))).thenAnswer(invocation -> online.get(invocation.<UUID>getArgument(0)));
        when(scheduler.runTaskTimer(eq(plugin), any(Runnable.class), anyLong(), anyLong())).thenAnswer(invocation -> {
            scheduledTimers.add(invocation.getArgument(1));
            return timer;
        });
        game = new FreezeTagGame(plugin);
        listener = new FreezeTagListener(game);
    }

    // ---- Starting and the countdown ----

    @Test
    void startNeedsAtLeastTwoPlayers() {
        join("Alex");
        game.start(admin);

        verify(admin).sendMessage(Component.text("Freeze Tag needs at least two players online.", NamedTextColor.RED));
        verifyNoInteractions(scheduler);
        assertEquals(GameState.IDLE, state());
    }

    @Test
    void startPicksATaggerFromEveryoneOnlineAndCountsDownOnOneTimer() {
        var alex = join("Alex");
        var blake = join("Blake");
        var casey = join("Casey");
        game.start(admin);

        assertEquals(GameState.COUNTDOWN, state());
        assertTrue(List.of(alex, blake, casey).contains(tagger()));
        assertAnnounced("messages.start", Placeholder.unparsed("tagger", tagger().getName()));
        verify(scheduler).runTaskTimer(eq(plugin), any(Runnable.class), eq(0L), eq(20L));

        tick();
        var bar = message("messages.countdown", Placeholder.unparsed("seconds", "5"));
        verify(alex).sendActionBar(bar);
        verify(blake).sendActionBar(bar);
        verify(casey).sendActionBar(bar);
    }

    @Test
    void aSecondStartIsRefusedWhileARoundIsOn() {
        join("Alex");
        join("Blake");
        game.start(admin);
        game.start(admin);

        verify(admin).sendMessage(Component.text("A Freeze Tag round is already running.", NamedTextColor.RED));
        verify(scheduler, times(1)).runTaskTimer(eq(plugin), any(Runnable.class), anyLong(), anyLong());
    }

    @Test
    void everyoneIsHeldDuringTheCountdownButCanStillLookAround() {
        var alex = join("Alex");
        var blake = join("Blake");
        game.start(admin);

        assertTrue(move(alex, true).isCancelled());
        assertTrue(move(blake, true).isCancelled());
        assertFalse(move(alex, false).isCancelled()); // only turning the head
    }

    @Test
    void theCountdownTurnsIntoTheRoundAndRunnersCanMove() {
        var alex = join("Alex");
        var blake = join("Blake");
        game.start(admin);
        tick(6); // 5, 4, 3, 2, 1, then GO

        assertEquals(GameState.RUNNING, state());
        assertAnnounced("messages.go");
        verify(alex).sendActionBar(message("messages.time-left", Placeholder.unparsed("seconds", "120")));
        assertFalse(move(alex, true).isCancelled());
        assertFalse(move(blake, true).isCancelled());
    }

    @Test
    void playersWhoJoinLaterStayOutOfTheRound() {
        join("Alex");
        join("Blake");
        game.start(admin);
        var late = join("Late");
        tick();

        assertFalse(game.isParticipant(late));
        verify(late, never()).sendActionBar(any(Component.class));
        assertFalse(move(late, true).isCancelled());
    }

    // ---- Tagging and rescuing ----

    @Test
    void theTaggerFreezesARunnerWithAHit() {
        startRunningRound("Alex", "Blake", "Casey");
        var runner = runners().getFirst();

        var hit = hit(tagger(), runner);

        assertTrue(hit.isCancelled()); // a tag, not damage
        assertAnnounced("messages.tagged",
                Placeholder.unparsed("player", runner.getName()),
                Placeholder.unparsed("tagger", tagger().getName()));
        assertTrue(move(runner, true).isCancelled());
        assertFalse(move(runner, false).isCancelled()); // can still look around
        assertEquals(GameState.RUNNING, state()); // one runner is still free
    }

    @Test
    void aFreeRunnerUnfreezesAFrozenTeammate() {
        startRunningRound("Alex", "Blake", "Casey");
        var frozen = runners().get(0);
        var rescuer = runners().get(1);
        hit(tagger(), frozen);

        assertTrue(hit(rescuer, frozen).isCancelled());

        assertAnnounced("messages.rescued",
                Placeholder.unparsed("player", frozen.getName()),
                Placeholder.unparsed("rescuer", rescuer.getName()));
        assertFalse(move(frozen, true).isCancelled());
    }

    @Test
    void hitsThatAreNotTagsOrRescuesDoNothingButAreStillCancelled() {
        startRunningRound("Alex", "Blake", "Casey", "Drew", "Emery");
        var runners = runners();
        var first = runners.get(0);
        var second = runners.get(1);
        var third = runners.get(2);
        var fourth = runners.get(3);
        hit(tagger(), first);
        hit(tagger(), second);
        clearInvocations(server);

        assertTrue(hit(tagger(), first).isCancelled()); // already frozen
        assertTrue(hit(first, second).isCancelled());   // a frozen runner can't rescue
        assertTrue(hit(third, tagger()).isCancelled()); // runners can't tag the tagger
        assertTrue(hit(third, fourth).isCancelled());   // nor freeze each other

        verify(server, never()).sendMessage(any(Component.class));
        assertTrue(move(first, true).isCancelled());
        assertTrue(move(second, true).isCancelled());
        assertFalse(move(third, true).isCancelled());
        assertFalse(move(fourth, true).isCancelled());
    }

    @Test
    void hitsDuringTheCountdownAreCancelledButDoNotTag() {
        join("Alex");
        join("Blake");
        game.start(admin);
        var runner = runners().getFirst();
        clearInvocations(server);

        assertTrue(hit(tagger(), runner).isCancelled());
        verify(server, never()).sendMessage(any(Component.class));
        tick(6);
        assertFalse(move(runner, true).isCancelled());
    }

    // ---- Only hits between two participants count ----

    @Test
    void voidLavaFallAndKillDamageAreNeverCancelled() {
        startRunningRound("Alex", "Blake", "Casey");
        var runner = runners().getFirst();
        hit(tagger(), runner); // even a frozen runner can fall into the void

        for (var cause : List.of(DamageCause.VOID, DamageCause.LAVA, DamageCause.FALL, DamageCause.KILL)) {
            var event = mock(EntityDamageEvent.class);
            when(event.getEntity()).thenReturn(runner);
            when(event.getCause()).thenReturn(cause);

            deliver(event);

            verify(event, never()).setCancelled(anyBoolean());
        }
    }

    @Test
    void mobsArrowsAndOutsidersAreLeftAlone() {
        startRunningRound("Alex", "Blake", "Casey");
        var runner = runners().get(0);
        var otherRunner = runners().get(1);
        hit(tagger(), runner);
        var outsider = join("Outsider");
        var zombie = mock(Zombie.class);
        var arrow = mock(Arrow.class);
        when(arrow.getShooter()).thenReturn(tagger());
        clearInvocations(server);

        assertFalse(hit(zombie, otherRunner).isCancelled());   // a mob hitting a runner
        assertFalse(hit(otherRunner, zombie).isCancelled());   // a runner hitting a mob
        assertFalse(hit(arrow, otherRunner).isCancelled());    // the tagger's arrow: not a tag
        assertFalse(hit(outsider, runner).isCancelled());      // an outsider can't rescue
        assertFalse(hit(tagger(), outsider).isCancelled());    // the tagger can't freeze an outsider

        verify(server, never()).sendMessage(any(Component.class));
        assertFalse(move(otherRunner, true).isCancelled());
        assertTrue(move(runner, true).isCancelled());
    }

    // ---- How a round ends ----

    @Test
    void theTaggerWinsWhenEveryRunnerIsFrozen() {
        startRunningRound("Alex", "Blake", "Casey");
        var runners = runners();
        hit(tagger(), runners.get(0));
        hit(tagger(), runners.get(1));

        assertAnnounced("messages.tagger-wins");
        assertRoundOver(runners.get(0));
    }

    @Test
    void theRunnersWinWhenTimeRunsOut() {
        config.set("round-seconds", 3);
        var alex = join("Alex");
        join("Blake");
        game.start(admin);

        tick(8); // 5 countdown seconds, GO with 3s left, 2s, 1s
        assertEquals(GameState.RUNNING, state());
        verify(alex).sendActionBar(message("messages.time-left", Placeholder.unparsed("seconds", "1")));

        tick();
        assertAnnounced("messages.runners-win");
        assertRoundOver(alex);
    }

    @Test
    void allRunnersQuittingIsNotATaggerWin() {
        startRunningRound("Alex", "Blake", "Casey");
        var runners = runners();
        var tagger = tagger();

        quit(runners.get(0));
        assertEquals(GameState.RUNNING, state());
        quit(runners.get(1));

        assertAnnounced("messages.not-enough-players");
        assertNotAnnounced("messages.tagger-wins");
        assertRoundOver(tagger);
    }

    @Test
    void aRunnerQuittingWhileEveryoneElseIsFrozenGivesTheTaggerTheWin() {
        startRunningRound("Alex", "Blake", "Casey");
        var runners = runners();
        hit(tagger(), runners.get(0));

        quit(runners.get(1));

        assertAnnounced("messages.tagger-wins");
        assertEquals(GameState.IDLE, state());
    }

    @Test
    void theTaggerQuittingEndsTheRound() {
        startRunningRound("Alex", "Blake", "Casey");
        var stillHere = runners().getFirst();

        quit(tagger());

        assertAnnounced("messages.tagger-left");
        assertRoundOver(stillHere);
    }

    @Test
    void aRunnerQuittingDuringTheCountdownLeavingOnePlayerEndsTheRound() {
        join("Alex");
        join("Blake");
        game.start(admin);

        quit(runners().getFirst());

        assertAnnounced("messages.not-enough-players");
        assertEquals(GameState.IDLE, state());
    }

    @Test
    void someoneOutsideTheRoundQuittingChangesNothing() {
        startRunningRound("Alex", "Blake");
        var outsider = join("Outsider");
        clearInvocations(server);

        quit(outsider);

        verify(server, never()).sendMessage(any(Component.class));
        assertEquals(GameState.RUNNING, state());
    }

    @Test
    void stopEndsTheRoundAndANewOneCanStart() {
        var alex = join("Alex");
        join("Blake");
        game.start(admin);
        hit(tagger(), runners().getFirst()); // ignored: still counting down

        game.stop(admin);

        assertAnnounced("messages.stopped");
        assertRoundOver(alex);

        game.start(admin);
        assertEquals(GameState.COUNTDOWN, state());
        verify(scheduler, times(2)).runTaskTimer(eq(plugin), any(Runnable.class), eq(0L), eq(20L));
    }

    @Test
    void stopWithNoRoundSaysSo() {
        game.stop(admin);

        verify(admin).sendMessage(Component.text("There is no Freeze Tag round to stop.", NamedTextColor.RED));
        verify(server, never()).sendMessage(any(Component.class));
    }

    // ---- The command ----

    @Test
    void theCommandNeedsPermissionAndRunsStartAndStop() throws CommandSyntaxException {
        join("Alex");
        join("Blake");
        var dispatcher = new CommandDispatcher<CommandSourceStack>();
        dispatcher.getRoot().addChild(FreezeTagCommand.create(game));
        var source = mock(CommandSourceStack.class);
        when(source.getSender()).thenReturn(admin);

        assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("freezetag start", source));
        assertEquals(GameState.IDLE, state());

        when(admin.hasPermission("freezetag.admin")).thenReturn(true);
        assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("freezetag", source)); // incomplete
        dispatcher.execute("freezetag start", source);
        assertEquals(GameState.COUNTDOWN, state());
        dispatcher.execute("freezetag stop", source);
        assertEquals(GameState.IDLE, state());
    }

    @Test
    void theListenerHasNoHandlerForPlainDamage() {
        // Bukkit only calls a handler with events of its parameter type (or a subclass).
        for (Method method : FreezeTagListener.class.getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventHandler.class)) {
                assertFalse(method.getParameterTypes()[0].isAssignableFrom(EntityDamageEvent.class), method.getName());
                assertEquals(EventPriority.NORMAL, method.getAnnotation(EventHandler.class).priority());
            }
        }
    }

    // ---- Helpers ----

    static YamlConfiguration bundledConfig() {
        var resource = FreezeTagTest.class.getResourceAsStream("/config.yml");
        assertNotNull(resource);
        try (var reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
            return YamlConfiguration.loadConfiguration(reader);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    Player join(String name) {
        var player = mock(Player.class);
        var id = UUID.randomUUID();
        when(player.getUniqueId()).thenReturn(id);
        when(player.getName()).thenReturn(name);
        online.put(id, player);
        return player;
    }

    void quit(Player player) {
        var event = mock(PlayerQuitEvent.class);
        when(event.getPlayer()).thenReturn(player);
        deliver(event); // the player is still online while the quit event runs
        online.remove(player.getUniqueId());
    }

    void startRunningRound(String... names) {
        for (String name : names) {
            join(name);
        }
        game.start(admin);
        tick(6);
        assertEquals(GameState.RUNNING, state());
        clearInvocations(server);
    }

    void tick() {
        scheduledTimers.getLast().run();
    }

    void tick(int times) {
        for (int i = 0; i < times; i++) {
            tick();
        }
    }

    // One entity hitting another, delivered the way Bukkit would.
    EntityDamageByEntityEvent hit(Entity attacker, Entity victim) {
        var event = mock(EntityDamageByEntityEvent.class);
        when(event.getDamager()).thenReturn(attacker);
        when(event.getEntity()).thenReturn(victim);
        trackCancelled(event);
        deliver(event);
        return event;
    }

    PlayerMoveEvent move(Player player, boolean changedPosition) {
        var event = mock(PlayerMoveEvent.class);
        when(event.getPlayer()).thenReturn(player);
        when(event.hasChangedPosition()).thenReturn(changedPosition);
        trackCancelled(event);
        deliver(event);
        return event;
    }

    // Give a mocked event a working cancelled flag.
    static void trackCancelled(Cancellable event) {
        boolean[] cancelled = {false};
        doAnswer(invocation -> cancelled[0] = invocation.getArgument(0)).when(event).setCancelled(anyBoolean());
        doAnswer(invocation -> cancelled[0]).when(event).isCancelled();
    }

    // Call every @EventHandler whose parameter type accepts this event, as Bukkit does.
    void deliver(Event event) {
        for (Method method : FreezeTagListener.class.getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventHandler.class) && method.getParameterTypes()[0].isInstance(event)) {
                try {
                    method.invoke(listener, event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new AssertionError(e);
                }
            }
        }
    }

    Component message(String path, TagResolver... placeholders) {
        return MiniMessage.miniMessage().deserialize(config.getString(path), placeholders);
    }

    void assertAnnounced(String path, TagResolver... placeholders) {
        verify(server).sendMessage(message(path, placeholders));
    }

    void assertNotAnnounced(String path) {
        verify(server, never()).sendMessage(message(path));
    }

    void assertRoundOver(Player formerParticipant) {
        assertEquals(GameState.IDLE, state());
        verify(timer).cancel();
        assertFalse(game.isParticipant(formerParticipant));
        assertFalse(move(formerParticipant, true).isCancelled());
    }

    // Read the game's private fields so the lesson code doesn't need extra getters for tests.
    GameState state() {
        return (GameState) field("state");
    }

    Player tagger() {
        return online.get((UUID) field("taggerId"));
    }

    List<Player> runners() {
        var tagger = tagger();
        return online.values().stream().filter(p -> p != tagger && game.isParticipant(p)).toList();
    }

    Object field(String name) {
        try {
            var field = FreezeTagGame.class.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(game);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }
}
