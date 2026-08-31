# Part 6: Brigadier Command Trees

This branch contains the completed **MovementCommands** plugin from Part 6 of
*Make a Minecraft Plugin in 2026*.

It introduces Paper's lifecycle command registration and Brigadier command
trees by building two typed commands:

- `/movement walk <speed>`
- `/movement fly <speed>`

The `speed` argument is a float from `0.0` through `1.0`. Both commands require
the `movementcommands.use` permission and can only be run by a player.

## What this part teaches

- Registering commands with `LifecycleEvents.COMMANDS`
- Building a command tree with literal and argument nodes
- Reading a typed Brigadier float argument
- Restricting an argument to a valid range
- Checking the command executor before using player-only methods
- Returning Brigadier success and failure result codes

There is intentionally no `commands:` section in `plugin.yml`; the commands are
registered through Paper's lifecycle API.

## Course target

- Paper 26.2 (API build 119)
- Java 25
- Gradle using the Kotlin DSL

## Build

```powershell
.\gradlew.bat clean build
```

The plugin JAR is generated in `build/libs`.

The series uses the [Paper developer documentation](https://docs.papermc.io/paper/dev/command-api/basics/registration/)
as its primary technical reference.
