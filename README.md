[![Vessel Banner](https://raw.githubusercontent.com/MaboroshiKobo/branding/refs/heads/main/projects/vessel/banners/vessel_2048.png)](https://docs.maboroshi.org/projects/vessel)

<div align="center">
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
    <a href="https://docs.maboroshi.org/projects/vessel"><img alt="generic" height="56" src="https://raw.githubusercontent.com/MaboroshiKobo/branding/refs/heads/main/socials/128x/domain_icon_bg.png"></a>
    <a href="https://discord.maboroshi.org"><img alt="discord-singular" height="56" src="https://raw.githubusercontent.com/MaboroshiKobo/branding/refs/heads/main/socials/128x/discord_icon_bg.png"></a>
  </p>
</div>

## Custom entity capture and item-based mob transportation

Vessel is a utility plugin that lets players capture, store, and transport entities using highly customizable pocket items. It provides administrators complete control over item behaviors, reuse rules, entity restrictions, and world filters to safely manage mob movement on a server.

## Features

* Create infinite item templates with distinct textures, custom lore, and targeted permission groups.
* Configure vessels as single-use consumables or infinitely reusable item containers.
* Apply material overrides to change a vessel's item appearance based on the specific entity caught inside.
* Restrict captures by world, entity type, spawn reasons, nametagged status, or pet ownership.
* Trigger customized sound and particle effects instantly during capture and release actions.

## Prerequisites

Vessel is compatible with the following plugins:

* [Nexo](https://www.nexomc.com/) (Optional for custom item models)
* [MythicMobs](https://mythiccraft.io/) (Optional for custom entity captures)
* [WorldGuard](https://enginehub.org/worldguard/) (Optional for region protection)
* [Towny](https://www.townyadvanced.com/) (Optional for town protection)
* [PlaceholderAPI](https://placeholderapi.com/) (Optional)

## Documentation & Support

For configurations, commands, and permissions, check out our [wiki](https://docs.maboroshi.org/projects/vessel). For bugs, questions, or updates, visit our [Discord server](https://discord.maboroshi.org) or open a [GitHub Issue](https://github.com/MaboroshiKobo/Vessel/issues).

## Statistics

This plugin utilizes [bStats](https://bstats.org/plugin/bukkit/Vessel/31642) to collect anonymous usage metrics.

![bStats Metrics](https://bstats.org/signatures/bukkit/Vessel.svg)

## Building

To build the project from source, ensure you have a Java 25 environment configured.

```bash
./gradlew build
```
