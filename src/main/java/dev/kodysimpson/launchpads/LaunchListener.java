package dev.kodysimpson.launchpads;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public final class LaunchListener implements Listener {
    private final LaunchPads plugin;

    public LaunchListener(LaunchPads plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onStep(PlayerInteractEvent event) {
        // PHYSICAL means stepping on something, not right-clicking it.
        // Respect interactions cancelled by other plugins.
        if (event.getAction() != Action.PHYSICAL) return;
        var plate = event.getClickedBlock();
        if (plate == null || !Tag.PRESSURE_PLATES.isTagged(plate.getType())) return;

        var config = plugin.getConfig(); // Cached: no file read on each step.
        if (!config.getBoolean("launch-pad.enabled", true)) return;

        // matchMaterial can return null for a typo. Reject it instead of crashing.
        Material material = Material.matchMaterial(config.getString("launch-pad.material", "GOLD_BLOCK"));
        if (material == null || !material.isBlock() || !material.isSolid() || !material.isOccluding()) return;
        if (plate.getRelative(BlockFace.DOWN).getType() != material) return;

        var player = event.getPlayer();
        if (player.isInsideVehicle() || player.isFlying() || player.isGliding()) return;

        double forward = config.getDouble("launch-pad.forward-strength", 1.6);
        double upward = config.getDouble("launch-pad.upward-strength", 1.0);
        // A small range check, like /snack. YAML also accepts .nan and .inf,
        // so check finiteness before passing values to the player's velocity.
        if (!Double.isFinite(forward) || forward < 0 || forward > 2) return;
        if (!Double.isFinite(upward) || upward < 0.1 || upward > 2) return;

        Location takeoff = player.getLocation();
        player.setVelocity(launchVelocity(takeoff, forward, upward));
        // A one-time cloud burst at takeoff, not a trail following the player.
        // 20 particles, XYZ spread, then particle speed. Nearby players can see it too.
        player.getWorld().spawnParticle(Particle.CLOUD, takeoff, 20, 0.3, 0.1, 0.3, 0.05);
        // Only the launched player hears this. Volume 1 and pitch 1 use normal playback.
        player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
        // Reuse Part 12: this is a trusted server-owner template, not player input.
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                config.getString("launch-pad.message", "<gold>Whoooosh!</gold>")));
        // No cooldown or fall-damage immunity yet. Land in water!
    }

    // Separate so the direction calculation can be explained and tested on its own.
    static Vector launchVelocity(Location location, double forward, double upward) {
        Location facing = location.clone();
        facing.setPitch(0); // Ignore looking up/down; keep the horizontal facing (yaw).
        // multiply controls horizontal speed; setY supplies the upward speed.
        // These are velocity values, NOT a distance or height in blocks.
        return facing.getDirection().multiply(forward).setY(upward);
    }
}
