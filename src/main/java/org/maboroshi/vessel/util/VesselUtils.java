package org.maboroshi.vessel.util;

import org.bukkit.entity.Ambient;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Golem;
import org.bukkit.entity.Illager;
import org.bukkit.entity.Monster;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.WaterMob;
import org.maboroshi.vessel.config.settings.components.FilterSettings;
import org.maboroshi.vessel.config.settings.components.FilterSettings.FilterMode;

public class VesselUtils {
    public static boolean hasGroupPermission(Player player, Entity target, String action) {
        String prefix = "vessel." + action + ".";
        if (target instanceof Animals && player.hasPermission(prefix + "animals")) return true;
        if (target instanceof Monster && player.hasPermission(prefix + "monsters")) return true;
        if (target instanceof Golem && player.hasPermission(prefix + "golems")) return true;
        if (target instanceof Fish && player.hasPermission(prefix + "fish")) return true;
        if (target instanceof WaterMob && player.hasPermission(prefix + "watermobs")) return true;
        if (target instanceof Ambient && player.hasPermission(prefix + "ambient")) return true;
        if (target instanceof Raider && player.hasPermission(prefix + "raiders")) return true;
        if (target instanceof Boss && player.hasPermission(prefix + "bosses")) return true;
        if (target instanceof Illager && player.hasPermission(prefix + "illagers")) return true;
        if (target instanceof Tameable && player.hasPermission(prefix + "tameable")) return true;
        if (target instanceof NPC && player.hasPermission(prefix + "npcs")) return true;
        return false;
    }

    public static boolean isAllowed(String value, FilterSettings filter) {
        if (filter.mode == FilterMode.NONE) return true;

        boolean listed = filter.values.stream().anyMatch(value::equalsIgnoreCase);
        return filter.mode == FilterMode.WHITELIST ? listed : !listed;
    }
}
