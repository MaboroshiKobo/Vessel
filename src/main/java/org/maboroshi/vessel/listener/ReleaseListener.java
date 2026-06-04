package org.maboroshi.vessel.listener;

import java.util.Locale;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
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
import org.maboroshi.vessel.config.settings.shared.ExclusionConfiguration;
import org.maboroshi.vessel.config.settings.shared.FilterConfiguration;
import org.maboroshi.vessel.config.settings.shared.FilterMode;
import org.maboroshi.vessel.handler.CooldownHandler;
import org.maboroshi.vessel.handler.ItemHandler;
import org.maboroshi.vessel.util.Logger;
import org.maboroshi.vessel.util.MessageUtils;
import org.maboroshi.vessel.util.NamespacedKeys;

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

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        Player player = event.getPlayer();
        ItemStack handItem = player.getInventory().getItemInMainHand();

        if (!handItem.hasItemMeta()) return;

        ItemMeta handMeta = handItem.getItemMeta();
        if (!handMeta.getPersistentDataContainer().has(NamespacedKeys.VESSEL_TYPE, PersistentDataType.STRING)) return;

        String vesselType =
                handMeta.getPersistentDataContainer().get(NamespacedKeys.VESSEL_TYPE, PersistentDataType.STRING);
        if (!"consumable".equals(vesselType) && !"reusable".equals(vesselType)) return;

        if (!player.hasPermission("vessel.use." + vesselType)) {
            messageUtils.send(player, config.getMessageConfig().general.cannotUseVessel);
            return;
        }

        String capturedNBT =
                handMeta.getPersistentDataContainer().get(NamespacedKeys.CAPTURED_ENTITY, PersistentDataType.STRING);
        if (capturedNBT == null || capturedNBT.isEmpty()) return;

        event.setCancelled(true);

        ConsumableConfiguration consumableConfig = config.getConsumableConfig();
        ReusableConfiguration reusableConfig = config.getReusableConfig();
        ExclusionConfiguration exclusions =
                "consumable".equals(vesselType) ? consumableConfig.exclusions : reusableConfig.exclusions;
        FilterConfiguration worldFilter =
                "consumable".equals(vesselType) ? consumableConfig.worlds : reusableConfig.worlds;

        if (!isAllowed(player.getWorld().getName(), worldFilter)) {
            messageUtils.send(
                    player,
                    config.getMessageConfig().general.cannotReleaseWorld,
                    messageUtils.tag("world", player.getWorld().getName()));
            return;
        }

        Location releaseLocation = findSafeReleaseLocation(clickedBlock, event.getBlockFace());
        if (releaseLocation == null) {
            messageUtils.send(player, "<red>There is no safe space to release this vessel.</red>");
            return;
        }

        if (!plugin.getProtectionService().canRelease(player, releaseLocation)) {
            messageUtils.send(player, config.getMessageConfig().general.cannotReleaseHere);
            return;
        }

        if (cooldownHandler.isOnCooldown(player.getUniqueId(), config.getMainConfig().cooldown)) {
            log.debug("Player " + player.getName() + " attempted to release a vessel but is on cooldown.");
            return;
        }

        EntitySnapshot snapshot = plugin.getServer().getEntityFactory().createEntitySnapshot(capturedNBT);
        String entityType = snapshot.getEntityType().name().toLowerCase(Locale.ROOT);

        if (!player.hasPermission("vessel.release." + entityType) && !player.hasPermission("vessel.release.*")) {
            messageUtils.send(
                    player,
                    config.getMessageConfig().general.cannotRelease,
                    messageUtils.tag("entity_type", entityType));
            return;
        }

        String capturedEntityName = handMeta.getPersistentDataContainer()
                .get(NamespacedKeys.CAPTURED_ENTITY_NAME, PersistentDataType.STRING);
        String storedSpawnReason =
                handMeta.getPersistentDataContainer().get(NamespacedKeys.SPAWN_REASON, PersistentDataType.STRING);

        Entity releasedEntity = snapshot.createEntity(releaseLocation.getWorld());

        if (exclusions.tamed && releasedEntity instanceof Tameable tameable && tameable.isTamed()) {
            messageUtils.send(player, config.getMessageConfig().general.cannotReleaseTamed);
            return;
        }

        if (exclusions.named && releasedEntity.customName() != null) {
            messageUtils.send(
                    player,
                    config.getMessageConfig().general.cannotReleaseNamed,
                    messageUtils.tag("entity_type", entityType));
            return;
        }

        VesselReleaseEvent releaseEvent = new VesselReleaseEvent(
                player,
                snapshot,
                releaseLocation,
                vesselType,
                capturedEntityName != null ? capturedEntityName : entityType,
                handItem);
        plugin.getServer().getPluginManager().callEvent(releaseEvent);

        if (releaseEvent.isCancelled()) return;

        CreatureSpawnEvent.SpawnReason spawnReason = resolveSpawnReason(storedSpawnReason);
        if (!releasedEntity.spawnAt(releaseLocation, spawnReason)) return;

        if ("consumable".equals(vesselType)) {
            handItem.subtract();
        } else if ("reusable".equals(vesselType)) {
            ItemStack emptyVessel = handItem.clone();
            emptyVessel.setAmount(1);
            ItemMeta emptyMeta = emptyVessel.getItemMeta();

            emptyMeta.getPersistentDataContainer().remove(NamespacedKeys.CAPTURED_ENTITY);
            emptyMeta.getPersistentDataContainer().remove(NamespacedKeys.CAPTURED_ENTITY_NAME);
            emptyMeta.getPersistentDataContainer().remove(NamespacedKeys.SPAWN_REASON);
            emptyMeta.getPersistentDataContainer().remove(NamespacedKeys.VESSEL_ID);
            ItemHandler.applyText(emptyMeta, config.getReusableConfig().displayName, config.getReusableConfig().lore);
            emptyVessel.setItemMeta(emptyMeta);

            if (handItem.getAmount() > 1) {
                handItem.subtract();
                player.getInventory()
                        .addItem(emptyVessel)
                        .values()
                        .forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
            } else {
                player.getInventory().setItemInMainHand(emptyVessel);
            }
        }

        cooldownHandler.setCooldown(player.getUniqueId());
    }

    private CreatureSpawnEvent.SpawnReason resolveSpawnReason(String storedSpawnReason) {
        if (storedSpawnReason == null || storedSpawnReason.isEmpty()) {
            return CreatureSpawnEvent.SpawnReason.CUSTOM;
        }
        try {
            return CreatureSpawnEvent.SpawnReason.valueOf(storedSpawnReason.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            log.debug("Unknown stored spawn reason '" + storedSpawnReason + "', falling back to CUSTOM.");
            return CreatureSpawnEvent.SpawnReason.CUSTOM;
        }
    }

    private boolean isAllowed(String value, FilterConfiguration filter) {
        if (filter.mode == FilterMode.NONE) return true;
        boolean listed = filter.values.stream().anyMatch(value::equalsIgnoreCase);
        return filter.mode == FilterMode.WHITELIST ? listed : !listed;
    }

    private Location findSafeReleaseLocation(Block clickedBlock, BlockFace clickedFace) {
        Block relativeBlock = clickedBlock.getRelative(clickedFace);
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
