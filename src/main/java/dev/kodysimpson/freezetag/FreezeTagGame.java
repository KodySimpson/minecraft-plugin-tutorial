package dev.kodysimpson.freezetag;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

// The game owns the rules. The command and the listener only report what happened;
// this class decides what it means.
public final class FreezeTagGame {
    private final FreezeTagPlugin plugin;

    private GameState state = GameState.IDLE;

    // Everyone in the round, by UUID, like the cooldown Maps. Filled once when the round starts.
    private final Set<UUID> participants = new HashSet<>();
    // The runners who are frozen right now. The tagger is never in here.
    private final Set<UUID> frozen = new HashSet<>();
    private UUID taggerId;

    // Seconds left in the current phase: first the countdown, then the round.
    private int secondsLeft;
    // The one repeating task that runs the whole round.
    private BukkitTask timer;

    public FreezeTagGame(FreezeTagPlugin plugin) {
        this.plugin = plugin;
    }

    public void start(CommandSender sender) {
        if (state != GameState.IDLE) {
            sender.sendMessage(Component.text("A Freeze Tag round is already running.", NamedTextColor.RED));
            return;
        }

        // getOnlinePlayers() can't be read by position, so copy it into a List.
        List<Player> players = List.copyOf(plugin.getServer().getOnlinePlayers());
        if (players.size() < 2) {
            sender.sendMessage(Component.text("Freeze Tag needs at least two players online.", NamedTextColor.RED));
            return;
        }

        // Everyone online right now plays. Anyone who joins later isn't in the Set, so they stay out.
        for (Player player : players) {
            participants.add(player.getUniqueId());
        }

        // A random position from 0 up to (but not including) the number of players.
        Player tagger = players.get(ThreadLocalRandom.current().nextInt(players.size()));
        taggerId = tagger.getUniqueId();

        state = GameState.COUNTDOWN;
        secondsLeft = plugin.getConfig().getInt("countdown-seconds");

        // getName() is plain text, so unparsed stops a name from being read as MiniMessage tags.
        plugin.getServer().sendMessage(MiniMessage.miniMessage().deserialize(
                plugin.getConfig().getString("messages.start"),
                Placeholder.unparsed("tagger", tagger.getName())));

        // One timer for the whole game: tick() runs now, then every 20 ticks (1 second).
        timer = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> tick(), 0L, 20L);
    }

    public void stop(CommandSender sender) {
        if (state == GameState.IDLE) {
            sender.sendMessage(Component.text("There is no Freeze Tag round to stop.", NamedTextColor.RED));
            return;
        }
        endRound("messages.stopped");
    }

    // Runs once a second while a round is on. What it does depends on the state.
    private void tick() {
        if (state == GameState.COUNTDOWN && secondsLeft <= 0) {
            // The countdown is over. The same timer keeps going and now counts down the round.
            state = GameState.RUNNING;
            secondsLeft = plugin.getConfig().getInt("round-seconds");
            plugin.getServer().sendMessage(MiniMessage.miniMessage().deserialize(
                    plugin.getConfig().getString("messages.go")));
        } else if (state == GameState.RUNNING && secondsLeft <= 0) {
            endRound("messages.runners-win");
            return;
        }

        // An action bar fades after a couple of seconds, so sending it every second keeps it up.
        String path = state == GameState.COUNTDOWN ? "messages.countdown" : "messages.time-left";
        Component actionBar = MiniMessage.miniMessage().deserialize(
                plugin.getConfig().getString(path),
                Placeholder.unparsed("seconds", Integer.toString(secondsLeft)));
        for (UUID id : participants) {
            // getPlayer returns null for anyone offline. Quitters are removed from the Set,
            // so this is only a safety check.
            Player player = plugin.getServer().getPlayer(id);
            if (player != null) {
                player.sendActionBar(actionBar);
            }
        }
        secondsLeft--;
    }

    // Called by the listener when one participant hits another.
    public void handleHit(Player attacker, Player victim) {
        // Hits during the countdown don't count.
        if (state != GameState.RUNNING) {
            return;
        }

        UUID attackerId = attacker.getUniqueId();
        UUID victimId = victim.getUniqueId();

        if (attackerId.equals(taggerId)) {
            // The tagger freezes a runner. add() returns false if they were already frozen.
            if (frozen.add(victimId)) {
                plugin.getServer().sendMessage(MiniMessage.miniMessage().deserialize(
                        plugin.getConfig().getString("messages.tagged"),
                        Placeholder.unparsed("player", victim.getName()),
                        Placeholder.unparsed("tagger", attacker.getName())));
                checkForTaggerWin();
            }
        } else if (!frozen.contains(attackerId) && frozen.remove(victimId)) {
            // A runner who isn't frozen unfreezes a frozen teammate.
            // remove() returns false if the victim wasn't frozen, so nothing happens then.
            plugin.getServer().sendMessage(MiniMessage.miniMessage().deserialize(
                    plugin.getConfig().getString("messages.rescued"),
                    Placeholder.unparsed("player", victim.getName()),
                    Placeholder.unparsed("rescuer", attacker.getName())));
        }
    }

    public void handleQuit(Player player) {
        UUID id = player.getUniqueId();
        // remove() returns false if they weren't playing (or no round is on).
        if (!participants.remove(id)) {
            return;
        }
        frozen.remove(id);

        if (id.equals(taggerId)) {
            endRound("messages.tagger-left");
        } else if (participants.size() < 2) {
            // Only the tagger is left: nobody to chase, so nobody wins.
            endRound("messages.not-enough-players");
        } else {
            // The runner who left might have been the last one still running.
            checkForTaggerWin();
        }
    }

    private void checkForTaggerWin() {
        // Everyone except the tagger is a runner. If they're all frozen, the tagger wins.
        if (frozen.size() == participants.size() - 1) {
            endRound("messages.tagger-wins");
        }
    }

    // Who the move listener should hold in place: every participant during the countdown,
    // and only frozen runners once the round is running.
    public boolean isHeldInPlace(Player player) {
        if (state == GameState.COUNTDOWN) {
            return participants.contains(player.getUniqueId());
        }
        return frozen.contains(player.getUniqueId());
    }

    public boolean isParticipant(Player player) {
        return participants.contains(player.getUniqueId());
    }

    // Every way a round can end comes through here, so nothing gets forgotten:
    // stop the timer, forget the round and go back to IDLE.
    private void endRound(String messagePath) {
        timer.cancel();
        timer = null;
        participants.clear();
        frozen.clear();
        taggerId = null;
        state = GameState.IDLE;

        plugin.getServer().sendMessage(MiniMessage.miniMessage().deserialize(
                plugin.getConfig().getString(messagePath)));
    }
}
