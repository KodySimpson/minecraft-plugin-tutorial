package dev.kodysimpson.kitpicker;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class KitPickerTest {
    final KitPickerPlugin plugin = mock(KitPickerPlugin.class);
    final Server server = mock(Server.class);
    final BukkitScheduler scheduler = mock(BukkitScheduler.class);
    final Player player = mock(Player.class);
    final PlayerInventory playerInventory = mock(PlayerInventory.class);
    final Inventory menuInventory = mock(Inventory.class);
    final KitMenu menu = mock(KitMenu.class);
    final ItemStack item = mock(ItemStack.class);
    final KitMenuListener listener = new KitMenuListener(plugin);

    KitPickerTest() {
        when(plugin.getServer()).thenReturn(server);
        when(server.getScheduler()).thenReturn(scheduler);
        when(player.getInventory()).thenReturn(playerInventory);
        when(playerInventory.addItem(any(ItemStack.class), any(ItemStack.class))).thenReturn(new HashMap<>());
        when(menuInventory.getHolder(false)).thenReturn(menu);
    }

    MockedStatic<ItemStack> stubItems() {
        var items = mockStatic(ItemStack.class);
        items.when(() -> ItemStack.of(any(Material.class))).thenReturn(item);
        items.when(() -> ItemStack.of(any(Material.class), anyInt())).thenReturn(item);
        return items;
    }

    InventoryClickEvent click(Inventory top, int rawSlot) {
        var event = mock(InventoryClickEvent.class);
        when(event.getInventory()).thenReturn(top);
        when(event.getRawSlot()).thenReturn(rawSlot);
        when(event.getWhoClicked()).thenReturn(player);
        return event;
    }

    @Test
    void menuIsHeldByKitMenuAndShowsTheIcon() {
        try (var items = stubItems()) {
            when(server.createInventory(any(InventoryHolder.class), eq(27), any(Component.class)))
                    .thenReturn(menuInventory);
            var created = new KitMenu(plugin);
            verify(server).createInventory(same(created), eq(27), any(Component.class));
            assertSame(menuInventory, created.getInventory());
            verify(menuInventory).setItem(KitMenu.TRAILBLAZER_SLOT, item);
        }
    }

    @Test
    void clickingTheIconGivesTheKitAndClosesOnTheNextTick() {
        try (var items = stubItems()) {
            var event = click(menuInventory, KitMenu.TRAILBLAZER_SLOT);
            listener.onMenuClick(event);

            verify(event).setCancelled(true);
            verify(playerInventory).addItem(item, item);
            // Not closed during the event; closed by the scheduled task.
            verify(player, never()).closeInventory();
            var task = ArgumentCaptor.forClass(Runnable.class);
            verify(scheduler).runTask(eq(plugin), task.capture());
            task.getValue().run();
            verify(player).closeInventory();
        }
    }

    @Test
    void clicksInThePlayersOwnInventoryAreBlockedButAreNotButtons() {
        // Raw slot 40 is in the bottom (player) inventory of a 27-slot chest window,
        // for example a shift-click that would move an item up into the menu.
        var event = click(menuInventory, 40);
        listener.onMenuClick(event);

        verify(event).setCancelled(true);
        verifyNoInteractions(playerInventory, scheduler);
    }

    @Test
    void emptyMenuSlotsAndClicksOutsideTheWindowDoNothing() {
        for (int rawSlot : new int[] {0, 26, -999}) {
            var event = click(menuInventory, rawSlot);
            listener.onMenuClick(event);
            verify(event).setCancelled(true);
        }
        verifyNoInteractions(playerInventory, scheduler);
    }

    @Test
    void otherInventoriesAreLeftAlone() {
        var chest = mock(Inventory.class);
        when(chest.getHolder(false)).thenReturn(mock(InventoryHolder.class));
        var event = click(chest, KitMenu.TRAILBLAZER_SLOT);
        listener.onMenuClick(event);

        verify(event, never()).setCancelled(anyBoolean());
        verifyNoInteractions(playerInventory, scheduler);
    }

    @Test
    void fullInventoryStillClosesTheMenuWithAWarning() {
        try (var items = stubItems()) {
            when(playerInventory.addItem(any(ItemStack.class), any(ItemStack.class)))
                    .thenReturn(new HashMap<>(Map.of(1, item)));
            listener.onMenuClick(click(menuInventory, KitMenu.TRAILBLAZER_SLOT));

            verify(player).sendMessage(argThat((Component message) ->
                    message.toString().contains("didn't fit")));
            verify(scheduler).runTask(eq(plugin), any(Runnable.class));
        }
    }

    @Test
    void draggingIsBlockedOnlyWhileTheMenuIsOpen() {
        var menuDrag = mock(InventoryDragEvent.class);
        when(menuDrag.getInventory()).thenReturn(menuInventory);
        listener.onMenuDrag(menuDrag);
        verify(menuDrag).setCancelled(true);

        var chest = mock(Inventory.class);
        var otherDrag = mock(InventoryDragEvent.class);
        when(otherDrag.getInventory()).thenReturn(chest);
        listener.onMenuDrag(otherDrag);
        verify(otherDrag, never()).setCancelled(anyBoolean());
    }

    @Test
    void consoleCannotOpenTheMenu() {
        var source = mock(CommandSourceStack.class);
        var console = mock(CommandSender.class);
        when(source.getSender()).thenReturn(console);
        when(source.getExecutor()).thenReturn(null); // the console is not an entity

        new KitsCommand(plugin).execute(source, new String[0]);

        verify(console).sendMessage(Component.text("Only a player can open the kit menu."));
        assertEquals("kitpicker.use", new KitsCommand(plugin).permission());
    }
}
