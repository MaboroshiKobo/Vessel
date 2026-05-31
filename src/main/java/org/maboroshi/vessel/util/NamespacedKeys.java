package org.maboroshi.vessel.util;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public final class NamespacedKeys {
    public static NamespacedKey VESSEL_TYPE;
    public static NamespacedKey CAPTURED_ENTITY;

    private NamespacedKeys() {}

    public static void load(Plugin plugin) {
        VESSEL_TYPE = new NamespacedKey(plugin, "vessel_type");
        CAPTURED_ENTITY = new NamespacedKey(plugin, "captured_entity");
    }
}