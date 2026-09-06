# Part 6: Brigadier Command Trees

This self-contained Part 6 plugin teaches literal command branches and an executable root.

- `/spawn zombie` and `/spawn creeper` spawn a mob. Bare `/spawn` is incomplete.
- `/day` sets noon (6000 ticks).
- `/day sunrise` sets dawn (23000 ticks).
- `/day sunset` sets sunset (12000 ticks).

Show the tree visual first, then the unregistered `BasicSpawnCommand.java` comparison,
then `SpawnCommand.java`. Finish with `DayCommand.java` to demonstrate an executable root.
The commented BasicSpawnCommand registration can be used instead of the spawn tree,
but should never be enabled alongside it under the same command name.

Time changes affect everyone in the executing player's world. Use clear Overworld
weather for the sky demo. Time continues advancing after each command.

Heal and movement now belong to **Part 7 — Command Arguments**.

## Build and run

Paper API `26.2.build.119-stable`, Java 25, Gradle 9.7.1.

```powershell
.\gradlew.bat clean build
.\gradlew.bat runServer
```

Commands are registered through Paper's lifecycle API; there is no `commands:`
section in `plugin.yml`. The project uses the existing package/main-class naming
for continuity with the course files.
