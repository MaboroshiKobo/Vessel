package org.maboroshi.vessel.manager;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.maboroshi.vessel.Vessel;
import org.maboroshi.vessel.config.ConfigManager;
import org.maboroshi.vessel.handler.ItemHandler;
import org.maboroshi.vessel.util.NamespacedKeys;

public class VesselManager {
    private final ConfigManager config;

    public VesselManager(Vessel plugin) {
        this.config = plugin.getConfigManager();
    }

    /**
     * Creates an empty vessel item based on the specified type.
     * @param type The type of vessel ("consumable" or "reusable")
     * @return The constructed ItemStack, or null if the type is invalid or disabled.
     */
    public ItemStack createEmptyVessel(String type) {
        if ("consumable".equalsIgnoreCase(type)) {
            if (!config.getMainConfig().modules.consumable.enabled) return null;

            ItemStack item =
                    resolveConfiguredItem(config.getMainConfig().modules.consumable.item, Material.AMETHYST_SHARD);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return null;

            ItemHandler.applyText(
                    meta,
                    config.getMainConfig().modules.consumable.displayName,
                    config.getMainConfig().modules.consumable.lore);

            meta.getPersistentDataContainer().set(NamespacedKeys.VESSEL_TYPE, PersistentDataType.STRING, "consumable");
            item.setItemMeta(meta);
            return item;

        } else if ("reusable".equalsIgnoreCase(type)) {
            if (!config.getMainConfig().modules.reusable.enabled) return null;

            ItemStack item = resolveConfiguredItem(config.getMainConfig().modules.reusable.item, Material.ECHO_SHARD);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return null;

            ItemHandler.applyText(
                    meta,
                    config.getMainConfig().modules.reusable.displayName,
                    config.getMainConfig().modules.reusable.lore);

            meta.getPersistentDataContainer().set(NamespacedKeys.VESSEL_TYPE, PersistentDataType.STRING, "reusable");
            item.setItemMeta(meta);
            return item;
        }

        return null;
    }

    private ItemStack resolveConfiguredItem(String configuredItem, Material fallback) {
        String itemId = configuredItem == null ? "" : configuredItem.trim();
        if (!itemId.isEmpty()) {
            Material material = Material.matchMaterial(itemId.toUpperCase(Locale.ROOT));
            if (material != null) {
                return new ItemStack(material);
            }

            if (Bukkit.getPluginManager().isPluginEnabled("Nexo")) {
                ItemStack nexoItem = NexoItems.optionalItemFromId(itemId)
                        .map(ItemBuilder::build)
                        .orElse(null);
                if (nexoItem != null) return nexoItem;
            }
        }

        return new ItemStack(fallback);
    }
}
