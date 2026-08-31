# Part 3: Responding to Events

`WelcomeMessages` is a self-contained Paper plugin that reacts when players
join and leave the server. It privately welcomes the joining player and
replaces Paper's public join and quit messages with Adventure Components.

## What you will learn

- How Paper announces server activity through events.
- How to create and register an external `Listener` class.
- How `@EventHandler` methods receive event data.
- How to get the affected player from an event.
- How to send a private Component and replace nullable join and quit messages.

## Commands

None. This part is entirely event-driven.

## Requirements

- Java 25
- Paper 26.2

## Build and run

On Windows, build the plugin with:

```powershell
.\gradlew.bat clean build
```

Start a local Paper test server with:

```powershell
.\gradlew.bat runServer
```

Join and leave the server with a Minecraft client to test both event handlers.
Type `stop` in the server console when you are finished.

## Exercise

Change the colors and wording of all three messages. Then hide the public quit
message by passing `null` to `event.quitMessage(...)` while keeping the private
welcome message unchanged.
