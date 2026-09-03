package me.kodysimpson.cancellingEvents.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class EatingListener implements Listener {

    @EventHandler
    public void onPlayerFall(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        boolean shieldInMainHand = player.getInventory().getItemInMainHand().getType() == Material.SHIELD;
        boolean shieldInOffHand = player.getInventory().getItemInOffHand().getType() == Material.SHIELD;

        if (shieldInMainHand || shieldInOffHand) {
            event.setCancelled(true);
            player.sendMessage(Component.text("Damaged stopped due to your powerful shield!"));
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.CONSUMABLE_EFFECT) {
            return;
        }

        var player = e.getPlayer();
        if (!player.isSneaking()) {
            return;
        }

        e.setCancelled(true);
        player.getWorld().spawnParticle(
                Particle.PORTAL,
                player.getLocation().add(0, 1, 0),
                30,
                0.45,
                0.65,
                0.45,
                0.2
        );
        player.getWorld().playSound(
                player.getLocation(),
                Sound.ENTITY_ENDERMAN_TELEPORT,
                0.8f,
                0.6f
        );
    }

}
