package dev.kodysimpson.dialogs.commands;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryBuilderFactory;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.InlinedRegistryBuilderProvider;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.DialogInstancesProvider;
import io.papermc.paper.registry.data.dialog.DialogRegistryEntry;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.action.DialogActionCallback;
import io.papermc.paper.registry.data.dialog.body.PlainMessageDialogBody;
import io.papermc.paper.registry.data.dialog.body.ItemDialogBody;
import io.papermc.paper.registry.data.dialog.input.NumberRangeDialogInput;
import io.papermc.paper.registry.data.dialog.type.ConfirmationType;
import io.papermc.paper.registry.data.dialog.type.NoticeType;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.Registry;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test-only plumbing for Paper's server-provided builders. The learner code
 * contains no mock providers or validation helpers. Tests invoke the callbacks
 * captured from the real commands, not copies of their business logic.
 * GUI drawing, packets, and the server's expiry manager still need client tests.
 */
@SuppressWarnings({"unchecked", "rawtypes", "removal"})
class DialogCommandsTest {
    private MockedStatic<RegistryAccess> registryScope;
    private MockedStatic<DialogInstancesProvider> instancesScope;
    private MockedStatic<InlinedRegistryBuilderProvider> inlinedScope;
    private DialogInstancesProvider instances;
    private Dialog dialog;
    private Player player;
    private CommandSourceStack source;
    private final List<DialogActionCallback> callbacks = new ArrayList<>();
    private final List<ClickCallback.Options> callbackOptions = new ArrayList<>();

    @BeforeEach
    void preparePaperBuilderStubs() {
        // Dialog's built-in constants look up registries on class initialization.
        registryScope = mockStatic(RegistryAccess.class);
        RegistryAccess access = mock(RegistryAccess.class);
        registryScope.when(RegistryAccess::registryAccess).thenReturn(access);
        // Lazy test registry avoids initializing Bukkit's legacy registries
        // before RegistryAccess is installed. No actual registry data is used.
        doAnswer(invocation -> new EmptyRegistry()).when(access).getRegistry(any(RegistryKey.class));
        doAnswer(invocation -> new EmptyRegistry()).when(access).getRegistry(any(Class.class));
        dialog = mock(Dialog.class);

        instances = mock(DialogInstancesProvider.class);
        instancesScope = mockStatic(DialogInstancesProvider.class);
        instancesScope.when(DialogInstancesProvider::instance).thenReturn(instances);

        DialogBase.Builder base = mock(DialogBase.Builder.class, RETURNS_SELF);
        when(base.build()).thenReturn(mock(DialogBase.class));
        when(instances.dialogBaseBuilder(any())).thenReturn(base);
        when(instances.plainMessageDialogBody(any())).thenAnswer(invocation -> mock(PlainMessageDialogBody.class));
        when(instances.notice()).thenReturn(mock(NoticeType.class));
        when(instances.confirmation(any(), any())).thenReturn(mock(ConfirmationType.class));
        when(instances.actionButtonBuilder(any())).thenAnswer(invocation -> {
            ActionButton.Builder button = mock(ActionButton.Builder.class, RETURNS_SELF);
            when(button.build()).thenReturn(mock(ActionButton.class));
            return button;
        });
        NumberRangeDialogInput.Builder slider = mock(NumberRangeDialogInput.Builder.class, RETURNS_SELF);
        when(slider.build()).thenReturn(mock(NumberRangeDialogInput.class));
        when(instances.numberRangeBuilder(anyString(), any(), anyFloat(), anyFloat())).thenReturn(slider);
        when(instances.register(any(), any())).thenAnswer(invocation -> {
            callbacks.add(invocation.getArgument(0));
            callbackOptions.add(invocation.getArgument(1));
            return mock(DialogAction.CustomClickAction.class);
        });

        InlinedRegistryBuilderProvider inlined = mock(InlinedRegistryBuilderProvider.class);
        inlinedScope = mockStatic(InlinedRegistryBuilderProvider.class);
        inlinedScope.when(InlinedRegistryBuilderProvider::instance).thenReturn(inlined);
        when(inlined.createDialog(any())).thenAnswer(invocation -> {
            Consumer<RegistryBuilderFactory<Dialog, ? extends DialogRegistryEntry.Builder>> consumer = invocation.getArgument(0);
            RegistryBuilderFactory factory = mock(RegistryBuilderFactory.class);
            when(factory.empty()).thenReturn(mock(DialogRegistryEntry.Builder.class, RETURNS_SELF));
            consumer.accept(factory);
            return dialog;
        });

        player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.hasPermission("dialogexamples.use")).thenReturn(true);
        source = mock(CommandSourceStack.class);
        when(source.getExecutor()).thenReturn(player);
        when(source.getSender()).thenReturn(player);
    }

    @AfterEach
    void closeProviderStubs() {
        if (inlinedScope != null) inlinedScope.close();
        if (instancesScope != null) instancesScope.close();
        if (registryScope != null) registryScope.close();
    }

    @Test
    void rulesShowsNoticeWithBookAndThreeRulesAndNoCallback() {
        ItemStack book = mock(ItemStack.class);
        ItemDialogBody.Builder item = mock(ItemDialogBody.Builder.class, RETURNS_SELF);
        when(item.build()).thenReturn(mock(ItemDialogBody.class));
        when(instances.itemDialogBodyBuilder(book)).thenReturn(item);
        // ItemStack.of needs a live server; isolate only that factory in this unit test.
        try (MockedStatic<ItemStack> items = mockStatic(ItemStack.class)) {
            items.when(() -> ItemStack.of(Material.BOOK)).thenReturn(book);
            new RulesCommand().execute(source, new String[0]);
            items.verify(() -> ItemStack.of(Material.BOOK));
        }
        verify(player).showDialog(dialog);
        verify(instances).notice();
        verify(instances, times(4)).plainMessageDialogBody(any());
        verify(instances).itemDialogBodyBuilder(book);
        verify(item).description(any(PlainMessageDialogBody.class));
        verify(item).showTooltip(true);
        verify(item).build();
        verify(player, never()).getInventory();
        assertTrue(callbacks.isEmpty());
        assertNull(new RulesCommand().permission());
    }

    @Test
    void commandsRejectConsoleAndUnexpectedArgumentsBeforeBuilding() {
        CommandSender console = mock(CommandSender.class);
        when(source.getExecutor()).thenReturn(null);
        when(source.getSender()).thenReturn(console);
        new RulesCommand().execute(source, new String[0]);
        new HealMenuCommand().execute(source, new String[0]);
        verify(console, times(2)).sendMessage(any(Component.class));
        verifyNoInteractions(instances);

        when(source.getExecutor()).thenReturn(player);
        when(source.getSender()).thenReturn(player);
        new RulesCommand().execute(source, new String[]{"extra"});
        new HealMenuCommand().execute(source, new String[]{"extra"});
        verify(player, never()).showDialog(any());
        assertTrue(callbacks.isEmpty());
    }

    @Test
    void healWaitsForClickAndSetsSelectedHealth() {
        AttributeInstance health = mock(AttributeInstance.class);
        when(health.getValue()).thenReturn(40.0);
        when(player.getAttribute(any())).thenReturn(health);
        new HealMenuCommand().execute(source, new String[0]);
        verify(player, never()).setHealth(anyDouble());
        DialogResponseView response = mock(DialogResponseView.class);
        when(response.getFloat("health")).thenReturn(12.0F);
        callbacks.getFirst().accept(response, player);
        verify(player).setHealth(12.0);
        verify(player).setFoodLevel(20);
        verify(player).setFireTicks(0);
        assertEquals("dialogexamples.use", new HealMenuCommand().permission());
    }

    @Test
    void invalidHealthDoesNotChangePlayer() {
        new HealMenuCommand().execute(source, new String[0]);
        for (Float value : new Float[]{null, Float.NaN, Float.POSITIVE_INFINITY, 0.0F, 21.0F}) {
            DialogResponseView response = mock(DialogResponseView.class);
            when(response.getFloat("health")).thenReturn(value);
            callbacks.getFirst().accept(response, player);
        }
        verify(player, never()).setHealth(anyDouble());
        verify(player, never()).setFoodLevel(anyInt());
    }

    @Test
    void eachOpenRegistersAFreshOneUseFiveMinuteCallback() {
        new HealMenuCommand().execute(source, new String[0]);
        new HealMenuCommand().execute(source, new String[0]);
        assertEquals(2, callbacks.size());
        assertNotSame(callbacks.get(0), callbacks.get(1));
        for (ClickCallback.Options options : callbackOptions) {
            assertEquals(1, options.uses());
            assertEquals(Duration.ofMinutes(5), options.lifetime());
        }
    }

    @Test
    void permissionLossAfterOpeningPreventsHealing() {
        new HealMenuCommand().execute(source, new String[0]);
        when(player.hasPermission("dialogexamples.use")).thenReturn(false);
        for (DialogActionCallback callback : callbacks) callback.accept(mock(DialogResponseView.class), player);
        verify(player, never()).setHealth(anyDouble());
        verify(player, never()).setFoodLevel(anyInt());
        verify(player, never()).setFireTicks(anyInt());
    }

    @Test
    void anotherPlayerOrNonPlayerAudienceCannotApplyOpenersActions() {
        new HealMenuCommand().execute(source, new String[0]);
        Player another = mock(Player.class);
        when(another.getUniqueId()).thenReturn(UUID.randomUUID());
        for (DialogActionCallback callback : callbacks) {
            callback.accept(mock(DialogResponseView.class), another);
            callback.accept(mock(DialogResponseView.class), Audience.empty());
        }
        verify(another, never()).setHealth(anyDouble());
    }

    private static class EmptyRegistry extends Registry.NotARegistry<Keyed> {
        @Override public Keyed get(NamespacedKey key) { return null; }
        @Override public Keyed getOrThrow(Key key) { return null; }
        @Override public Keyed getOrThrow(NamespacedKey key) { return null; }
        @Override public Iterator<Keyed> iterator() { return List.<Keyed>of().iterator(); }
    }
}
