# Part 4: Controlling Events

This part builds `cancelling-events`, a small plugin with two easy-to-test rules:

- A player holding a shield takes no fall damage.
- A sneaking player does not randomly teleport after eating chorus fruit, and
  instead sees portal particles and hears a teleport sound.

## What you learn

- How to check whether an event is cancellable
- How cancelling an event prevents its normal server behavior
- How to filter a broad event down to the exact situation you want
- How an event cause identifies the specific behavior that triggered an event

## Code tour

1. `CancellingEvents` registers `EatingListener` during `onEnable`.
2. `onPlayerFall` filters `EntityDamageEvent` to a player taking fall damage.
3. The handler checks both hands for a shield before cancelling that damage.
4. `onTeleport` filters `PlayerTeleportEvent` to random teleportation
   caused by a consumable, then cancels it while the player is sneaking.
5. The handlers send visible feedback with a message, particles, and a sound.

In vanilla Minecraft, chorus fruit is the normal item that causes a consumable
random teleport. Paper uses the broader `CONSUMABLE_EFFECT` cause because custom
items can use the same effect.

Permissions, configuration, and custom items are deliberately left for later
parts. These rules work for every player.

## Requirements

- Java 25
- IntelliJ IDEA
- The Minecraft Development plugin for IntelliJ IDEA

## Build the plugin

On Windows:

```powershell
.\gradlew.bat build
```

Run a local Paper test server:

```powershell
.\gradlew.bat runServer
```

Type `stop` in the server console to shut the server down cleanly.

## Manual test checklist

- Fall without a shield: ordinary fall damage still happens.
- Hold a shield in either hand and fall: the damage is cancelled.
- Take another kind of damage while holding a shield: it is not cancelled.
- Eat chorus fruit normally: its random teleport still happens.
- Hold sneak while eating chorus fruit: the random teleport is cancelled.
- Use an unrelated teleport while sneaking: it still works.

## Try it yourself

Change the fall-damage rule so the shield must be in the player's offhand.
