# Part 7 - Command Targets and Suggestions

This branch contains the completed **Admin Toolkit** plugin from Part 7 of
Kody Simpson's Paper plugin development series.

The project builds on command trees by using Paper's Minecraft-aware argument
types. Paper and the client now parse player selectors and game modes for us,
so commands get validation and tab suggestions without handwritten string
parsing.

## Commands

- `/admin heal <targets>` heals one or more online players.
- `/admin gamemode <mode> <targets>` changes one or more players' game mode.

Try player names as well as native selectors such as `@a`, `@p`, and `@s`.
The sender needs both `admintoolkit.use` and Paper's
`minecraft.command.selector` permission to see and use the command tree.

## What this part teaches

- `ArgumentTypes.players()` and `PlayerSelectorArgumentResolver`
- Resolving a selector against the command's `CommandSourceStack`
- `ArgumentTypes.gameMode()` returning a real Bukkit `GameMode`
- Built-in client suggestions and validation from native argument types
- Restricting a command tree with plugin and selector permissions
- Registering Brigadier commands through `LifecycleEvents.COMMANDS`

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
