# Part 5 — Simple Commands

This branch contains the finished `ServerUtilities` plugin from Part 5 of
Kody Simpson's Paper plugin development series.

## What this part teaches

- Paper's `BasicCommand` interface
- registering commands in `onEnable`
- the difference between the command sender and executor
- player-only and console-safe command behavior
- permissions and Component feedback

## Commands

| Command | Purpose | Permission |
| --- | --- | --- |
| `/heal` | Restores the executing player's health, hunger, and fire state | `serverutilities.heal` |
| `/serverinfo` | Shows the Minecraft version, player count, and loaded-world count | None |

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
