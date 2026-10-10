package dev.kodysimpson.cooldowns;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SmiteCommand implements BasicCommand {
    // How long a player waits between smites.
    static final long COOLDOWN_SECONDS = 10;

    // Each player's UUID, mapped to the time they last used /smite (a System.currentTimeMillis() value).
    // A player with no entry hasn't used /smite since the server started.
    private final Map<UUID, Long> lastUsedTimes = new HashMap<>();

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getExecutor() instanceof Player player)) {
            source.getSender().sendMessage(Component.text("Only a player can smite."));
            return;
        }

        // A UUID identifies the player's account. It stays the same when they rejoin,
        // so logging out and back in can't skip the wait.
        UUID playerId = player.getUniqueId();

        // When did this player last smite? null means they haven't since the server started.
        Long lastUsed = lastUsedTimes.get(playerId);
        if (lastUsed != null) {
            // currentTimeMillis is the current time in milliseconds. 1 second = 1,000 milliseconds.
            // It follows the computer's clock, so if someone changes the clock, a cooldown can
            // end early or late. For a smite command, that's fine.
            long elapsed = System.currentTimeMillis() - lastUsed;
            long cooldownMillis = COOLDOWN_SECONDS * 1000;

            if (elapsed < cooldownMillis) {
                // Round up, so 0.2 seconds left shows as 1s instead of 0s.
                long secondsLeft = (long) Math.ceil((cooldownMillis - elapsed) / 1000.0);
                player.sendMessage(Component.text("You can smite again in " + secondsLeft + "s.", NamedTextColor.RED));
                return;
            }
        }

        // The block the player is looking at, up to 50 blocks away.
        // It's null when there's no block in range, for example when looking at the sky.
        Block target = player.getTargetBlockExact(50);
        if (target == null) {
            player.sendMessage(Component.text("Look at a block to smite it.", NamedTextColor.RED));
            return; // Nothing happened, so don't start the cooldown.
        }

        // Real lightning: it damages anything it hits (including you, if you smite your own feet)
        // and can set blocks on fire. strikeLightningEffect would be the flash and thunder only.
        player.getWorld().strikeLightning(target.getLocation());

        // Only record the time once the smite actually happened.
        // put() replaces this player's previous time.
        lastUsedTimes.put(playerId, System.currentTimeMillis());
    }

    @Override
    public String permission() {
        return "cooldowns.smite";
    }

    // Cooldowns live only in memory: a server restart clears them. Saving them is a later lesson.
}
