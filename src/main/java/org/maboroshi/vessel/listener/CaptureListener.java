package org.maboroshi.vessel.listener;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.maboroshi.vessel.Vessel;
import org.maboroshi.vessel.api.event.VesselCaptureEvent;
import org.maboroshi.vessel.config.ConfigManager;
import org.maboroshi.vessel.handler.ItemHandler;
import org.maboroshi.vessel.util.Logger;
import org.maboroshi.vessel.util.MessageUtils;
import org.maboroshi.vessel.util.NamespacedKeys;
import java.util.UUID;

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
        ItemStack handItem = event.getPlayer().getInventory().getItemInMainHand();
        if (!handItem.hasItemMeta()) {
            return;
        }

        ItemMeta handMeta = handItem.getItemMeta();

        if (!handMeta.getPersistentDataContainer().has(NamespacedKeys.VESSEL_TYPE, PersistentDataType.STRING)) {
            return;
        }

        if (handMeta.getPersistentDataContainer().has(NamespacedKeys.CAPTURED_ENTITY, PersistentDataType.STRING)) {
            return;
        }

        String tier = handMeta.getPersistentDataContainer().get(NamespacedKeys.VESSEL_TYPE, PersistentDataType.STRING);
        boolean isConsumable = "consumable".equals(tier) && config.getMainConfig().modules.consumable.enabled;
        boolean isReusable = "reusable".equals(tier) && config.getMainConfig().modules.reusable.enabled;

        if (!isConsumable && !isReusable) {
            return;
        }

        if (plugin.getCooldownHandler()
                .isOnCooldown(event.getPlayer().getUniqueId(), config.getMainConfig().cooldown)) {
            return;
        }

        String entityType = event.getRightClicked().getType().toString();
        boolean blacklisted = (isConsumable
                        && config.getMainConfig()
                                .modules
                                .consumable
                                .blacklistedMobs
                                .contains(entityType))
                || (isReusable
                        && config.getMainConfig()
                                .modules
                                .reusable
                                .blacklistedMobs
                                .contains(entityType));

        if (blacklisted) {
            log.debug("Player " + event.getPlayer().getName() + " tried to capture a blacklisted entity.");
            messageUtils.send(
                    event.getPlayer(),
                    config.getMessageConfig().general.cannotCapture,
                    messageUtils.tag("entity_type", entityType),
                    messageUtils.tagParsed(
                            "entity_name",
                            event.getRightClicked().getName() == null
                                    ? ""
                                    : event.getRightClicked().getName()));
            return;
        }

        ItemStack captureItem = handItem.clone();
        captureItem.setAmount(1);
        ItemMeta captureMeta = captureItem.getItemMeta();

        Entity target = event.getRightClicked();
        Location captureLocation = target.getLocation();

        EntitySnapshot snapshot = target.createSnapshot();
        captureMeta
                .getPersistentDataContainer()
                .set(NamespacedKeys.CAPTURED_ENTITY, PersistentDataType.STRING, snapshot.getAsString());
        captureMeta
            .getPersistentDataContainer()
            .set(NamespacedKeys.VESSEL_ID, PersistentDataType.STRING, UUID.randomUUID().toString());

        if (isConsumable) {
            ItemHandler.applyText(
                    captureMeta,
                    config.getMainConfig().modules.consumable.displayName,
                    config.getMainConfig().modules.consumable.filledLore.stream()
                            .map(line -> line.replace("<entity_name>", target.getName())
                                    .replace("<entity_type>", target.getType().toString()))
                            .toList());
        } else {
            ItemHandler.applyText(
                    captureMeta,
                    config.getMainConfig().modules.reusable.displayName,
                    config.getMainConfig().modules.reusable.filledLore.stream()
                            .map(line -> line.replace("<entity_name>", target.getName())
                                    .replace("<entity_type>", target.getType().toString()))
                            .toList());
        }

        captureItem.setItemMeta(captureMeta);

        VesselCaptureEvent captureEvent =
                new VesselCaptureEvent(event.getPlayer(), snapshot, captureLocation, tier, captureItem);
        plugin.getServer().getPluginManager().callEvent(captureEvent);

        if (captureEvent.isCancelled()) {
            return;
        }

        handItem.setAmount(handItem.getAmount() - 1);

        event.getPlayer()
                .getInventory()
                .addItem(captureItem)
                .values()
                .forEach(leftover ->
                        event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), leftover));

        target.remove();
        event.setCancelled(true);
        plugin.getCooldownHandler().setCooldown(event.getPlayer().getUniqueId());
    }
}
