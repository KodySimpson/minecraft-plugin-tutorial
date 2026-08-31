# Part 4: Controlling Events

This part builds `SpawnProtection`, a complete plugin that protects the area
around each world's spawn point. Ordinary players cannot break blocks, place
blocks, or take damage inside the protected radius. Operators bypass the rule.

## What you learn

- How cancellable events let a plugin enforce a gameplay rule
- How event priority controls when a listener runs
- Why `ignoreCancelled = true` avoids interfering with work another plugin has
  already done
- How early returns keep frequently called event handlers readable
- How to compare horizontal distance without using a square root
- How permissions create an intentional bypass

## Code tour

1. `SpawnProtectionPlugin` registers the external listener during `onEnable`.
2. `SpawnProtectionListener` listens for block breaks, block placements, and
   player damage.
3. Each handler first checks the bypass permission, then checks the location,
   and only then cancels the event.
4. `isInsideSpawnProtection` compares the event location with that world's
   spawn. Only X and Z matter, so the protected area is a vertical cylinder.
5. The radius is a named Java constant for now. Configuration files arrive in
   a later part.

## Requirements

- Java 25
- IntelliJ IDEA
- The Minecraft Development plugin for IntelliJ IDEA

## Build the plugin

On Windows:

```powershell
.\gradlew.bat build
```

Run a local Paper test server:

```powershell
.\gradlew.bat runServer
```

Type `stop` in the server console to shut the server down cleanly.

## Manual test checklist

Use a non-operator player for the protected tests, then repeat them as an
operator to verify the bypass.

- Break and place a block within 16 blocks of spawn: both actions are denied.
- Take fall, fire, mob, or player damage near spawn: the damage is cancelled.
- Repeat those actions outside the radius: normal gameplay continues.
- Stand exactly on the boundary and confirm it is protected.
- Test another world and confirm its own spawn has the same protection.

## Try it yourself

Change `PROTECTION_RADIUS`, rebuild, and predict where the new boundary will
be before testing it. As a larger challenge, add a second bypass permission
that prevents only damage while still blocking building.
