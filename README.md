<div align="center">
  <img src="https://raw.githubusercontent.com/MaboroshiKobo/branding/refs/heads/main/projects/vessel/vessel.avif" width="180" alt="Vessel Logo" />
  <h1>Vessel</h1>
  <p>Redefine mob transportation on your server. Safely catch, store, and release any entity using highly customizable pocket items. A free alternative to SafariNet.</p>

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

* Create as many different vessel templates as you want. You can give each item its own unique behavior, custom textures, usage restrictions, and specific entity blacklists/whitelists.
* Set up material overrides so a pocket item's type or appearance instantly transforms depending on the exact mob resting inside it (like showing a chicken spawn egg item when a chicken is caught).
* Trigger custom actions like vanilla sounds, particle effects, or commands whenever a player catches or releases an entity; complete with chance mechanics and permission checks.
* Keep your server looking incredibly clean with native MiniMessage styling and full PlaceholderAPI support to display dynamic text.

### Prerequisites

This plugin is designed and officially tested for **Paper** `26.1`+ using **Java 25**. While it might technically run on slightly older Minecraft or Java versions, those aren't officially supported; so if something breaks, you're on your own!

#### Compatibility

Vessel supports integration with the following plugins to enhance functionality:

* [Nexo](https://www.nexomc.com/) (Custom item models)
* [MythicMobs](https://mythiccraft.io/) (Supports catching and storing MythicMobs entities)
* [PlaceholderAPI](https://placeholderapi.com/)
* [WorldGuard](https://enginehub.org/worldguard/)
* [Towny](https://www.townyadvanced.com/)

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
