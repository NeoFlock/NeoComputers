# NeoComputers
A rewrite of OpenComputers for modern Minecraft, alongside our own additions. Based off of the https://github.com/JumperOnJava/Stonecutter-Arch-Template template.
### Project setup
This uses a combination of Architectury, Architectury API, Stonecutter, and Kotlin. Stonecutter is the most important one
to read about. Don't forget to Gradle -> Tasks -> stonecutter -> "Set active project to [version]-[loader]" before building/testing
the mod for that version!

Also, try reading about how stonecutter's conditional macros work (those can be seen as the `//?` statements in the code).
Stonecutter automatically comments and uncomments them when you switch between versions or loaders, you shouldn't do it yourself.

The minecraft version this mod is currently being developed on is 1.21.11 neoforge or fabric. Although the project stonecutter.gradle.kts
is currently using 1.21.9-fabric, you can easily change it with the gradle task.

The recommended IDE for this is IntelliJ IDEA 2026.1, and the JDK used is Eclipse Temurin 25.0.2 (from Adoptium), although
you should be able to use any other build of OpenJDK 26.