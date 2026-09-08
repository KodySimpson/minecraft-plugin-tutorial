# Part 7: Command Arguments

This self-contained Part 7 plugin teaches numbers, booleans, and text as typed input, building on Part 6's trees.

- `/heal` fully heals and feeds the player.
- `/heal <amount>` sets the player's health to the supplied amount, from 1–20 health points.
- `/movement` displays usage.
- `/movement walk <speed>` sets walk speed from 0 to 1.
- `/movement fly <speed>` sets fly speed from 0 to 1.
- `/glow <enabled>` turns your outline on with `true` and off with `false`.
- `/announce <message>` broadcasts the remaining text, including spaces.

Start with `HealCommand.java`, then combine arguments and literals in
`MovementCommand.java`. `BasicHealCommand.java` is an unregistered optional
comparison that shows the equivalent manual number parsing and validation.
The main class includes a commented registration line; use only one heal version at a time.
Then use `GlowCommand.java` for a boolean and `AnnounceCommand.java` for a greedy
string. The comments explain the new concepts without repeating every line of Java.

No permissions are introduced here. Player targets and suggestions follow in Part 8;
permission declarations and access checks follow in Part 9.
These unrestricted examples are for a private test server, not a public server deployment.

For recording: use a Survival player with missing health for healing, and Creative
flight to demonstrate fly speed. Setting fly speed does not grant flight.
Restore speeds with `/movement walk 0.2` and `/movement fly 0.1`.

For glow, switch to third-person (F5), compare `/glow true` with `/glow false`,
and try `/glow banana` to show rejected input. Reset with `/glow false`.
For text, try `/announce The potatoes have escaped` from the game and
`announce Hello from the console` from the server console. No quotes are needed:
`greedyString()` reads everything remaining, and quotation marks would be kept as text.
Bare `/glow` and `/announce` are incomplete because these roots have no executor.

Briefly contrast `word()` (one word), `string()` (one word or a quoted phrase),
and `greedyString()` (all remaining text). Mention `IntegerArgumentType` and
`LongArgumentType` for whole numbers; no extra command is needed just to list types.

Reference: [Paper's arguments and literals](https://docs.papermc.io/paper/dev/command-api/basics/arguments-and-literals/).

## Build and run

Paper API `26.2.build.119-stable`, Java 25, Gradle 9.7.1.

```powershell
.\gradlew.bat clean build
.\gradlew.bat runServer
```

Commands are registered through Paper's lifecycle API; there is no `commands:`
section in `plugin.yml`. The project uses the existing package/main-class naming
for continuity with the course files.
