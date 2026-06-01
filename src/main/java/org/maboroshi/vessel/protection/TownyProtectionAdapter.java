package org.maboroshi.vessel.protection;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownyPermission.ActionType;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class TownyProtectionAdapter implements ProtectionAdapter {

    @Override
    public boolean canCapture(Player player, Location location) {
        return canBuild(player, location);
    }

    @Override
    public boolean canRelease(Player player, Location location) {
        return canBuild(player, location);
    }

    @Override
    public String getName() {
        return "Towny";
    }

    private boolean canBuild(Player player, Location location) {
        if (player == null || location == null) {
            return false;
        }

        World world = location.getWorld();
        if (world == null) {
            return false;
        }

        TownyAPI townyApi = TownyAPI.getInstance();
        if (townyApi == null || !townyApi.isTownyWorld(world)) {
            return true;
        }

        return PlayerCacheUtil.getCachePermission(
                player, location, location.getBlock().getType(), ActionType.BUILD);
    }
}
