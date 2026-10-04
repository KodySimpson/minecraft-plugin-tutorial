# Make a Minecraft Plugin in 2026

This is the code repository for Kody Simpson's modern Paper plugin development
series. Every coding part lives on its own frozen branch, so you can inspect the
finished project for one lesson without inheriting unrelated code from the rest
of the course.

Part 1 is the series introduction and has no code branch. Choose the branch for
the part you are watching from GitHub's branch dropdown or the table below.

## Course branches

| Part | Project | Branch |
| --- | --- | --- |
| 2 — Your First Paper Plugin | FirstPlugin | [`part-02-first-paper-plugin`](../../tree/part-02-first-paper-plugin) |
| 3 — Responding to Events | WelcomeMessages | [`part-03-responding-to-events`](../../tree/part-03-responding-to-events) |
| 4 — Controlling Events | cancelling-events | [`part-04-controlling-events`](../../tree/part-04-controlling-events) |
| 5 — Simple Commands | ServerUtilities | [`part-05-simple-commands`](../../tree/part-05-simple-commands) |
| 6 — Brigadier Command Trees | CommandTrees | [`part-06-brigadier-command-trees`](../../tree/part-06-brigadier-command-trees) |
| 7 — Command Arguments | CommandArguments | [`part-07-command-arguments`](../../tree/part-07-command-arguments) |
| 8 — Command Targets and Suggestions | AdminToolkit | [`part-08-command-targets-and-suggestions`](../../tree/part-08-command-targets-and-suggestions) |
| 9 — Command Permissions | permissions | [`part-09-command-permissions`](../../tree/part-09-command-permissions) |
| 10 — Components and Audiences | text-components | [`part-10-components-and-audiences`](../../tree/part-10-components-and-audiences) |
| 11 — Configuration Files | ConfigTutorial | [`part-11-configuration-files`](../../tree/part-11-configuration-files) |
| 12 — MiniMessage | JoinMessages | [`part-12-minimessage`](../../tree/part-12-minimessage) |
| 13 — Code Along: Launch Pads | LaunchPads | [`part-13-launch-pads`](../../tree/part-13-launch-pads) |
| 14 — Dialog API | DialogExamples | [`part-14-dialog-api`](../../tree/part-14-dialog-api) |

Part 12 is a standalone JoinMessages project, so its class and package names
differ from the Part 11 branch. New branches are added as their parts are
published.

## Course baseline

- Java 25
- Paper 26.2 for Parts 2–10 and 12; Paper 26.3 for Part 11 and Parts 13 onward
- Gradle with the Kotlin DSL
- IntelliJ IDEA with the Minecraft Development plugin for project creation

Each branch sets its own Paper API version in `build.gradle.kts`.

The videos use IntelliJ's Minecraft Development plugin to make project setup
friendly for beginners. The generated Gradle files remain ordinary project
files, so you can also clone a branch and build it from the command line.

## Use a part

1. Open the branch dropdown above the file list, or click a branch in the table.
2. Select the branch matching the video.
3. Download that branch or clone the repository and check it out locally.
4. Open the project folder in IntelliJ IDEA.

On Windows, every coding branch can be built and run with:

```powershell
.\gradlew.bat clean build
.\gradlew.bat runServer
```

Type `stop` in the server console to shut the test server down cleanly.
