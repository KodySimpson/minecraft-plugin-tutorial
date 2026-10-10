package dev.kodysimpson.cooldowns;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SnowballWandListener implements Listener {
    // How long the wand takes to recharge.
    static final long COOLDOWN_SECONDS = 3;

    private final CooldownsPlugin plugin;

    // The same idea as /smite: each player's UUID, mapped to when they last fired the wand.
    // This is a separate Map, so the wand and /smite have separate cooldowns.
    private final Map<UUID, Long> lastUsedTimes = new HashMap<>();

    public SnowballWandListener(CooldownsPlugin plugin) {
        this.plugin = plugin;
    }

    // Don't add ignoreCancelled = true here: Paper sends right-clicks on the air
    // already marked as cancelled, so the wand would only work when clicking a block.
    @EventHandler
    public void onWandUse(PlayerInteractEvent event) {
        // Right-clicking the air or a block. Left clicks and pressure plates don't count.
        if (!event.getAction().isRightClick()) {
            return;
        }
        // getMaterial() is the type of the item in the hand that was used (AIR if it's empty).
        // For now, any blaze rod is a wand. Marking one special item comes with PDC later.
        if (event.getMaterial() != Material.BLAZE_ROD) {
            return;
        }

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        Long lastUsed = lastUsedTimes.get(playerId);
        if (lastUsed != null && System.currentTimeMillis() - lastUsed < COOLDOWN_SECONDS * 1000) {
            // The boss bar is already showing the recharge, so there's nothing to say.
            // The Map still decides whether the wand can fire; the bar only displays it.
            return;
        }

        // Fire a snowball from the player's eyes, in the direction they're looking.
        player.launchProjectile(Snowball.class);
        // launchProjectile is silent, so play the normal throwing sound ourselves.
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 1.0f, 1.0f);

        long firedAt = System.currentTimeMillis();
        lastUsedTimes.put(playerId, firedAt);

        // Show a full boss bar, then let a repeating task drain it every tick until the cooldown ends.
        // Boss bars: https://docs.papermc.io/adventure/bossbar/
        BossBar bar = BossBar.bossBar(Component.text("Wand recharging"), 1.0f,
                BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
        player.showBossBar(bar);
        new WandRechargeTask(player, bar, firedAt).runTaskTimer(plugin, 0L, 1L);
    }
}
