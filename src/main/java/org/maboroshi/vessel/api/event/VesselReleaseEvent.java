package org.maboroshi.vessel.api.event;

import org.bukkit.Location;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class VesselReleaseEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled = false;
    private final Player player;
    private final EntitySnapshot snapshot;
    private final Location location;
    private final String vesselType;
    private final ItemStack vesselItem;

    public VesselReleaseEvent(
            Player player, EntitySnapshot snapshot, Location location, String vesselType, ItemStack vesselItem) {
        this.player = player;
        this.snapshot = snapshot;
        this.location = location;
        this.vesselType = vesselType;
        this.vesselItem = vesselItem;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public Player getPlayer() {
        return player;
    }

    public EntitySnapshot getSnapshot() {
        return snapshot;
    }

    public Location getLocation() {
        return location;
    }

    public String getVesselType() {
        return vesselType;
    }

    public ItemStack getVesselItem() {
        return vesselItem;
    }
}
