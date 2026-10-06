package dev.kodysimpson.scheduling.commands;

import dev.kodysimpson.scheduling.SchedulingPlugin;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.IllegalPluginAccessException;

import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;

// Final example: main thread -> async file work -> main thread.
// Docs: https://docs.papermc.io/paper/dev/scheduler/#difference-between-synchronous-and-asynchronous-tasks
public final class ServerReportCommand implements BasicCommand {
    private final SchedulingPlugin plugin;

    public ServerReportCommand(SchedulingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (args.length != 0) {
            source.getSender().sendMessage(Component.text("Usage: /serverreport"));
            return;
        }

        // 1. MAIN THREAD: read the live server now, before starting the async task.
        // A String is a snapshot, not a live Player collection shared between threads.
        // Getters are not automatically async-safe: this collection is live server state.
        // https://jd.papermc.io/paper/26.3/org/bukkit/Server.html#getOnlinePlayers()
        var server = plugin.getServer();
        String report = "Minecraft version: " + server.getMinecraftVersion() + "\n"
                + "Online players: " + server.getOnlinePlayers().size() + "\n"
                + "Player limit: " + server.getMaxPlayers() + "\n";
        var reportsDirectory = plugin.getDataFolder().toPath().resolve("reports");
        var scheduler = server.getScheduler();
        var sender = source.getSender();
        var logger = plugin.getLogger();
        sender.sendMessage(Component.text("Saving a server report..."));

        scheduler.runTaskAsynchronously(plugin, () -> {
            // 2. ASYNC THREAD: real disk I/O, using only the captured text and path.
            // Do not move getOnlinePlayers(), world reads or inventory changes in here.
            // Default: keep live game APIs on the main thread unless documented safe.
            // Capturing a Player in a final/local variable would NOT copy its live state.
            String result;
            try {
                Files.createDirectories(reportsDirectory);
                // A unique filename keeps simultaneous requests from overwriting each other.
                // Despite its name, createTempFile does NOT automatically delete this report.
                var file = Files.createTempFile(reportsDirectory, "server-report-", ".txt");
                Files.writeString(file, report); // UTF-8; Java opens and closes the file for us.
                result = "Saved reports/" + file.getFileName();
            } catch (IOException exception) {
                // Java's Logger is documented as thread-safe, so logging here is OK.
                logger.log(Level.WARNING, "Could not save the server report", exception);
                result = "Could not save the report. Check the server console.";
            }

            var feedback = Component.text(result);
            try {
                // 3. MAIN THREAD: runTask queues our result handling back on the server.
                // The scheduler supports this handoff; only the inner callback runs there.
                // No get(), join() or sleep: neither thread waits for the other here.
                scheduler.runTask(plugin, () -> sender.sendMessage(feedback));
            } catch (IllegalPluginAccessException disabled) {
                // Shutdown edge case: a running file write can finish after plugin disable.
                // The scheduler then rejects new tasks; this plugin cannot queue a reply.
                logger.fine("Report finished after plugin disable; skipping feedback.");
            }
        });
    }

    @Override
    public String permission() { return "schedulingexamples.use"; }
}
