# Unsuspicious Stew — Minecraft 1.21.1 / NeoForge

Client-side port of Schauweg's MIT-licensed UnsuspiciousStew mod.

- Shows suspicious stew effects and durations when hovering over the item.
- Shows the same information above the hotbar when selecting a suspicious stew,
  with the vanilla selected-item timer and fade.
- Uses Minecraft's translations for effect names and duration formatting.
- Reads the item's `minecraft:suspicious_stew_effects` data component without
  changing the item or its effects.

## Requirements and installation

- Minecraft **1.21.1**
- NeoForge **21.1.264** or newer in the **21.1** series (the build target is 21.1.264)
- Java **21**

Put `unsuspicious-stew-1.21.1-neoforge-0.4.0.jar` in the instance's `mods`
directory. Fabric API and Held Item Info are not required. Only install this on
the client; the server does not need it. Effect previews require the server to
send the stew's effect component to the client.

## Build

Select a Java 21 JDK with `JAVA_HOME`, then run:

```sh
./gradlew build
```

The installable JAR is generated in `build/libs/`. The `-sources.jar`
is for source code and should not be installed in the game.

For a development client, run `./gradlew runClient`.

The port uses NeoForge client events and ModDevGradle. Two access-transformer
entries expose the vanilla selected-item timer and stack for the HUD's timing.
Effect durations use the world's tick rate, matching vanilla's formatting.
The mod entrypoint and event subscribers load only on the client.

## License

MIT. Original copyright: Schauweg, 2020. See [LICENSE](LICENSE).
