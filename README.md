# Episode 11 - Configuration Files

The recorded example for Minecraft Plugin Tutorial Ep. 11, using Paper's built-in config.yml support.

## Topics

- Bundled defaults and saveDefaultConfig()
- Reading values with getConfig()
- YAML sections and dotted paths
- Passing the plugin instance to commands and listeners
- Validating the snack amount and handling inventory leftovers
- Configurable join messages

## Build

Requires Java 25 and targets Paper 26.3.

```sh
./gradlew build
```

On Windows, use `gradlew.bat build`. The plugin JAR is generated in `build/libs/`.

## Try it

Run `/snack` as a player to receive cookies. The configured amount must be from 1 through 100.

The plugin creates `plugins/ConfigTutorial/config.yml` on first startup:

```yaml
snack-amount: 5
join-effects:
  enabled: true
  message: "Welcome to the server!"
```

Edit the server's copy and restart to apply changes. Existing files are preserved by saveDefaultConfig(); newly added defaults are not automatically written over the server owner's file.

This preserves the recorded lesson implementation. Only run the optional `runServer` task if you accept the Minecraft EULA; its recorded configuration includes the EULA acceptance JVM flag.
