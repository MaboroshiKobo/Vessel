package org.maboroshi.vessel.listener;

import io.lumine.mythic.bukkit.MythicBukkit;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Golem;
import org.bukkit.entity.Illager;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Monster;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.WaterMob;
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
import org.maboroshi.vessel.util.Keys;
import org.maboroshi.vessel.util.Logger;
import org.maboroshi.vessel.util.MessageUtils;

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
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        if (!itemInHand.hasItemMeta()) return;

        ItemMeta meta = itemInHand.getItemMeta();
        if (!meta.getPersistentDataContainer().has(Keys.TYPE, PersistentDataType.STRING)
                || meta.getPersistentDataContainer().has(Keys.MOB_DATA, PersistentDataType.STRING)) {
            return;
        }

        Entity target = event.getRightClicked();
        if (!(target instanceof Mob clickedMob)) {
            return;
        }

        String vesselType = meta.getPersistentDataContainer().get(Keys.TYPE, PersistentDataType.STRING);
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

        ConsumableConfiguration oneUse = config.getConsumableConfig();
        ReusableConfiguration multiUse = config.getReusableConfig();

        FilterConfiguration worlds = consumable ? oneUse.worlds : multiUse.worlds;
        if (!isAllowed(player.getWorld().getName(), worlds)) {
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
        ExclusionConfiguration rules = consumable ? oneUse.exclusions : multiUse.exclusions;

        if (clickedMob.getPersistentDataContainer().has(Keys.SPAWN_REASON, PersistentDataType.STRING)) {
            String reason = clickedMob.getPersistentDataContainer().get(Keys.SPAWN_REASON, PersistentDataType.STRING);
            if (!isAllowed(reason, rules.spawnReasons)) {
                messageUtils.send(
                        player,
                        config.getMessageConfig().general.blacklistedEntity,
                        messageUtils.tag("entity_type", mobId),
                        messageUtils.tag("spawn_reason", reason),
                        messageUtils.tagParsed(
                                "entity_name", clickedMob.getName() != null ? clickedMob.getName() : mobId));
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

        FilterConfiguration mobs = consumable ? oneUse.entities : multiUse.entities;

        if (!isAllowed(mobId, mobs)) {
            log.debug("Player " + player.getName() + " tried to capture a disallowed entity.");
            messageUtils.send(
                    player,
                    config.getMessageConfig().general.blacklistedEntity,
                    messageUtils.tag("entity_type", mobId),
                    messageUtils.tagParsed("entity_name", clickedMob.getName() != null ? clickedMob.getName() : mobId));
            return;
        }

        if (!player.hasPermission("vessel.capture.*")
                && !player.hasPermission("vessel.capture." + mobId)
                && !hasGroupPermission(player, clickedMob)) {
            messageUtils.send(
                    player, config.getMessageConfig().general.cannotCapture, messageUtils.tag("entity_type", mobId));
            return;
        }

        if (plugin.getCooldownHandler().isOnCooldown(player.getUniqueId(), config.getMainConfig().cooldown)) return;

        ItemStack resultItem = itemInHand.clone();
        resultItem.setAmount(1);
        ItemMeta resultMeta = resultItem.getItemMeta();

        String targetName = clickedMob.getName() != null
                ? clickedMob.getName()
                : clickedMob.getType().toString();
        String targetType = clickedMob.getType().toString();

        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            var activeMobOpt = MythicBukkit.inst().getMobManager().getActiveMob(clickedMob.getUniqueId());
            if (activeMobOpt.isPresent()) {
                var activeMob = activeMobOpt.get();
                String internalName = activeMob.getType().getInternalName();
                resultMeta.getPersistentDataContainer().set(Keys.MYTHIC_ID, PersistentDataType.STRING, internalName);

                targetType = internalName;
                targetName = activeMob.getDisplayName() != null
                                && !activeMob.getDisplayName().isEmpty()
                        ? activeMob.getDisplayName()
                        : internalName;
            }
        }

        final String finalName = targetName;
        final String finalType = targetType;

        EntitySnapshot snapshot = clickedMob.createSnapshot();
        if (snapshot == null) {
            Entity temp = clickedMob.getWorld().spawnEntity(clickedMob.getLocation(), clickedMob.getType());
            snapshot = temp.createSnapshot();
            temp.remove();
        }

        if (snapshot == null) {
            log.error("Failed to create an EntitySnapshot for entity type: " + clickedMob.getType()
                    + ". Capture aborted.");
            return;
        }

        resultMeta.getPersistentDataContainer().set(Keys.MOB_DATA, PersistentDataType.STRING, snapshot.getAsString());
        resultMeta.getPersistentDataContainer().set(Keys.MOB_NAME, PersistentDataType.STRING, finalName);
        resultMeta.getPersistentDataContainer().set(Keys.TYPE, PersistentDataType.STRING, finalType);
        String spawnReason = clickedMob.getPersistentDataContainer().get(Keys.SPAWN_REASON, PersistentDataType.STRING);
        if (spawnReason == null || spawnReason.isEmpty()) {
            spawnReason = clickedMob.getEntitySpawnReason().name();
        }

        resultMeta.getPersistentDataContainer().set(Keys.SPAWN_REASON, PersistentDataType.STRING, spawnReason);
        resultMeta
                .getPersistentDataContainer()
                .set(Keys.ID, PersistentDataType.STRING, UUID.randomUUID().toString());

        String nameFormat = consumable ? oneUse.displayName : multiUse.displayName;
        List<String> rawLore = consumable ? oneUse.filledLore : multiUse.filledLore;

        ItemHandler.applyText(
                resultMeta,
                nameFormat,
                rawLore.stream()
                        .map(line -> line.replace("<entity_name>", finalName).replace("<entity_type>", finalType))
                        .toList());

        resultItem.setItemMeta(resultMeta);

        VesselCaptureEvent captureEvent =
                new VesselCaptureEvent(player, snapshot, loc, vesselType, finalName, resultItem);
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

    private boolean hasGroupPermission(Player player, Entity target) {
        if (target instanceof Animals && player.hasPermission("vessel.capture.animals")) return true;
        if (target instanceof Monster && player.hasPermission("vessel.capture.monsters")) return true;
        if (target instanceof Golem && player.hasPermission("vessel.capture.golems")) return true;
        if (target instanceof Fish && player.hasPermission("vessel.capture.fish")) return true;
        if (target instanceof WaterMob && player.hasPermission("vessel.capture.watermobs")) return true;
        if (target instanceof Ambient && player.hasPermission("vessel.capture.ambient")) return true;
        if (target instanceof Raider && player.hasPermission("vessel.capture.raiders")) return true;
        if (target instanceof Boss && player.hasPermission("vessel.capture.bosses")) return true;
        if (target instanceof Illager && player.hasPermission("vessel.capture.illagers")) return true;
        if (target instanceof Tameable && player.hasPermission("vessel.capture.tameable")) return true;
        if (target instanceof NPC && player.hasPermission("vessel.capture.npcs")) return true;
        return false;
    }

    private boolean isAllowed(String value, FilterConfiguration filter) {
        if (filter.mode == FilterMode.NONE) return true;

        boolean listed = filter.values.stream().anyMatch(value::equalsIgnoreCase);
        return filter.mode == FilterMode.WHITELIST ? listed : !listed;
    }
}
