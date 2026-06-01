package org.maboroshi.vessel.listener;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.maboroshi.vessel.Vessel;
import org.maboroshi.vessel.api.event.VesselCaptureEvent;
import org.maboroshi.vessel.config.ConfigManager;
import org.maboroshi.vessel.config.settings.modules.ConsumableConfiguration;
import org.maboroshi.vessel.config.settings.modules.ReusableConfiguration;
import org.maboroshi.vessel.config.settings.shared.ExclusionConfiguration;
import org.maboroshi.vessel.config.settings.shared.FilterConfiguration;
import org.maboroshi.vessel.config.settings.shared.FilterMode;
import org.maboroshi.vessel.handler.ItemHandler;
import org.maboroshi.vessel.util.Logger;
import org.maboroshi.vessel.util.MessageUtils;
import org.maboroshi.vessel.util.NamespacedKeys;

public class CaptureListener implements Listener {
    private final Vessel plugin;
    private final Logger log;
    private final MessageUtils messageUtils;
    private final ConfigManager config;

    public CaptureListener(Vessel plugin) {
        this.plugin = plugin;
        this.log = plugin.getPluginLogger();
        this.messageUtils = plugin.getMessageUtils();
        this.config = plugin.getConfigManager();
    }

    @EventHandler
    public void onCapture(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack handItem = player.getInventory().getItemInMainHand();

        if (!handItem.hasItemMeta()) {
            return;
        }

        ItemMeta handMeta = handItem.getItemMeta();
        if (!handMeta.getPersistentDataContainer().has(NamespacedKeys.VESSEL_TYPE, PersistentDataType.STRING)
                || handMeta.getPersistentDataContainer()
                        .has(NamespacedKeys.CAPTURED_ENTITY, PersistentDataType.STRING)) {
            return;
        }

        event.setCancelled(true);

        String tier = handMeta.getPersistentDataContainer().get(NamespacedKeys.VESSEL_TYPE, PersistentDataType.STRING);
        boolean isConsumable = "consumable".equals(tier);
        boolean isReusable = "reusable".equals(tier);

        if (!isConsumable && !isReusable) {
            return;
        }

        boolean isEnabled = isConsumable ? config.getConsumableConfig().enabled : config.getReusableConfig().enabled;
        if (!isEnabled) {
            return;
        }

        ConsumableConfiguration consumableConfig = config.getConsumableConfig();
        ReusableConfiguration reusableConfig = config.getReusableConfig();

        FilterConfiguration worldFilter = isConsumable ? consumableConfig.worlds : reusableConfig.worlds;
        if (!isAllowed(player.getWorld().getName(), worldFilter)) {
            messageUtils.send(player, config.getMessageConfig().general.cannotCaptureWorld);
            return;
        }

        if (!player.hasPermission("vessel.use." + tier)) {
            messageUtils.send(player, config.getMessageConfig().general.cannotUseVessel);
            return;
        }

        Entity target = event.getRightClicked();
        Location captureLocation = target.getLocation();

        if (!plugin.getProtectionService().canCapture(player, captureLocation)) {
            messageUtils.send(player, config.getMessageConfig().general.cannotCaptureHere);
            return;
        }

        String entityType = target.getType().name().toLowerCase(Locale.ROOT);

        ExclusionConfiguration exclusions = isConsumable ? consumableConfig.exclusions : reusableConfig.exclusions;

        if (target.getPersistentDataContainer().has(NamespacedKeys.SPAWN_REASON, PersistentDataType.STRING)) {
            String spawnReason =
                    target.getPersistentDataContainer().get(NamespacedKeys.SPAWN_REASON, PersistentDataType.STRING);
            if (!isAllowed(spawnReason, exclusions.spawnReasons)) {
                messageUtils.send(
                        player,
                        config.getMessageConfig().general.blacklistedEntity,
                        messageUtils.tag("entity_type", entityType),
                        messageUtils.tag("spawn_reason", spawnReason),
                        messageUtils.tagParsed(
                                "entity_name", target.getName() != null ? target.getName() : entityType));
                log.debug("Player " + player.getName() + " tried to capture entity spawned by reason " + spawnReason
                        + ".");
                return;
            }
        }

        if (exclusions.tamed && target instanceof Tameable tameable && tameable.isTamed()) {
            messageUtils.send(player, config.getMessageConfig().general.cannotCaptureTamed);
            return;
        }

        if (exclusions.named && target.customName() != null) {
            messageUtils.send(
                    player,
                    config.getMessageConfig().general.cannotCaptureNamed,
                    messageUtils.tag("entity_type", entityType));
            return;
        }

        FilterConfiguration entityFilter = isConsumable ? consumableConfig.entities : reusableConfig.entities;

        if (!isAllowed(entityType, entityFilter)) {
            log.debug("Player " + player.getName() + " tried to capture a disallowed entity.");
            messageUtils.send(
                    player,
                    config.getMessageConfig().general.blacklistedEntity,
                    messageUtils.tag("entity_type", entityType),
                    messageUtils.tagParsed("entity_name", target.getName() != null ? target.getName() : entityType));
            return;
        }

        if (!player.hasPermission("vessel.capture." + entityType) && !player.hasPermission("vessel.capture.*")) {
            messageUtils.send(
                    player,
                    config.getMessageConfig().general.cannotCapture,
                    messageUtils.tag("entity_type", entityType));
            return;
        }

        if (plugin.getCooldownHandler().isOnCooldown(player.getUniqueId(), config.getMainConfig().cooldown)) {
            return;
        }

        ItemStack captureItem = handItem.clone();
        captureItem.setAmount(1);
        ItemMeta captureMeta = captureItem.getItemMeta();

        EntitySnapshot snapshot = target.createSnapshot();
        String safeEntityName =
                target.getName() != null ? target.getName() : target.getType().toString();
        captureMeta
                .getPersistentDataContainer()
                .set(NamespacedKeys.CAPTURED_ENTITY, PersistentDataType.STRING, snapshot.getAsString());
        captureMeta
                .getPersistentDataContainer()
                .set(NamespacedKeys.CAPTURED_ENTITY_NAME, PersistentDataType.STRING, safeEntityName);
        String spawnReason =
                target.getPersistentDataContainer().get(NamespacedKeys.SPAWN_REASON, PersistentDataType.STRING);
        if (spawnReason == null || spawnReason.isEmpty()) {
            spawnReason = target.getEntitySpawnReason().name();
        }
        captureMeta
                .getPersistentDataContainer()
                .set(NamespacedKeys.SPAWN_REASON, PersistentDataType.STRING, spawnReason);
        captureMeta
                .getPersistentDataContainer()
                .set(
                        NamespacedKeys.VESSEL_ID,
                        PersistentDataType.STRING,
                        UUID.randomUUID().toString());

        String displayName =
                isConsumable ? config.getConsumableConfig().displayName : config.getReusableConfig().displayName;
        List<String> filledLore =
                isConsumable ? config.getConsumableConfig().filledLore : config.getReusableConfig().filledLore;

        ItemHandler.applyText(
                captureMeta,
                displayName,
                filledLore.stream()
                        .map(line -> line.replace("<entity_name>", safeEntityName)
                                .replace("<entity_type>", target.getType().toString()))
                        .toList());

        captureItem.setItemMeta(captureMeta);

        VesselCaptureEvent captureEvent =
                new VesselCaptureEvent(player, snapshot, captureLocation, tier, safeEntityName, captureItem);
        plugin.getServer().getPluginManager().callEvent(captureEvent);

        if (captureEvent.isCancelled()) {
            return;
        }

        handItem.subtract();

        player.getInventory()
                .addItem(captureItem)
                .values()
                .forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));

        target.remove();
        plugin.getCooldownHandler().setCooldown(player.getUniqueId());
    }

    private boolean isAllowed(String value, FilterConfiguration filter) {
        if (filter.mode == FilterMode.NONE) {
            return true;
        }

        boolean listed = filter.values.stream().anyMatch(value::equalsIgnoreCase);
        return filter.mode == FilterMode.WHITELIST ? listed : !listed;
    }
}
