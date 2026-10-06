package dev.kodysimpson.scheduling;

import dev.kodysimpson.scheduling.commands.CountdownCommand;
import dev.kodysimpson.scheduling.commands.ReminderCommand;
import dev.kodysimpson.scheduling.commands.RepeatCommand;
import dev.kodysimpson.scheduling.tasks.CountdownTask;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SchedulingTest {
    final SchedulingPlugin plugin = mock(SchedulingPlugin.class);
    final Server server = mock(Server.class);
    final BukkitScheduler scheduler = mock(BukkitScheduler.class);
    final CommandSourceStack source = mock(CommandSourceStack.class);
    final CommandSender sender = mock(CommandSender.class);

    SchedulingTest() {
        when(plugin.getServer()).thenReturn(server);
        when(server.getScheduler()).thenReturn(scheduler);
        when(source.getSender()).thenReturn(sender);
        when(sender.hasPermission("schedulingexamples.use")).thenReturn(true);
    }

    private CommandDispatcher<CommandSourceStack> reminderDispatcher() {
        var dispatcher = new CommandDispatcher<CommandSourceStack>();
        dispatcher.getRoot().addChild(ReminderCommand.create(plugin));
        return dispatcher;
    }

    @Test
    void reminderMessagesSenderOnlyWhenCallbackRuns() throws CommandSyntaxException {
        assertEquals(1, reminderDispatcher().execute("remind 5", source));
        var callback = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler).runTaskLater(eq(plugin), callback.capture(), eq(100L));
        verify(sender).sendMessage(Component.text("Reminder scheduled in 5 second(s)!"));
        verify(sender, never()).sendMessage(Component.text("Reminder: take a stretch break!"));
        callback.getValue().run();
        verify(sender).sendMessage(Component.text("Reminder: take a stretch break!"));
        verify(server, never()).broadcast(any(Component.class));
    }

    @Test
    void reminderConvertsDifferentDelaysWithoutIntegerOverflow() throws CommandSyntaxException {
        var dispatcher = reminderDispatcher();
        dispatcher.execute("remind 1", source);
        dispatcher.execute("remind 12", source);
        dispatcher.execute("remind 2147483647", source);
        verify(scheduler).runTaskLater(eq(plugin), any(Runnable.class), eq(20L));
        verify(scheduler).runTaskLater(eq(plugin), any(Runnable.class), eq(240L));
        verify(scheduler).runTaskLater(eq(plugin), any(Runnable.class), eq(42_949_672_940L));
    }

    @Test
    void reminderRejectsInvalidInputAndMissingPermission() {
        var dispatcher = reminderDispatcher();
        for (String input : new String[]{"remind", "remind 0", "remind -1", "remind 1.5",
                "remind hello", "remind 5 extra", "remind 2147483648"}) {
            assertThrows(CommandSyntaxException.class, () -> dispatcher.execute(input, source), input);
        }
        when(sender.hasPermission("schedulingexamples.use")).thenReturn(false);
        assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("remind 5", source));
        verifyNoInteractions(scheduler);
    }

    @Test
    void extraArgumentsDoNotScheduleAnything() {
        new RepeatCommand(plugin).execute(source, new String[]{"extra"});
        new CountdownCommand(plugin).execute(source, new String[]{"extra"});
        verifyNoInteractions(scheduler);
    }

    @Test
    void repeatingAnnouncementTogglesAndCanRestart() {
        BukkitTask task = mock(BukkitTask.class);
        when(scheduler.runTaskTimer(eq(plugin), any(Runnable.class), eq(0L), eq(100L)))
                .thenReturn(task);
        var command = new RepeatCommand(plugin);
        command.execute(source, new String[0]);
        command.execute(source, new String[0]);
        verify(task).cancel();
        verify(scheduler).runTaskTimer(eq(plugin), any(Runnable.class), eq(0L), eq(100L));
        command.execute(source, new String[0]);
        verify(scheduler, times(2)).runTaskTimer(eq(plugin), any(Runnable.class), eq(0L), eq(100L));
    }

    @Test
    void countdownSendsFiveThroughOneThenGoAndCancels() {
        try (var bukkit = mockStatic(Bukkit.class)) {
            bukkit.when(Bukkit::getScheduler).thenReturn(scheduler);
            BukkitTask task = mock(BukkitTask.class);
            when(task.getTaskId()).thenReturn(42);
            // Model the task handle becoming cancelled when BukkitRunnable.cancel() runs.
            doAnswer(invocation -> {
                when(task.isCancelled()).thenReturn(true);
                return null;
            }).when(scheduler).cancelTask(42);
            when(scheduler.runTaskTimer(eq(plugin), any(Runnable.class), eq(0L), eq(20L)))
                    .thenReturn(task);
            var command = new CountdownCommand(plugin);
            command.execute(source, new String[0]);
            command.execute(source, new String[0]); // Guard prevents overlapping countdowns.
            var callback = ArgumentCaptor.forClass(Runnable.class);
            verify(scheduler).runTaskTimer(eq(plugin), callback.capture(), eq(0L), eq(20L));
            assertInstanceOf(CountdownTask.class, callback.getValue());
            verify(server, never()).broadcast(any(Component.class));
            for (int i = 0; i < 6; i++) callback.getValue().run();
            var messages = ArgumentCaptor.forClass(Component.class);
            verify(server, times(6)).broadcast(messages.capture());
            for (int i = 0; i < 5; i++) {
                assertEquals(Component.text("Starting in " + (5 - i) + "..."), messages.getAllValues().get(i));
            }
            assertEquals(Component.text("Go!"), messages.getAllValues().get(5));
            verify(scheduler).cancelTask(42);
            command.execute(source, new String[0]);
            verify(scheduler, times(2)).runTaskTimer(eq(plugin), any(Runnable.class), eq(0L), eq(20L));
            verify(scheduler, times(2)).runTaskTimer(eq(plugin), callback.capture(), eq(0L), eq(20L));
            Runnable restarted = callback.getAllValues().getLast();
            assertNotSame(callback.getAllValues().getFirst(), restarted);
            restarted.run();
            verify(server, times(2)).broadcast(Component.text("Starting in 5..."));
        }
    }
}
