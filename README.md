# Episode 9 - Permissions

The recorded example for Minecraft Plugin Tutorial Ep. 9, using Paper.

## Topics

- Protecting a BasicCommand with permission()
- Gating a Brigadier command tree with requires()
- Declaring permission nodes, defaults, and child permissions in plugin.yml

## Build

Requires Java 25 and targets Paper 26.2.

```sh
./gradlew build
```

On Windows, use `gradlew.bat build`. The plugin JAR is generated in `build/libs/`.

## Commands and permissions

| Command | Permission | Default |
| --- | --- | --- |
| `/heal` | `commandpermissions.heal` | Operators |
| `/spawn zombie` | `commandpermissions.spawn` | Everyone |
| `/spawn creeper` | `commandpermissions.spawn` | Everyone |

The `commandpermissions.*` parent grants both child permissions and defaults to operators. The spawn command also requires a player sender.

This preserves the recorded lesson implementation. Only run the optional `runServer` task if you accept the Minecraft EULA; its recorded configuration includes the EULA acceptance JVM flag.
