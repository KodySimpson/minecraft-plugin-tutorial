# Episode 13 Launch Pads

Completed example for Kody Simpson's Paper plugin tutorial: step on a pressure plate above a configured block to launch forward and upward, with a sound, takeoff particles, and a MiniMessage message.

Requires Java 25. This project pins Paper 26.3 build 40 alpha in its Gradle configuration.

## Build and run

On Windows, run `./gradlew.bat test build` to build and test, or `./gradlew.bat runServer` to start the development server. On macOS/Linux, use `./gradlew` instead.

The plugin JAR is created in `build/libs/`. After the first server start, edit `run/plugins/LaunchPads/config.yml` while the server is stopped, then restart. The bundled defaults do not overwrite an existing configuration.

Place a pressure plate on a gold block (or the configured material). Leave open space above and build a water landing area: normal fall damage still applies. Velocity strengths are not distances in blocks.

`/launchpad` displays setup instructions; it requires `launchpads.manage` (operators by default).

Tests cover the velocity calculation; they do not verify in-game visuals.
