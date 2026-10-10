# Episode 18 Cooldowns

Completed example for Kody Simpson's Paper plugin tutorial:

- `/smite`: strikes real lightning on the block you're looking at, up to 50 blocks away. Each player then waits 10 seconds, and using it too soon tells them how many seconds are left. Looking at the sky doesn't use up the cooldown.
- Snowball wand: right-click while holding any blaze rod to throw a snowball. The wand recharges for 3 seconds, shown as a boss bar that drains and disappears when it's ready.

Both cooldowns are stored per player by UUID as the time the player last used them, so rejoining doesn't skip the wait. They live in memory, so a server restart clears them.

Real lightning damages whatever it hits and can start fires, so smite away from builds you care about.

Requires Java 25. This project pins Paper 26.3 build 151 beta in its Gradle configuration.

## Build and run

On Windows, run `./gradlew.bat test build` to build and test, or `./gradlew.bat runServer` to start the development server. On macOS/Linux, use `./gradlew` instead.

The plugin JAR is created in `build/libs/`. Join the development server with a compatible Minecraft client, then try `/smite` and `/give @s blaze_rod`. `/smite` requires `cooldowns.smite` (granted to all players by default).

Tests exercise the cooldowns, the wand and its boss bar with mocks; they do not verify lightning, snowballs or the boss bar in a running server.
