# Paper Plugin Development

Course code for Kody Simpson's Paper plugin development series.

This repository contains only the plugin project shown in the videos. Use the
branch selector on GitHub to choose the episode you are watching. Each episode
branch is a frozen checkpoint of the project at the end of that episode.

## Episode branches

| Episode | Branch |
| --- | --- |
| 2 - Your First Paper Plugin | `episode-02-first-paper-plugin` |

The `main` branch contains the latest completed version of the course project.

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
