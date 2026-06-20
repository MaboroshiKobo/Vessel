package org.maboroshi.vessel.util;

import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public final class MythicHook {

    public static String getInternalName(Entity entity) {
        return MythicBukkit.inst()
                .getMobManager()
                .getActiveMob(entity.getUniqueId())
                .map(activeMob -> activeMob.getType().getInternalName())
                .orElse(null);
    }

    public static String getDisplayName(Entity entity) {
        return MythicBukkit.inst()
                .getMobManager()
                .getActiveMob(entity.getUniqueId())
                .map(activeMob -> activeMob.getDisplayName() != null
                                && !activeMob.getDisplayName().isEmpty()
                        ? activeMob.getDisplayName()
                        : activeMob.getType().getInternalName())
                .orElse(null);
    }

    public static Entity spawnMob(String id, Location loc) {
        return MythicBukkit.inst().getMobManager().spawnMob(id, loc).getEntity().getBukkitEntity();
    }
}
