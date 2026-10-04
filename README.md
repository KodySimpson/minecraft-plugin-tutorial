# Episode 12 MiniMessage

Completed example for Kody Simpson's Paper plugin tutorial: the join welcome and `/snack` messages from Part 11, now written as MiniMessage templates in `config.yml`. The welcome inserts the player's name, and the snack message uses a gradient heading, the actual cookie amount, a hover tooltip and a click suggestion.

Requires Java 25. This project pins Paper API 26.2 build 119 in its Gradle configuration.

## Build and run

On Windows, run `./gradlew.bat test build` to build and test, or `./gradlew.bat runServer` to start the development server. On macOS/Linux, use `./gradlew` instead.

The plugin JAR is created in `build/libs/`. After the first server start, edit `run/plugins/JoinMessages/config.yml` while the server is stopped, then restart. The bundled defaults do not overwrite an existing configuration, so add new keys manually if you reuse an older run folder.

Join the server to see the welcome, then run `/snack`. Hover over **[More cookies]** to see the tooltip; clicking it only suggests `/snack` in chat and does not run it.

Templates are trusted, administrator-authored markup. Dynamic values such as the player's name are inserted with `Placeholder.unparsed`, so they are always shown as plain text and cannot add formatting tags.

Tests parse the bundled templates and check placeholder insertion and the hover/click structure; they do not verify in-game rendering.
