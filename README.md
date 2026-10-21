# Unsuspicious Stew — Minecraft 1.20.1 / NeoForge

Client-side port of Schauweg's MIT-licensed UnsuspiciousStew mod.

- Shows suspicious stew effects and durations when hovering over the item.
- Shows the same information above the hotbar when selecting a suspicious stew,
  with the vanilla selected-item timer and fade.
- Uses Minecraft's translations for effect names and duration formatting.
- Reads the item's existing NBT without changing the item or its effects.

## Requirements and installation

- Minecraft **1.20.1**
- NeoForge **47.1.105** (the tested target)
- Java **17**

Put `unsuspicious-stew-1.20.1-neoforge-0.3.0.jar` in the instance's `mods`
directory. Fabric API and Held Item Info are not required. Only install this on
the client; the server does not need it. Effect previews require the server to
send the stew's effect NBT to the client.

## Build

Select a Java 17 JDK with `JAVA_HOME`, then run:

```sh
./gradlew build
```

The installable, reobfuscated JAR is generated in `build/libs/`. The `-sources.jar`
is for source code and should not be installed in the game.

For a development client, run `./gradlew runClient`.

The port replaces the Fabric entrypoint and rendering mixins with Forge-style
client events, as used by NeoForge 1.20.1. Two access-transformer entries expose
the vanilla selected-item timer and stack for the HUD's timing.

## License

MIT. Original copyright: Schauweg, 2020. See [LICENSE](LICENSE).
