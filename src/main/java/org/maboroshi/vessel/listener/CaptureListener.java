package org.maboroshi.vessel.listener;

import java.util.Locale;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.Mob;
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
import org.maboroshi.vessel.config.settings.ConsumableConfiguration;
import org.maboroshi.vessel.config.settings.ReusableConfiguration;
import org.maboroshi.vessel.config.settings.components.ExclusionSettings;
import org.maboroshi.vessel.config.settings.components.FilterSettings;
import org.maboroshi.vessel.util.Keys;
import org.maboroshi.vessel.util.Logger;
import org.maboroshi.vessel.util.MessageUtils;
import org.maboroshi.vessel.util.MythicHook;
import org.maboroshi.vessel.util.VesselUtils;

public class CaptureListener implements Listener {
    private final Vessel plugin;
    private final Logger log;
    private final MessageUtils messageUtils;
    private final ConfigManager config;

    private static final MiniMessage mm = MiniMessage.miniMessage();

    public CaptureListener(Vessel plugin) {
        this.plugin = plugin;
        this.log = plugin.getPluginLogger();
        this.messageUtils = plugin.getMessageUtils();
        this.config = plugin.getConfigManager();
    }

    @EventHandler
    public void onCapture(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (!itemInHand.hasItemMeta()) return;

        ItemMeta meta = itemInHand.getItemMeta();
        if (!meta.getPersistentDataContainer().has(Keys.VESSEL_TYPE, PersistentDataType.STRING)
                || meta.getPersistentDataContainer().has(Keys.MOB_DATA, PersistentDataType.STRING)) return;

        Entity target = event.getRightClicked();
        if (!(target instanceof Mob clickedMob)) return;

        String vesselType = meta.getPersistentDataContainer().get(Keys.VESSEL_TYPE, PersistentDataType.STRING);
        boolean consumable = "consumable".equals(vesselType);
        boolean reusable = "reusable".equals(vesselType);

        if (!consumable && !reusable) return;

        if (!player.hasPermission("vessel.use." + vesselType)) {
            messageUtils.send(player, config.getMessageConfig().general.cannotUseVessel);
            return;
        }

        event.setCancelled(true);

        boolean active = consumable ? config.getConsumableConfig().enabled : config.getReusableConfig().enabled;
        if (!active) return;

        ConsumableConfiguration singleUse = config.getConsumableConfig();
        ReusableConfiguration multiUse = config.getReusableConfig();

        FilterSettings worlds = consumable ? singleUse.restrictions.worlds : multiUse.restrictions.worlds;
        if (!VesselUtils.isAllowed(player.getWorld().getName(), worlds)) {
            messageUtils.send(
                    player,
                    config.getMessageConfig().general.cannotCaptureWorld,
                    messageUtils.tag("world", player.getWorld().getName()));
            return;
        }

        Location loc = clickedMob.getLocation();

        if (!plugin.getProtectionService().canCapture(player, loc)) {
            messageUtils.send(player, config.getMessageConfig().general.cannotCaptureHere);
            return;
        }

        String mobId = clickedMob.getType().name().toLowerCase(Locale.ROOT);
        ExclusionSettings rules = consumable ? singleUse.restrictions.exclusions : multiUse.restrictions.exclusions;

        String rawMobName = clickedMob.getName() != null ? clickedMob.getName() : mobId;
        String safeMobName =
                mm.serialize(LegacyComponentSerializer.legacySection().deserialize(rawMobName));

        if (clickedMob.getPersistentDataContainer().has(Keys.SPAWN_REASON, PersistentDataType.STRING)) {
            String reason = clickedMob.getPersistentDataContainer().get(Keys.SPAWN_REASON, PersistentDataType.STRING);
            if (!VesselUtils.isAllowed(reason, rules.spawnReasons)) {
                messageUtils.send(
                        player,
                        config.getMessageConfig().general.blacklistedEntity,
                        messageUtils.tag("entity_type", mobId),
                        messageUtils.tag("spawn_reason", reason),
                        messageUtils.tagParsed("entity_name", safeMobName));
                log.debug("Player " + player.getName() + " tried to capture entity spawned by reason " + reason + ".");
                return;
            }
        }

        if (clickedMob instanceof Tameable pet && pet.isTamed()) {
            UUID owner = pet.getOwnerUniqueId();
            if (owner != null) {
                if (owner.equals(player.getUniqueId())) {
                    if (rules.tamed) {
                        messageUtils.send(player, config.getMessageConfig().general.cannotCaptureTamed);
                        return;
                    }
                } else {
                    if (rules.othersTamed) {
                        messageUtils.send(player, config.getMessageConfig().general.cannotCaptureOthersTamed);
                        return;
                    }
                }
            }
        }

        if (rules.named && clickedMob.customName() != null) {
            messageUtils.send(
                    player,
                    config.getMessageConfig().general.cannotCaptureNamed,
                    messageUtils.tag("entity_type", mobId));
            return;
        }

        FilterSettings mobs = consumable ? singleUse.restrictions.entities : multiUse.restrictions.entities;

        if (!VesselUtils.isAllowed(mobId, mobs)) {
            log.debug("Player " + player.getName() + " tried to capture a disallowed entity.");
            messageUtils.send(
                    player,
                    config.getMessageConfig().general.blacklistedEntity,
                    messageUtils.tag("entity_type", mobId),
                    messageUtils.tagParsed("entity_name", safeMobName));
            return;
        }

        if (!player.hasPermission("vessel.capture.*")
                && !player.hasPermission("vessel.capture." + mobId)
                && !VesselUtils.hasGroupPermission(player, clickedMob, "capture")) {
            messageUtils.send(player, config.getMessageConfig().general.cannotCapture, messageUtils.tag("entity_type", mobId));
            return;
        }

        if (plugin.getCooldownHandler().isOnCooldown(player.getUniqueId(), config.getMainConfig().cooldown)) return;

        String rawTargetName = clickedMob.getName() != null
                ? clickedMob.getName()
                : clickedMob.getType().name();
        String mythicId = null;

        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            mythicId = MythicHook.getInternalName(clickedMob);
            if (mythicId != null) {
                rawTargetName = MythicHook.getDisplayName(clickedMob);
            }
        }

        String targetName =
                mm.serialize(LegacyComponentSerializer.legacySection().deserialize(rawTargetName));

        Component customNameComponent = clickedMob.customName();
        if (customNameComponent != null) {
            String legacyRepresentation =
                    LegacyComponentSerializer.legacySection().serialize(customNameComponent);
            Component cleanedComponent =
                    LegacyComponentSerializer.legacySection().deserialize(legacyRepresentation);
            clickedMob.customName(cleanedComponent);
        }

        EntitySnapshot snapshot = clickedMob.createSnapshot();
        if (snapshot == null) {
            Class<? extends Entity> entityClass = clickedMob.getType().getEntityClass();
            if (entityClass != null) {
                Entity temp = clickedMob.getWorld().createEntity(clickedMob.getLocation(), entityClass);
                snapshot = temp.createSnapshot();
            }
        }

        if (snapshot == null) {
            log.debug("Failed to create an EntitySnapshot for entity type: " + clickedMob.getType());
            return;
        }

        ItemStack resultItem = plugin.getVesselManager().createFilledVessel(vesselType, clickedMob, targetName);
        if (resultItem == null) return;

        ItemMeta resultMeta = resultItem.getItemMeta();
        if (resultMeta == null) return;

        resultMeta.getPersistentDataContainer().set(Keys.MOB_DATA, PersistentDataType.STRING, snapshot.getAsString());
        resultMeta.getPersistentDataContainer().set(Keys.MOB_NAME, PersistentDataType.STRING, targetName);

        if (mythicId != null) resultMeta.getPersistentDataContainer().set(Keys.MYTHIC_ID, PersistentDataType.STRING, mythicId);

        String spawnReason = clickedMob.getPersistentDataContainer().get(Keys.SPAWN_REASON, PersistentDataType.STRING);
        if (spawnReason == null || spawnReason.isEmpty()) spawnReason = clickedMob.getEntitySpawnReason().name();
        resultMeta.getPersistentDataContainer().set(Keys.SPAWN_REASON, PersistentDataType.STRING, spawnReason);
        resultMeta.getPersistentDataContainer().set(Keys.VESSEL_ID, PersistentDataType.STRING, UUID.randomUUID().toString());
        resultItem.setItemMeta(resultMeta);

        VesselCaptureEvent captureEvent = new VesselCaptureEvent(player, snapshot, loc, vesselType, targetName, resultItem);
        plugin.getServer().getPluginManager().callEvent(captureEvent);

        if (captureEvent.isCancelled()) return;

        itemInHand.subtract();

        player.getInventory()
                .addItem(resultItem)
                .values()
                .forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));

        clickedMob.remove();
        plugin.getCooldownHandler().setCooldown(player.getUniqueId());
    }
}
