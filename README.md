# Part 5 — Simple Commands

This branch contains the finished `ServerUtilities` plugin from Part 5 of
Kody Simpson's Paper plugin development series.

## What this part teaches

- Paper's `BasicCommand` interface
- registering commands in `onEnable`
- the difference between the command sender and executor
- player-only and console-safe command behavior
- clear Component feedback
- connecting a command to an event-driven result

## Commands

| Command | Purpose |
| --- | --- |
| `/heal` | Restores the executing player's health, hunger, and fire state |
| `/serverinfo` | Shows the Minecraft version, player count, and loaded-world count |
| `/potato` | Drops a potato four blocks ahead that kills a player when they pick it up |

## Requirements

- Java 25
- Paper 26.2

## Build and run

```powershell
.\gradlew.bat clean build
.\gradlew.bat runServer
```

## Try this

Add a console-safe `/online` command that lists the names of online players.

The potato listener intentionally treats every potato as dangerous. Later, the
custom-items part will show how PDC can identify only the plugin's special item.
