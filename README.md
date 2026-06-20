<div align="center">
  <img src="https://raw.githubusercontent.com/MaboroshiKobo/branding/refs/heads/main/projects/vessel/vessel.avif" width="180" alt="Vessel Logo" />
  <h1>Vessel</h1>
  <p>Easily capture and store any entity in portable items, allowing you to move and release them anywhere.</p>

  <p>
    <img alt="paper" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/paper_vector.svg">
    <img alt="purpur" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/purpur_vector.svg">
    <img alt="spigot" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/unsupported/spigot_vector.svg">
  </p>

  <p>
    <a href="https://github.com/MaboroshiKobo/Vessel"><img alt="github" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg"></a>
    <a href="https://hangar.papermc.io/Maboroshi/Vessel"><img alt="hangar" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/hangar_vector.svg"></a>
    <a href="https://modrinth.com/plugin/vessel"><img alt="modrinth" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg"></a>
  </p>

  <p>
    <a href="https://docs.maboroshi.org"><img alt="generic" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/documentation/generic_vector.svg"></a>
    <a href="https://discord.maboroshi.org"><img alt="discord-singular" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/social/discord-singular_vector.svg"></a>
  </p>
</div>

### Features

Vessel provides flexible capture-and-release mechanics with a wide range of configuration options:

* Create as many vessel templates as you want with independent behavior, item appearance, and usage restrictions.
* Material overrides to dynamically change the item type or texture based on the exact entity that was captured.
* MythicMobs support to capture and release custom entities.
* Nexo integration to use your own custom item models for your vessel items.
* Custom effects and actions to trigger sounds, particles, or console commands on use (supports chance, permissions, and random selections).
* MiniMessage and PlaceholderAPI support for fully stylized and dynamic display text.

### Prerequisites

To use this plugin, your server must be running **Paper** on `26.1` or higher, and Java 25 or higher.

### Documentation & Support

For a complete guide on features, commands, and configuration, please visit our [wiki](https://docs.maboroshi.org). If you have questions or need to report a bug, join our [Discord server](https://discord.maboroshi.org).

### Statistics

This plugin utilizes [bStats](https://bstats.org/plugin/bukkit/Vessel/31642) to collect anonymous usage metrics.

![bStats Metrics](https://bstats.org/signatures/bukkit/Vessel.svg)

## Building

If you wish to build the project from source, ensure you have a Java 25 environment configured.

```bash
./gradlew build
```
