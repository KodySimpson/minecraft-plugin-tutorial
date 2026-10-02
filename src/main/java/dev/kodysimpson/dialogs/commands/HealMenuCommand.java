package dev.kodysimpson.dialogs.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.List;

public final class HealMenuCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (args.length != 0) {
            source.getSender().sendMessage(Component.text("Usage: /healmenu", NamedTextColor.RED));
            return;
        }
        if (!(source.getExecutor() instanceof Player player)) {
            source.getSender().sendMessage(Component.text("Only a player can open a dialog.", NamedTextColor.RED));
            return;
        }

        // Keep just the opener's ID, rather than retaining their whole Player in the callback.
        var openerId = player.getUniqueId();
        // A callback is code Paper calls later when the button is clicked.
        // Build a fresh callback each time this player opens the menu.
        // Docs: https://docs.papermc.io/paper/dev/dialogs/#using-callbacks
        DialogAction healAction = DialogAction.customClick((response, audience) -> {
            // Use the player who submitted the action. A displayed button does not grant permission.
            if (!(audience instanceof Player clickingPlayer)
                    || !clickingPlayer.getUniqueId().equals(openerId)) return;
            if (!clickingPlayer.hasPermission("dialogexamples.use")) {
                clickingPlayer.sendMessage(Component.text("You no longer have permission to heal.", NamedTextColor.RED));
                return;
            }

            // "health" matches the input key below. Number-range inputs return a Float.
            Float health = response.getFloat("health");
            // Validate on the server too: a modified client can bypass the slider's bounds.
            if (health == null || !Float.isFinite(health) || health < 1 || health > 20) {
                clickingPlayer.sendMessage(Component.text("Choose health between 1 and 20.", NamedTextColor.RED));
                return;
            }
            // Another plugin may change maximum health while the dialog is open.
            var maxHealth = clickingPlayer.getAttribute(Attribute.MAX_HEALTH);
            if (maxHealth == null || health > maxHealth.getValue() || clickingPlayer.isDead()) {
                clickingPlayer.sendMessage(Component.text("That health cannot be applied right now.", NamedTextColor.RED));
                return;
            }
            clickingPlayer.setHealth(health); // Set the total, rather than adding this amount.
            clickingPlayer.setFoodLevel(20);
            clickingPlayer.setFireTicks(0);
            clickingPlayer.sendMessage(Component.text("Health set to " + health + "!", NamedTextColor.GREEN));
        }, ClickCallback.Options.builder()
                .uses(1) // This particular button action can run once.
                .lifetime(Duration.ofMinutes(5)) // After five minutes, reopen the command for a fresh action.
                .build());

        Dialog dialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Heal yourself?", NamedTextColor.GOLD))
                        .body(List.of(DialogBody.plainMessage(Component.text(
                                "Choose your health (2 points = 1 heart). Hunger and fire are also restored."
                        ))))
                        .inputs(List.of(
                                // First input example: add this to the confirmation we already built.
                                // The key connects this slider to response.getFloat("health").
                                DialogInput.numberRange("health", Component.text("Health"), 1.0F, 20.0F)
                                        .step(1.0F) // One health point = half a heart. Avoid 0 (death).
                                        .initial(20.0F)
                                        .width(300)
                                        .build()
                        ))
                        .build())
                // Confirmation always has a positive and a negative button.
                .type(DialogType.confirmation(
                        ActionButton.builder(Component.text("Heal me", NamedTextColor.GREEN))
                                .tooltip(Component.text("Set your health to the selected amount."))
                                .action(healAction)
                                .build(),
                        // With no action, Cancel simply closes the dialog. Escape also cancels.
                        ActionButton.builder(Component.text("Cancel", NamedTextColor.RED)).build()
                ))
        );
        player.showDialog(dialog);
    }

    @Override
    public String permission() {
        return "dialogexamples.use";
    }
}
