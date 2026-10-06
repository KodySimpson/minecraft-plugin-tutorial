# Episode 15 Scheduled Tasks

Completed example for Kody Simpson's Paper plugin tutorial:

- `/remind <seconds>`: a one-time delayed task that sends you a reminder later.
- `/repeat`: starts a repeating server-wide announcement; run it again to stop it.
- `/countdown`: a five-second countdown task that cancels itself when it reaches zero.
- `/serverreport`: reads server details on the main thread, saves them to `plugins/SchedulingExamples/reports/` on an async thread, then replies back on the main thread.

Requires Java 25. This project pins Paper 26.3 build 135 beta in its Gradle configuration.

## Build and run

On Windows, run `./gradlew.bat test build` to build and test, or `./gradlew.bat runServer` to start the development server. On macOS/Linux, use `./gradlew` instead.

The plugin JAR is created in `build/libs/`. The commands require `schedulingexamples.use` (operators by default).

Tests exercise the scheduling and report logic with a mocked Bukkit scheduler; they do not verify in-game timing.
