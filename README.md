# Make a Minecraft Plugin in 2026

This is the code repository for Kody Simpson's modern Paper plugin development
series. Every coding part lives on its own frozen branch, so you can inspect the
finished project for one lesson without inheriting unrelated code from the rest
of the course.

Part 1 is the series introduction and has no code branch. Choose the branch for
the part you are watching from GitHub's branch dropdown.

## Course branches

| Part | Project | Branch |
| --- | --- | --- |
| 2 — Your First Paper Plugin | FirstPlugin | [`part-02-first-paper-plugin`](../../tree/part-02-first-paper-plugin) |
| 3 — Responding to Events | WelcomeMessages | [`part-03-responding-to-events`](../../tree/part-03-responding-to-events) |
| 4 — Controlling Events | cancelling-events | [`part-04-controlling-events`](../../tree/part-04-controlling-events) |

## Course baseline

- Paper 26.2
- Paper API `26.2.build.119-stable`
- Java 25
- Gradle with the Kotlin DSL
- IntelliJ IDEA with the Minecraft Development plugin for project creation

The videos use IntelliJ's Minecraft Development plugin to make project setup
friendly for beginners. The generated Gradle files remain ordinary project
files, so you can also clone a branch and build it from the command line.

## Use a part

1. Open the branch dropdown above the file list.
2. Select the branch matching the video.
3. Download that branch or clone the repository and check it out locally.
4. Open the project folder in IntelliJ IDEA.

On Windows, every coding branch can be built and run with:

```powershell
.\gradlew.bat clean build
.\gradlew.bat runServer
```

Type `stop` in the server console to shut the test server down cleanly.
