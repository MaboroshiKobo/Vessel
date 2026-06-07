package org.maboroshi.vessel.listener;

import io.lumine.mythic.bukkit.MythicBukkit;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Golem;
import org.bukkit.entity.Illager;
import org.bukkit.entity.Monster;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.WaterMob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.maboroshi.vessel.Vessel;
import org.maboroshi.vessel.api.event.VesselReleaseEvent;
import org.maboroshi.vessel.config.ConfigManager;
import org.maboroshi.vessel.config.settings.modules.ConsumableConfiguration;
import org.maboroshi.vessel.config.settings.modules.ReusableConfiguration;
import org.maboroshi.vessel.config.settings.shared.FilterConfiguration;
import org.maboroshi.vessel.config.settings.shared.FilterMode;
import org.maboroshi.vessel.handler.CooldownHandler;
import org.maboroshi.vessel.handler.ItemHandler;
import org.maboroshi.vessel.util.Keys;
import org.maboroshi.vessel.util.Logger;
import org.maboroshi.vessel.util.MessageUtils;

public class ReleaseListener implements Listener {
    private final Vessel plugin;
    private final ConfigManager config;
    private final Logger log;
    private final CooldownHandler cooldownHandler;
    private final MessageUtils messageUtils;

    public ReleaseListener(Vessel plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.log = plugin.getPluginLogger();
        this.cooldownHandler = plugin.getCooldownHandler();
        this.messageUtils = plugin.getMessageUtils();
    }

    @EventHandler
    public void onRelease(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (!itemInHand.hasItemMeta()) return;

        ItemMeta meta = itemInHand.getItemMeta();
        if (!meta.getPersistentDataContainer().has(Keys.TYPE, PersistentDataType.STRING)) return;

        String vesselType = meta.getPersistentDataContainer().get(Keys.TYPE, PersistentDataType.STRING);
        if (!"consumable".equals(vesselType) && !"reusable".equals(vesselType)) return;

        if (!player.hasPermission("vessel.use." + vesselType)) {
            messageUtils.send(player, config.getMessageConfig().general.cannotUseVessel);
            return;
        }

        String nbtData = meta.getPersistentDataContainer().get(Keys.MOB_DATA, PersistentDataType.STRING);
        if (nbtData == null || nbtData.isEmpty()) return;

        event.setCancelled(true);

        ConsumableConfiguration oneUse = config.getConsumableConfig();
        ReusableConfiguration multiUse = config.getReusableConfig();
        FilterConfiguration worlds = "consumable".equals(vesselType) ? oneUse.worlds : multiUse.worlds;

        if (!isAllowed(player.getWorld().getName(), worlds)) {
            messageUtils.send(
                    player,
                    config.getMessageConfig().general.cannotReleaseWorld,
                    messageUtils.tag("world", player.getWorld().getName()));
            return;
        }

        Location loc = findSafeReleaseLocation(block, event.getBlockFace());
        if (loc == null) {
            messageUtils.send(player, "<prefix> <white>There is no safe space to release this vessel.</white>");
            return;
        }

        if (!plugin.getProtectionService().canRelease(player, loc)) {
            messageUtils.send(player, config.getMessageConfig().general.cannotReleaseHere);
            return;
        }

        if (cooldownHandler.isOnCooldown(player.getUniqueId(), config.getMainConfig().cooldown)) {
            log.debug("Player " + player.getName() + " attempted to release a vessel but is on cooldown.");
            return;
        }

        EntitySnapshot snapshot = plugin.getServer().getEntityFactory().createEntitySnapshot(nbtData);
        String mobId = snapshot.getEntityType().name().toLowerCase(Locale.ROOT);
        Entity tempMob = snapshot.createEntity(loc.getWorld());

        if (!player.hasPermission("vessel.release.*")
                && !player.hasPermission("vessel.release." + mobId)
                && !hasGroupPermission(player, tempMob)) {
            messageUtils.send(
                    player, config.getMessageConfig().general.cannotRelease, messageUtils.tag("entity_type", mobId));
            return;
        }

        String savedName = meta.getPersistentDataContainer().get(Keys.MOB_NAME, PersistentDataType.STRING);
        String savedReason = meta.getPersistentDataContainer().get(Keys.SPAWN_REASON, PersistentDataType.STRING);

        VesselReleaseEvent releaseEvent = new VesselReleaseEvent(
                player, snapshot, loc, vesselType, savedName != null ? savedName : mobId, itemInHand);
        plugin.getServer().getPluginManager().callEvent(releaseEvent);

        if (releaseEvent.isCancelled()) return;

        Entity releasedMob;
        String mythicId = meta.getPersistentDataContainer().get(Keys.MYTHIC_ID, PersistentDataType.STRING);
        boolean mythic = mythicId != null && !mythicId.isEmpty();

        if (mythic && Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            try {
                releasedMob = MythicBukkit.inst()
                        .getMobManager()
                        .spawnMob(mythicId, loc)
                        .getEntity()
                        .getBukkitEntity();

                if (releasedMob != null && savedReason != null) {
                    releasedMob
                            .getPersistentDataContainer()
                            .set(Keys.SPAWN_REASON, PersistentDataType.STRING, savedReason.toUpperCase(Locale.ROOT));
                }
            } catch (Exception e) {
                log.error("Failed to spawn MythicMob type '" + mythicId + "', falling back to vanilla snapshot.");
                releasedMob = spawnVanillaSnapshot(snapshot, loc, savedReason);
            }
        } else {
            releasedMob = spawnVanillaSnapshot(snapshot, loc, savedReason);
        }

        if (releasedMob == null) return;

        releasedMob.getPersistentDataContainer().set(Keys.FROM_VESSEL, PersistentDataType.BOOLEAN, true);

        if ("consumable".equals(vesselType)) {
            itemInHand.subtract();
        } else if ("reusable".equals(vesselType)) {
            ItemStack cleanedVessel = itemInHand.clone();
            cleanedVessel.setAmount(1);
            ItemMeta cleanedMeta = cleanedVessel.getItemMeta();

            cleanedMeta.getPersistentDataContainer().remove(Keys.MOB_DATA);
            cleanedMeta.getPersistentDataContainer().remove(Keys.MOB_NAME);
            cleanedMeta.getPersistentDataContainer().remove(Keys.SPAWN_REASON);
            cleanedMeta.getPersistentDataContainer().remove(Keys.ID);
            cleanedMeta.getPersistentDataContainer().remove(Keys.MYTHIC_ID);
            ItemHandler.applyText(cleanedMeta, config.getReusableConfig().displayName, config.getReusableConfig().lore);
            cleanedVessel.setItemMeta(cleanedMeta);

            if (itemInHand.getAmount() > 1) {
                itemInHand.subtract();
                player.getInventory()
                        .addItem(cleanedVessel)
                        .values()
                        .forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
            } else {
                player.getInventory().setItemInMainHand(cleanedVessel);
            }
        }

        cooldownHandler.setCooldown(player.getUniqueId());
    }

    private Entity spawnVanillaSnapshot(EntitySnapshot snapshot, Location loc, String savedReason) {
        Entity tempMob = snapshot.createEntity(loc.getWorld());
        CreatureSpawnEvent.SpawnReason spawnReason = resolveSpawnReason(savedReason);
        if (tempMob.spawnAt(loc, spawnReason)) {
            return tempMob;
        }
        return null;
    }

    private boolean hasGroupPermission(Player player, Entity target) {
        if (target instanceof Animals && player.hasPermission("vessel.release.animals")) return true;
        if (target instanceof Monster && player.hasPermission("vessel.release.monsters")) return true;
        if (target instanceof Golem && player.hasPermission("vessel.release.golems")) return true;
        if (target instanceof Fish && player.hasPermission("vessel.release.fish")) return true;
        if (target instanceof WaterMob && player.hasPermission("vessel.release.watermobs")) return true;
        if (target instanceof Ambient && player.hasPermission("vessel.release.ambient")) return true;
        if (target instanceof Raider && player.hasPermission("vessel.release.raiders")) return true;
        if (target instanceof Boss && player.hasPermission("vessel.release.bosses")) return true;
        if (target instanceof Illager && player.hasPermission("vessel.release.illagers")) return true;
        if (target instanceof Tameable && player.hasPermission("vessel.release.tameable")) return true;
        if (target instanceof NPC && player.hasPermission("vessel.release.npcs")) return true;
        return false;
    }

    private CreatureSpawnEvent.SpawnReason resolveSpawnReason(String savedReason) {
        if (savedReason == null || savedReason.isEmpty()) {
            return CreatureSpawnEvent.SpawnReason.CUSTOM;
        }
        try {
            return CreatureSpawnEvent.SpawnReason.valueOf(savedReason.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            log.debug("Unknown stored spawn reason '" + savedReason + "', falling back to CUSTOM.");
            return CreatureSpawnEvent.SpawnReason.CUSTOM;
        }
    }

    private boolean isAllowed(String value, FilterConfiguration filter) {
        if (filter.mode == FilterMode.NONE) return true;
        boolean listed = filter.values.stream().anyMatch(value::equalsIgnoreCase);
        return filter.mode == FilterMode.WHITELIST ? listed : !listed;
    }

    private Location findSafeReleaseLocation(Block block, BlockFace face) {
        Block relativeBlock = block.getRelative(face);
        Location base = relativeBlock.getLocation().add(0.5, 0, 0.5);

        if (relativeBlock.isPassable()
                && relativeBlock.getRelative(BlockFace.UP).isPassable()) return base;

        for (int yOffset = 0; yOffset <= 2; yOffset++) {
            for (int xOffset = -1; xOffset <= 1; xOffset++) {
                for (int zOffset = -1; zOffset <= 1; zOffset++) {
                    Location candidate = base.clone().add(xOffset, yOffset, zOffset);
                    if (candidate.getBlock().isPassable()
                            && candidate.clone().add(0, 1, 0).getBlock().isPassable()) {
                        return candidate;
                    }
                }
            }
        }

        for (int yOffset = 3; yOffset <= 5; yOffset++) {
            Location candidate = base.clone().add(0, yOffset, 0);
            if (candidate.getBlock().isPassable()
                    && candidate.clone().add(0, 1, 0).getBlock().isPassable()) return candidate;
        }
        return null;
    }
}
