package org.maboroshi.vessel.listener;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.maboroshi.vessel.Vessel;
import org.maboroshi.vessel.config.ConfigManager;
import org.maboroshi.vessel.handler.CooldownHandler;
import org.maboroshi.vessel.handler.EffectHandler;
import org.maboroshi.vessel.handler.ItemHandler;
import org.maboroshi.vessel.util.Logger;
import org.maboroshi.vessel.util.MessageUtils;
import org.maboroshi.vessel.util.NamespacedKeys;

public class ReleaseListener implements Listener {
    private final Vessel plugin;
    private final ConfigManager config;
    private final Logger log;
    private final EffectHandler effectHandler;
    private final CooldownHandler cooldownHandler;
    private final MessageUtils messageUtils;

    public ReleaseListener(Vessel plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.log = plugin.getPluginLogger();
        this.effectHandler = plugin.getEffectHandler();
        this.cooldownHandler = plugin.getCooldownHandler();
        this.messageUtils = plugin.getMessageUtils();
    }

    @EventHandler
    public void onRelease(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) {
            log.debug("Ignored interaction: Not a right-click on block or not main hand");
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            log.debug("Ignored interaction: Clicked block is null");
            return;
        }

        ItemStack handItem = event.getPlayer().getInventory().getItemInMainHand();
        if (!handItem.hasItemMeta()) {
            log.debug("Ignored interaction: Hand item has no meta");
            return;
        }

        ItemMeta handMeta = handItem.getItemMeta();

        if (!handMeta.getPersistentDataContainer().has(NamespacedKeys.VESSEL_TYPE, PersistentDataType.STRING)) {
            log.debug("Ignored interaction: Hand item has no vessel type");
            return;
        }

        String capturedNBT = handMeta.getPersistentDataContainer().get(NamespacedKeys.CAPTURED_ENTITY, PersistentDataType.STRING);
        if (capturedNBT == null) {
            log.debug("Ignored interaction: Captured entity NBT is null");
            return;
        }

        if (cooldownHandler.isOnCooldown(event.getPlayer().getUniqueId(), config.getMainConfig().cooldown)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);
        cooldownHandler.setCooldown(event.getPlayer().getUniqueId());

        Location releaseLocation = findSafeReleaseLocation(clickedBlock);
        if (releaseLocation == null) {
            messageUtils.send(event.getPlayer(), "<red>There is no safe space to release this vessel.</red>");
            return;
        }

        EntitySnapshot snapshot = plugin.getServer().getEntityFactory().createEntitySnapshot(capturedNBT);
        Entity releasedEntity = snapshot.createEntity(releaseLocation);

        String vesselType = handMeta.getPersistentDataContainer().get(NamespacedKeys.VESSEL_TYPE, PersistentDataType.STRING);

        if ("consumable".equals(vesselType)) {
            effectHandler.playEffects(
                    config.getMainConfig().modules.consumable.releaseEffects, releasedEntity.getLocation(), false);
            handItem.setAmount(handItem.getAmount() - 1);
        } else if ("reusable".equals(vesselType)) {
            effectHandler.playEffects(
                    config.getMainConfig().modules.reusable.releaseEffects, releasedEntity.getLocation(), false);
            handMeta.getPersistentDataContainer().remove(NamespacedKeys.CAPTURED_ENTITY);
            ItemHandler.applyText(
                    handMeta,
                    config.getMainConfig().modules.reusable.displayName,
                    config.getMainConfig().modules.reusable.lore);
            handItem.setItemMeta(handMeta);
        }
    }

    private Location findSafeReleaseLocation(Block clickedBlock) {
        Location base = clickedBlock.getLocation();

        for (int yOffset = 1; yOffset <= 2; yOffset++) {
            for (int xOffset = -1; xOffset <= 1; xOffset++) {
                for (int zOffset = -1; zOffset <= 1; zOffset++) {
                    Location candidate = base.clone().add(0.5 + xOffset, yOffset, 0.5 + zOffset);
                    if (candidate.getBlock().isPassable()
                            && candidate.clone().add(0, 1, 0).getBlock().isPassable()) {
                        return candidate;
                    }
                }
            }
        }

        for (int yOffset = 3; yOffset <= 5; yOffset++) {
            Location candidate = base.clone().add(0.5, yOffset, 0.5);
            if (candidate.getBlock().isPassable()
                    && candidate.clone().add(0, 1, 0).getBlock().isPassable()) {
                return candidate;
            }
        }

        return null;
    }
}
