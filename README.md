# Episode 16 Custom Items

Completed example for Kody Simpson's Paper plugin tutorial:

- `/kit`: gives you a starter kit built from `ItemStack`s and `ItemMeta`.
  - **Trailblazer**: a diamond sword with a custom name, lore, Sharpness III and a hidden enchantment line, built with `getItemMeta()` and `setItemMeta()`.
  - **Trail Rations**: 16 named bread with lore, built the short way with `editMeta`.

Requires Java 25. This project pins Paper 26.3 build 135 beta in its Gradle configuration.

## Build and run

On Windows, run `./gradlew.bat build` to build, or `./gradlew.bat runServer` to start the development server. On macOS/Linux, use `./gradlew` instead.

The plugin JAR is created in `build/libs/`. Join the development server with a compatible Minecraft client and run `/kit`. The command requires `customkit.kit` (operators by default).
