# Part 8 - Command Targets and Suggestions

This branch contains the completed **Admin Toolkit** plugin from Part 8 of
Kody Simpson's Paper plugin development series.

The project builds on command trees by using Paper's Minecraft-aware argument
types. Paper and the client now parse player selectors and game modes for us,
so commands get validation and tab suggestions without handwritten string
parsing.

## Commands

- `/admin heal` heals the executing player; `/admin heal <targets>` heals selected players.
- `/admin gamemode <mode>` changes the executing player's mode; append `<targets>` to change selected players.

The shorter routes have their own `.executes(...)` callbacks. They pass a one-player
list to the same action methods used by explicit targets. A normal console command
must specify targets because it has no player executor. An invalid explicit target
still produces an error; it never silently falls back to self.

Try player names as well as native selectors such as `@a`, `@p`, and `@s`.

This companion version extracts selector resolution into a shared helper and
formats game mode names in lowercase. Healing uses the player's maximum-health
attribute and also restores saturation; the recording uses `setHealth(20)`.
The command tree and argument-resolution concepts are the same.

Native selectors require the appropriate server permissions (the video uses
`op` on a local test server). This teaching example does not yet restrict the
admin command itself; do not deploy it unchanged on a public server.
[Part 9](../../tree/part-09-command-permissions) shows how to lock a command
like this down with a permission and `.requires(...)`.

## What this part teaches

- `ArgumentTypes.players()` and `PlayerSelectorArgumentResolver`
- Resolving a selector against the command's `CommandSourceStack`
- `ArgumentTypes.gameMode()` returning a real Bukkit `GameMode`
- Built-in client suggestions and validation from native argument types
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
