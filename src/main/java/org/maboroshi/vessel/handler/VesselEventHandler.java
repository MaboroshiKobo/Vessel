package org.maboroshi.vessel.handler;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.maboroshi.vessel.Vessel;
import org.maboroshi.vessel.api.event.VesselCaptureEvent;
import org.maboroshi.vessel.api.event.VesselReleaseEvent;
import org.maboroshi.vessel.config.ConfigManager;
import org.maboroshi.vessel.config.settings.MainConfig;

public class VesselEventHandler implements Listener {
    private final ConfigManager config;
    private final EffectHandler effectHandler;
    private final ActionHandler actionHandler;

    public VesselEventHandler(Vessel plugin) {
        this.config = plugin.getConfigManager();
        this.effectHandler = plugin.getEffectHandler();
        this.actionHandler = plugin.getActionHandler();
    }

    @EventHandler(ignoreCancelled = true)
    public void onCapture(VesselCaptureEvent event) {
        String vesselType = event.getVesselType();
        MainConfig.ModuleEvents moduleEvents = moduleEventsForType(vesselType);
        if (moduleEvents == null) return;
        runModuleEvent(moduleEvents.capture, event.getLocation(), event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onRelease(VesselReleaseEvent event) {
        String vesselType = event.getVesselType();
        MainConfig.ModuleEvents moduleEvents = moduleEventsForType(vesselType);
        if (moduleEvents == null) return;
        runModuleEvent(moduleEvents.release, event.getLocation(), event.getPlayer());
    }

    private MainConfig.ModuleEvents moduleEventsForType(String vesselType) {
        return switch (vesselType) {
            case "consumable" -> config.getMainConfig().modules.consumable.events;
            case "reusable" -> config.getMainConfig().modules.reusable.events;
            default -> null;
        };
    }

    private void runModuleEvent(MainConfig.VesselEvent event, Location location, OfflinePlayer player) {
        if (event == null || !event.enabled) return;
        effectHandler.playEffects(event.effects, location, false);
        actionHandler.process(player, event.actions.values());
    }
}
