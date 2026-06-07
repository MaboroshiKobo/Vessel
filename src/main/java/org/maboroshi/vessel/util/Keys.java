package org.maboroshi.vessel.util;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public final class Keys {
    public static NamespacedKey VESSEL_TYPE;
    public static NamespacedKey MOB_DATA;
    public static NamespacedKey MOB_NAME;
    public static NamespacedKey VESSEL_ID;
    public static NamespacedKey SPAWN_REASON;
    public static NamespacedKey FROM_VESSEL;
    public static NamespacedKey MYTHIC_ID;

    private Keys() {}

    public static void init(Plugin plugin) {
        VESSEL_TYPE = new NamespacedKey(plugin, "type");
        MOB_DATA = new NamespacedKey(plugin, "mob_data");
        MOB_NAME = new NamespacedKey(plugin, "mob_name");
        VESSEL_ID = new NamespacedKey(plugin, "id");
        SPAWN_REASON = new NamespacedKey(plugin, "spawn_reason");
        MYTHIC_ID = new NamespacedKey(plugin, "mythic_id");
        FROM_VESSEL = new NamespacedKey(plugin, "from_vessel");
    }
}
