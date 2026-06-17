package org.maboroshi.vessel.manager;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.maboroshi.vessel.Vessel;
import org.maboroshi.vessel.config.ConfigManager;
import org.maboroshi.vessel.config.settings.components.ItemSettings;
import org.maboroshi.vessel.handler.ItemHandler;
import org.maboroshi.vessel.util.Keys;
import org.maboroshi.vessel.util.Logger;
import org.maboroshi.vessel.util.MythicHook;

public class VesselManager {
    private final ConfigManager config;
    private final Logger log;

    public VesselManager(Vessel plugin) {
        this.config = plugin.getConfigManager();
        this.log = plugin.getPluginLogger();
    }

    public ItemStack createEmptyVessel(String type) {
        String normType = type.toLowerCase(Locale.ROOT);
        ItemSettings settings;
        Material fallback;

        if (normType.equals("consumable")) {
            if (!config.getConsumableConfig().enabled) return null;
            settings = config.getConsumableConfig().item;
            fallback = Material.AMETHYST_SHARD;
        } else if (normType.equals("reusable")) {
            if (!config.getReusableConfig().enabled) return null;
            settings = config.getReusableConfig().item;
            fallback = Material.ECHO_SHARD;
        } else {
            return null;
        }

        ItemStack item = resolveConfiguredItem(settings.material, fallback);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        ItemHandler.applyText(meta, settings.displayName, settings.lore);
        meta.getPersistentDataContainer().set(Keys.VESSEL_TYPE, PersistentDataType.STRING, normType);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack createFilledVessel(String type, Entity capturedEntity, String entityCustomName) {
        String normType = type.toLowerCase(Locale.ROOT);
        ItemSettings settings;
        Material fallback;

        if (normType.equals("consumable")) {
            settings = config.getConsumableConfig().item;
            fallback = Material.AMETHYST_SHARD;
        } else if (normType.equals("reusable")) {
            settings = config.getReusableConfig().item;
            fallback = Material.ECHO_SHARD;
        } else {
            return null;
        }

        String targetMaterialKey = resolveFilledMaterialKey(settings, capturedEntity);
        ItemStack item = resolveConfiguredItem(targetMaterialKey, fallback);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        meta.getPersistentDataContainer().set(Keys.VESSEL_TYPE, PersistentDataType.STRING, normType);

        String rawTypeName =
                capturedEntity.getType().name().toLowerCase(Locale.ROOT).replace("_", " ");
        String safeName = (entityCustomName != null && !entityCustomName.isBlank()) ? entityCustomName : rawTypeName;

        String finalDisplayName =
                settings.displayName.replace("<entity_name>", safeName).replace("<entity_type>", rawTypeName);

        List<String> finalLore = settings.filledLore.stream()
                .map(line -> line.replace("<entity_name>", safeName).replace("<entity_type>", rawTypeName))
                .toList();

        ItemHandler.applyText(meta, finalDisplayName, finalLore);

        item.setItemMeta(meta);
        return item;
    }

    private String resolveFilledMaterialKey(ItemSettings settings, Entity entity) {
        if (Bukkit.getPluginManager().isPluginEnabled("MythicMobs")) {
            String mythicId = MythicHook.getInternalName(entity);
            if (mythicId != null && settings.materialOverrides.containsKey(mythicId)) {
                return settings.materialOverrides.get(mythicId);
            }
        }

        String vanillaType = entity.getType().name();
        if (settings.materialOverrides.containsKey(vanillaType)) {
            return settings.materialOverrides.get(vanillaType);
        }

        return settings.filledMaterial;
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

            log.warn("Invalid material ID configured: " + configuredItem + ". Defaulting to " + fallback.name() + ".");
        }

        return new ItemStack(fallback);
    }
}
