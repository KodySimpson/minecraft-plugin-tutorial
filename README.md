# Episode 19 Build: Freeze Tag

Completed checkpoint build for Kody Simpson's Paper plugin tutorial: one round of Freeze Tag for everyone online.

- `/freezetag start`: everyone online plays, and one player is picked at random as the tagger.
- A 5-second countdown holds everyone in place, shown in the action bar.
- The tagger freezes runners by hitting them. Frozen runners can't move but can still look around.
- A runner who is still free can unfreeze a teammate by hitting them.
- The tagger wins by freezing every runner; the runners win if anyone is still free when time runs out.
- `/freezetag stop` ends a round early. The round also ends if the tagger leaves or fewer than two players are left.

The game is modeled as explicit states (`IDLE`, `COUNTDOWN`, `RUNNING`) in `FreezeTagGame`, with one repeating task running both the countdown and the round. Only hits between two players in the round count as tags; void, lava, fall damage and `/kill` work normally. The countdown and round lengths and every message are in `config.yml`, as MiniMessage.

This code is the course's reference version. Your project from the video may use different package or class names; the behavior is the same.

Requires Java 25. This project pins Paper 26.3 build 151 beta in its Gradle configuration.

## Build and run

On Windows, run `./gradlew.bat test build` to build and test, or `./gradlew.bat runServer` to start the development server. On macOS/Linux, use `./gradlew` instead.

The plugin JAR is created in `build/libs/`. You need at least two players on the server to start a round; `/freezetag` requires `freezetag.admin` (operators by default).

Known limits: cancelling movement doesn't stop teleports such as ender pearls or `/tp`, and frozen players can still use items. Tests exercise the game rules with mocks; they don't verify the game in a running server.
