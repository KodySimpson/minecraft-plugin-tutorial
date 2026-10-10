package dev.kodysimpson.cooldowns;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked", "rawtypes", "removal"})
class SnowballWandListenerTest {
    // Sound's constants are looked up in a server registry when the class first loads.
    // There is no server in a unit test, so load Sound once against a stand-in registry
    // that gives every sound a mock. The lesson code is unchanged.
    static {
        try (var registries = mockStatic(RegistryAccess.class)) {
            var access = mock(RegistryAccess.class);
            registries.when(RegistryAccess::registryAccess).thenReturn(access);
            doAnswer(invocation -> new MockSoundRegistry()).when(access).getRegistry(any(RegistryKey.class));
            doAnswer(invocation -> new MockSoundRegistry()).when(access).getRegistry(any(Class.class));
            assertNotNull(Sound.ENTITY_SNOWBALL_THROW);
        }
    }

    private static class MockSoundRegistry extends Registry.NotARegistry<Keyed> {
        @Override public Keyed get(NamespacedKey key) { return mock(Sound.class); }
        @Override public Keyed getOrThrow(Key key) { return mock(Sound.class); }
        @Override public Keyed getOrThrow(NamespacedKey key) { return mock(Sound.class); }
        @Override public Iterator<Keyed> iterator() { return List.<Keyed>of().iterator(); }
    }

    final CooldownsPlugin plugin = mock(CooldownsPlugin.class);
    final BukkitScheduler scheduler = mock(BukkitScheduler.class);
    final BukkitTask scheduled = mock(BukkitTask.class);
    final SnowballWandListener listener = new SnowballWandListener(plugin);
    final UUID id = UUID.randomUUID();
    final Player player = mock(Player.class);
    final World world = mock(World.class);
    final Location location = mock(Location.class);
    MockedStatic<Bukkit> bukkit;

    SnowballWandListenerTest() {
        setUp(player, id);
    }

    // BukkitRunnable schedules itself through Bukkit.getScheduler(), so point that at a mock.
    @BeforeEach
    void mockScheduler() {
        bukkit = mockStatic(Bukkit.class);
        bukkit.when(Bukkit::getScheduler).thenReturn(scheduler);
        when(scheduled.getTaskId()).thenReturn(7);
        when(scheduler.runTaskTimer(eq(plugin), any(Runnable.class), anyLong(), anyLong())).thenReturn(scheduled);
    }

    @AfterEach
    void closeScheduler() {
        bukkit.close();
    }

    void setUp(Player who, UUID uuid) {
        when(who.getUniqueId()).thenReturn(uuid);
        when(who.getWorld()).thenReturn(world);
        when(who.getLocation()).thenReturn(location);
    }

    void use(Player who, Action action, Material held) {
        var event = mock(PlayerInteractEvent.class);
        when(event.getPlayer()).thenReturn(who);
        when(event.getAction()).thenReturn(action);
        when(event.getMaterial()).thenReturn(held);
        listener.onWandUse(event);
    }

    BossBar shownBar(Player who) {
        var bar = ArgumentCaptor.forClass(BossBar.class);
        verify(who).showBossBar(bar.capture());
        return bar.getValue();
    }

    WandRechargeTask scheduledTask() {
        var task = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler).runTaskTimer(eq(plugin), task.capture(), eq(0L), eq(1L));
        return assertInstanceOf(WandRechargeTask.class, task.getValue());
    }

    @Test
    void rightClickingTheAirThrowsASnowballAndShowsAFullBossBar() {
        use(player, Action.RIGHT_CLICK_AIR, Material.BLAZE_ROD);

        verify(player).launchProjectile(Snowball.class);
        verify(world).playSound(location, Sound.ENTITY_SNOWBALL_THROW, 1.0f, 1.0f);
        var bar = shownBar(player);
        assertEquals(Component.text("Wand recharging"), bar.name());
        assertEquals(1.0f, bar.progress());
        scheduledTask(); // drains it every tick, starting now
        assertTrue(lastUsedTimes().containsKey(id));
    }

    @Test
    void rightClickingABlockWorksToo() {
        use(player, Action.RIGHT_CLICK_BLOCK, Material.BLAZE_ROD);

        verify(player).launchProjectile(Snowball.class);
    }

    @Test
    void aQuickSecondClickDoesNothingWhileTheBarIsShowing() {
        use(player, Action.RIGHT_CLICK_AIR, Material.BLAZE_ROD);
        use(player, Action.RIGHT_CLICK_AIR, Material.BLAZE_ROD);

        verify(player, times(1)).launchProjectile(Snowball.class);
        verify(player, times(1)).showBossBar(any(BossBar.class));
        verify(player, never()).sendMessage(any(Component.class));
        verify(player, never()).sendActionBar(any(Component.class));
    }

    @Test
    void theTaskDrainsTheBarThenHidesItAndStops() {
        use(player, Action.RIGHT_CLICK_AIR, Material.BLAZE_ROD);
        var bar = shownBar(player);
        var task = scheduledTask();

        // The scheduled task's first run, right after firing: still (almost) full.
        task.run();
        assertTrue(bar.progress() > 0.9f);
        verify(player, never()).hideBossBar(any(BossBar.class));

        // Halfway through the cooldown: about half full.
        new WandRechargeTask(player, bar, System.currentTimeMillis() - 1500).run();
        assertEquals(0.5f, bar.progress(), 0.05f);

        // Once the cooldown has passed: hide the bar and cancel the repeating task.
        var finished = new WandRechargeTask(player, bar, System.currentTimeMillis() - 3_001);
        finished.runTaskTimer(plugin, 0L, 1L);
        finished.run();
        verify(player).hideBossBar(bar);
        verify(scheduler).cancelTask(7);
    }

    @Test
    void progressNeverGoesAboveFullIfTheClockMovesBackwards() {
        var bar = BossBar.bossBar(Component.text("Wand recharging"), 1.0f,
                BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
        // A "fired at" time in the future (the clock moved backwards) would give more than 1.
        new WandRechargeTask(player, bar, System.currentTimeMillis() + 60_000).run();

        assertEquals(1.0f, bar.progress());
    }

    @Test
    void anExpiredCooldownAllowsAnotherSnowball() {
        lastUsedTimes().put(id, System.currentTimeMillis() - 3_001); // just over 3 seconds ago
        use(player, Action.RIGHT_CLICK_AIR, Material.BLAZE_ROD);

        verify(player).launchProjectile(Snowball.class);
    }

    @Test
    void leftClicksAndOtherItemsDoNothingAndDontStartTheCooldown() {
        use(player, Action.LEFT_CLICK_AIR, Material.BLAZE_ROD);
        use(player, Action.PHYSICAL, Material.BLAZE_ROD);
        use(player, Action.RIGHT_CLICK_AIR, Material.STICK);
        use(player, Action.RIGHT_CLICK_AIR, Material.AIR); // empty hand

        verify(player, never()).launchProjectile(any());
        verify(player, never()).showBossBar(any(BossBar.class));
        verifyNoInteractions(world, scheduler);
        assertTrue(lastUsedTimes().isEmpty());
    }

    @Test
    void eachPlayerHasTheirOwnWandCooldownAndBar() {
        use(player, Action.RIGHT_CLICK_AIR, Material.BLAZE_ROD);

        var other = mock(Player.class);
        setUp(other, UUID.randomUUID());
        use(other, Action.RIGHT_CLICK_AIR, Material.BLAZE_ROD);

        verify(other).launchProjectile(Snowball.class);
        assertNotSame(shownBar(player), shownBar(other));
    }

    // Read the listener's private map so tests can simulate time passing without waiting
    // or adding a testing clock to the lesson code.
    Map<UUID, Long> lastUsedTimes() {
        try {
            var field = SnowballWandListener.class.getDeclaredField("lastUsedTimes");
            field.setAccessible(true);
            return (Map<UUID, Long>) field.get(listener);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }
}
