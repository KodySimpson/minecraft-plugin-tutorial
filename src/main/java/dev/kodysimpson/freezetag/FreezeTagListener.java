package dev.kodysimpson.freezetag;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class FreezeTagListener implements Listener {
    private final FreezeTagGame game;

    public FreezeTagListener(FreezeTagGame game) {
        this.game = game;
    }

    // Cancelling a move puts the player back where they were.
    // hasChangedPosition() is false when they only turn their head, so they can still look around.
    // Limits: this stops walking, sprinting, jumping and falling, but not teleports
    // (ender pearls, chorus fruit, /tp), and held players can still use items and break blocks.
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.hasChangedPosition() && game.isHeldInPlace(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    // Only one entity hitting another. Void, lava, fall damage and /kill are a plain
    // EntityDamageEvent, so this method never sees them, and they still work as normal.
    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        // getDamager() is whatever hit directly: the player for a punch, the arrow for a shot.
        // So arrows, snowballs and mobs never count as tags.
        if (!(event.getEntity() instanceof Player victim) || !(event.getDamager() instanceof Player attacker)) {
            return;
        }
        // Leave fights involving anyone outside the round alone.
        if (!game.isParticipant(victim) || !game.isParticipant(attacker)) {
            return;
        }

        // In Freeze Tag a hit is a tag, not damage.
        event.setCancelled(true);
        game.handleHit(attacker, victim);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        game.handleQuit(event.getPlayer());
    }
}
