package org.maboroshi.vessel.manager;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.maboroshi.vessel.Vessel;
import org.maboroshi.vessel.config.ConfigManager;
import org.maboroshi.vessel.handler.ItemHandler;

public class VesselManager {
    private final ConfigManager config;
    private final NamespacedKey typeKey;

    public VesselManager(Vessel plugin) {
        this.config = plugin.getConfigManager();
        this.typeKey = new NamespacedKey(plugin, "vessel_type");
    }

    /**
     * Creates an empty vessel item based on the specified type.
     * @param type The type of vessel ("consumable" or "reusable")
     * @return The constructed ItemStack, or null if the type is invalid or disabled.
     */
    public ItemStack createEmptyVessel(String type) {
        if ("consumable".equalsIgnoreCase(type)) {
            if (!config.getMainConfig().modules.consumable.enabled) return null;

            Material mat = Material.matchMaterial(
                    config.getMainConfig().modules.consumable.item.toUpperCase());
            if (mat == null) mat = Material.AMETHYST_SHARD;

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return null;

            ItemHandler.applyText(
                    meta,
                    config.getMainConfig().modules.consumable.displayName,
                    config.getMainConfig().modules.consumable.lore);

            meta.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, "consumable");
            item.setItemMeta(meta);
            return item;

        } else if ("reusable".equalsIgnoreCase(type)) {
            if (!config.getMainConfig().modules.reusable.enabled) return null;

            Material mat = Material.matchMaterial(
                    config.getMainConfig().modules.reusable.item.toUpperCase());
            if (mat == null) mat = Material.ECHO_SHARD;

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return null;

            ItemHandler.applyText(
                    meta,
                    config.getMainConfig().modules.reusable.displayName,
                    config.getMainConfig().modules.reusable.lore);

            meta.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING, "reusable");
            item.setItemMeta(meta);
            return item;
        }

        return null;
    }
}
