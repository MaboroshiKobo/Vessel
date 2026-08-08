# Changelog

All notable changes to this project are documented here. Format loosely follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

### Added

- Versioned entity payload storage: captured entity data is now wrapped in an envelope recording
  Vessel's schema version, the Minecraft DataVersion at capture time, the codec id, the entity type,
  the serialized payload, a CRC32 checksum, and the Vessel ID. Legacy pre-envelope items (raw
  `EntitySnapshot#getAsString()`, no version metadata — now called "schema v0") are detected
  automatically and migrated lazily the next time the item is used, with no server-wide scan.
- Malformed, checksum-mismatched, or future-schema-version payloads are now rejected outright rather
  than risk spawning garbage or losing the original item: the item is left completely untouched and
  the player is told the vessel's data is corrupted.
- Entities whose serialized data would exceed the safe PersistentDataContainer string size (vanilla
  NBT strings silently truncate to empty past 65,535 bytes) are now rejected at capture time instead
  of risking silent data loss on the next world save.
- GriefPrevention support (optional soft dependency): capture requires a configurable claim
  permission (default `CONTAINER`), release requires another (default `BUILD`). Claim
  ownership/trust/subdivisions/Admin Claims/public trust/`ignoreclaims` are all delegated to
  GriefPrevention's own `Claim#checkPermission` — not reimplemented — and its specific denial reason
  is shown to the player when available.
- An in-flight guard keyed by Vessel ID (release) / target entity UUID (capture) prevents the same
  vessel or the same targeted mob from being processed twice by overlapping interactions.
- `Bukkit.isOwnedByCurrentRegion(...)` re-validation before acting on a computed release location, and
  per-player `EntityScheduler` dispatch for any action that fans out to all online players (broadcast
  sounds, `global: true` command actions) — both previously assumed a single main thread and would
  have been unsafe under Folia if exercised.

### Changed

- `ProtectionAdapter` (and the WorldGuard/Towny adapters) now return a `ProtectionResult` carrying an
  optional denial reason instead of a bare `boolean`, so a specific reason (currently only surfaced by
  the new GriefPrevention adapter) can reach the player.
- Capture now serializes and validates the result item fully before removing the target mob or
  consuming the vessel in hand; a failure at any point leaves the mob alive and the item untouched.

### Fixed

- `Vessel.onDisable()` was a no-op; the static plugin instance is now cleared so a PlugMan-style
  unload/reload doesn't leak the previous classloader.

### Notes on Folia + GriefPrevention (see `TESTING.md`)

- Plain GriefPrevention does not run on a stock/vanilla Folia build (no `folia-supported` declaration,
  calls the legacy `Bukkit.getScheduler().scheduleSyncRepeatingTask` API, which Folia's threading
  model rejects). Vessel handles that gracefully — a crashed-and-disabled GriefPrevention is treated
  the same as "not installed."
- Some Folia-derived server forks ship their own compatibility shims that let plugins like
  GriefPrevention load and run despite this. On a build with such a shim, GriefPrevention 16.18.7 was
  confirmed to boot cleanly and Vessel enables right after it with zero errors — see `TESTING.md` for
  the full writeup, including the version-drift trap this uncovered in `ClaimPermission`.
