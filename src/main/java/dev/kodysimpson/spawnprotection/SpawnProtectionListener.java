package dev.kodysimpson.spawnprotection;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public final class SpawnProtectionListener implements Listener {

    private static final double PROTECTION_RADIUS = 16.0;
    private static final double PROTECTION_RADIUS_SQUARED = PROTECTION_RADIUS * PROTECTION_RADIUS;
    private static final String BYPASS_PERMISSION = "spawnprotection.bypass";
    private static final Component PROTECTED_MESSAGE = Component.text(
            "You cannot do that inside spawn protection.",
            NamedTextColor.RED
    );

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission(BYPASS_PERMISSION)) {
            return;
        }

        if (!isInsideSpawnProtection(event.getBlock().getLocation())) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(PROTECTED_MESSAGE);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission(BYPASS_PERMISSION)) {
            return;
        }

        if (!isInsideSpawnProtection(event.getBlock().getLocation())) {
            return;
        }

        event.setCancelled(true);
        player.sendMessage(PROTECTED_MESSAGE);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (player.hasPermission(BYPASS_PERMISSION)) {
            return;
        }

        if (!isInsideSpawnProtection(player.getLocation())) {
            return;
        }

        event.setCancelled(true);
    }

    private boolean isInsideSpawnProtection(Location location) {
        Location spawn = location.getWorld().getSpawnLocation();
        double deltaX = location.getX() - spawn.getX();
        double deltaZ = location.getZ() - spawn.getZ();

        return deltaX * deltaX + deltaZ * deltaZ <= PROTECTION_RADIUS_SQUARED;
    }
}
