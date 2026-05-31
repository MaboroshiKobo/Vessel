package org.maboroshi.vessel.util;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public final class NamespacedKeys {
    public static NamespacedKey VESSEL_TYPE;
    public static NamespacedKey CAPTURED_ENTITY;
    public static NamespacedKey CAPTURED_ENTITY_NAME;
    public static NamespacedKey VESSEL_ID;

    private NamespacedKeys() {}

    public static void load(Plugin plugin) {
        VESSEL_TYPE = new NamespacedKey(plugin, "vessel_type");
        CAPTURED_ENTITY = new NamespacedKey(plugin, "captured_entity");
        CAPTURED_ENTITY_NAME = new NamespacedKey(plugin, "captured_entity_name");
        VESSEL_ID = new NamespacedKey(plugin, "vessel_id");
    }
}
