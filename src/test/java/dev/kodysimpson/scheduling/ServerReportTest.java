package dev.kodysimpson.scheduling;

import dev.kodysimpson.scheduling.commands.ServerReportCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ServerReportTest {
    @TempDir Path directory;
    final SchedulingPlugin plugin = mock(SchedulingPlugin.class);
    final Server server = mock(Server.class);
    final BukkitScheduler scheduler = mock(BukkitScheduler.class);
    final CommandSourceStack source = mock(CommandSourceStack.class);
    final CommandSender sender = mock(CommandSender.class);
    final Logger logger = mock(Logger.class);

    @BeforeEach
    void setup() {
        when(plugin.getServer()).thenReturn(server);
        when(plugin.getDataFolder()).thenReturn(directory.toFile());
        when(plugin.getLogger()).thenReturn(logger);
        when(server.getScheduler()).thenReturn(scheduler);
        when(server.getMinecraftVersion()).thenReturn("26.3");
        when(server.getMaxPlayers()).thenReturn(20);
        doReturn(List.of(mock(Player.class))).when(server).getOnlinePlayers();
        when(source.getSender()).thenReturn(sender);
    }

    private Runnable startReport() {
        new ServerReportCommand(plugin).execute(source, new String[0]);
        var work = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler).runTaskAsynchronously(eq(plugin), work.capture());
        return work.getValue();
    }

    // The test runner may wait; production command code never blocks for completion.
    private void onWorker(Runnable work) throws Exception {
        try (var executor = Executors.newSingleThreadExecutor()) {
            executor.submit(work).get(5, TimeUnit.SECONDS);
        }
    }

    @Test
    void snapshotThenRealDiskWriteThenQueuedFeedback() throws Exception {
        Runnable work = startReport();
        assertFalse(Files.exists(directory.resolve("reports")), "No disk I/O inside execute");
        verify(sender).sendMessage(Component.text("Saving a server report..."));
        // Change the live value AFTER capture: the file must still contain the old snapshot.
        when(server.getMaxPlayers()).thenReturn(99);
        clearInvocations(server, source, sender, plugin);
        onWorker(work);
        verifyNoInteractions(server, source, sender, plugin);
        try (var files = Files.list(directory.resolve("reports"))) {
            var reports = files.toList();
            assertEquals(1, reports.size());
            assertEquals("Minecraft version: 26.3\nOnline players: 1\nPlayer limit: 20\n",
                    Files.readString(reports.getFirst()));
            var completion = ArgumentCaptor.forClass(Runnable.class);
            verify(scheduler).runTask(eq(plugin), completion.capture());
            completion.getValue().run();
            verify(sender).sendMessage(Component.text("Saved reports/" + reports.getFirst().getFileName()));
        }
    }

    @Test
    void concurrentReportsUseSeparateFiles() throws Exception {
        var command = new ServerReportCommand(plugin);
        command.execute(source, new String[0]);
        command.execute(source, new String[0]);
        var work = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler, times(2)).runTaskAsynchronously(eq(plugin), work.capture());
        try (var executor = Executors.newFixedThreadPool(2)) {
            var first = executor.submit(work.getAllValues().get(0));
            var second = executor.submit(work.getAllValues().get(1));
            first.get(5, TimeUnit.SECONDS);
            second.get(5, TimeUnit.SECONDS);
        }
        try (var files = Files.list(directory.resolve("reports"))) {
            var reports = files.toList();
            assertEquals(2, reports.size());
            assertEquals(Files.readString(reports.get(0)), Files.readString(reports.get(1)));
        }
    }

    @Test
    void ioFailureQueuesFailureNotSuccess() throws Exception {
        Files.writeString(directory.resolve("reports"), "A file blocking the directory");
        Runnable work = startReport();
        clearInvocations(sender);
        onWorker(work);
        verify(logger).log(eq(Level.WARNING), eq("Could not save the server report"), any(Throwable.class));
        verifyNoInteractions(sender);
        var completion = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler).runTask(eq(plugin), completion.capture());
        completion.getValue().run();
        verify(sender).sendMessage(Component.text("Could not save the report. Check the server console."));
        assertEquals("A file blocking the directory", Files.readString(directory.resolve("reports")));
    }

    @Test
    void disabledPluginSkipsFeedbackWithoutDiscardingCompletedFile() throws Exception {
        when(scheduler.runTask(eq(plugin), any(Runnable.class)))
                .thenThrow(new IllegalPluginAccessException("disabled"));
        Runnable work = startReport();
        clearInvocations(sender);
        onWorker(work);
        verifyNoInteractions(sender);
        verify(logger).fine("Report finished after plugin disable; skipping feedback.");
        try (var files = Files.list(directory.resolve("reports"))) {
            assertEquals(1, files.count());
        }
    }

    @Test
    void extraArgumentsDoNotScheduleAndPermissionRemainsOperatorOnly() {
        var command = new ServerReportCommand(plugin);
        command.execute(source, new String[]{"extra"});
        verifyNoInteractions(scheduler);
        verify(sender).sendMessage(Component.text("Usage: /serverreport"));
        assertEquals("schedulingexamples.use", command.permission());
    }
}
