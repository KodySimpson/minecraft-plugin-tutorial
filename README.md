# Episode 17 Inventory Menus

Completed example for Kody Simpson's Paper plugin tutorial:

- `/kits`: opens a three-row chest menu with a Trailblazer Kit icon in the middle slot.
- Clicking the icon gives you Episode 16's Trailblazer sword and Trail Rations, then closes the menu on the next tick.
- The menu is recognized by its `InventoryHolder`, not its title, and clicks and drags inside it are cancelled so its items can't be taken or moved.

Requires Java 25. This project pins Paper 26.3 build 151 beta in its Gradle configuration.

## Build and run

On Windows, run `./gradlew.bat test build` to build and test, or `./gradlew.bat runServer` to start the development server. On macOS/Linux, use `./gradlew` instead.

The plugin JAR is created in `build/libs/`. Join the development server with a compatible Minecraft client and run `/kits`. The command requires `kitpicker.use` (granted to all players by default).

Tests exercise the menu and click handling with mocks; they do not verify the menu in a running server.
