package dev.kodysimpson.cooldowns;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

// Drains the wand's boss bar until the cooldown ends, then hides it.
// Like Episode 15's CountdownTask: a repeating task that cancels itself when it's done.
// Boss bars: https://docs.papermc.io/adventure/bossbar/
public final class WandRechargeTask extends BukkitRunnable {
    // Holding the Player is fine here: this task only lives for the 3-second cooldown.
    // The Map stores UUIDs because it keeps entries for as long as the server runs.
    private final Player player;
    private final BossBar bar;
    private final long firedAt;

    public WandRechargeTask(Player player, BossBar bar, long firedAt) {
        this.player = player;
        this.bar = bar;
        this.firedAt = firedAt;
    }

    @Override
    public void run() {
        long elapsed = System.currentTimeMillis() - firedAt;
        long cooldownMillis = SnowballWandListener.COOLDOWN_SECONDS * 1000;
        if (elapsed >= cooldownMillis) {
            player.hideBossBar(bar);
            cancel(); // Stops future runs; return exits this run() call.
            return;
        }

        // Progress goes from 1 (full) down to 0 (empty): the share of the cooldown left.
        // The (float) makes this a decimal division; with two longs, 1500 / 3000 would be 0.
        // Boss bars only accept 0 to 1, so Math.min keeps it at 1 even if someone sets the clock backwards.
        float progress = (cooldownMillis - elapsed) / (float) cooldownMillis;
        bar.progress(Math.min(1.0f, progress));
    }
}
