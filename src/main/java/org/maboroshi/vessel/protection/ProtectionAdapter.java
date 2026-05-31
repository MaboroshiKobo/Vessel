package org.maboroshi.vessel.protection;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ProtectionAdapter {
    boolean canCapture(Player player, Location location);

    boolean canRelease(Player player, Location location);

    String getName();
}
