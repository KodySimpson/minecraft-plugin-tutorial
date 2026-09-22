# Episode 10 - Text Components

The recorded example for Minecraft Plugin Tutorial Ep. 10, using Paper and Adventure.

## Topics

- Text components, colors, decorations, appending, and builders
- Hovering to preview a custom-named item
- Click events that suggest a command
- Action bar announcements
- Titles, subtitles, and fade-in/stay/fade-out durations

## Build

Requires Java 25. The project targets Paper 26.2.

```sh
./gradlew build
```

On Windows, use `gradlew.bat build`. The plugin JAR is generated in `build/libs/`.

## Commands

Operators have the `announcements.use` permission by default.

```text
/announce
/announce chat The event starts soon!
/announce actionbar Get ready!
/announce title Welcome!
```

The recorded final implementation loops through online players to send action bars and titles. The video also discusses grouping recipients with Adventure audiences. The clickable chat component suggests `/warp event`; this example does not implement that command.
