# Episode 14 Dialog API

Completed example for Kody Simpson's Paper plugin tutorial, limited to the recorded command flow:

- `/rules`: a notice with a title, rule text, and a book displayed in the body.
- `/healmenu`: confirmation buttons, a one-use callback, and a health slider whose value is read and validated on the server.

The plugin also adds an example link to Minecraft's Server Links screen. Replace `https://example.com` in the main plugin class with your own website.

Requires Java 25. This project pins Paper 26.3 build 135 beta in its Gradle configuration.

## Build and run

On Windows, run `./gradlew.bat test build` to build and test, or `./gradlew.bat runServer` to start the development server. On macOS/Linux, use `./gradlew` instead.

The plugin JAR is created in `build/libs/`. Join the development server with a compatible Minecraft client to try the dialogs. Healing requires `dialogexamples.use` (operators by default).

Tests exercise command and callback behavior with mocks; they do not verify the Minecraft GUI or callback expiration in a running server.
