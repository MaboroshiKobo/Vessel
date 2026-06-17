package org.maboroshi.vessel.handler;

import java.util.Locale;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.maboroshi.vessel.Vessel;
import org.maboroshi.vessel.api.event.VesselCaptureEvent;
import org.maboroshi.vessel.api.event.VesselReleaseEvent;
import org.maboroshi.vessel.config.ConfigManager;
import org.maboroshi.vessel.config.settings.components.ModuleEvents;
import org.maboroshi.vessel.config.settings.components.ModuleEvents.EventSettings;

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
        ModuleEvents moduleEvents = moduleEventsForType(vesselType);
        if (moduleEvents == null) return;
        runModuleEvent(
                moduleEvents.capture,
                event.getLocation(),
                event.getPlayer(),
                event.getEntityName(),
                event.getSnapshot(),
                vesselType);
    }

    @EventHandler(ignoreCancelled = true)
    public void onRelease(VesselReleaseEvent event) {
        String vesselType = event.getVesselType();
        ModuleEvents moduleEvents = moduleEventsForType(vesselType);
        if (moduleEvents == null) return;
        runModuleEvent(
                moduleEvents.release,
                event.getLocation(),
                event.getPlayer(),
                event.getEntityName(),
                event.getSnapshot(),
                vesselType);
    }

    private ModuleEvents moduleEventsForType(String vesselType) {
        return switch (vesselType) {
            case "consumable" -> config.getConsumableConfig().events;
            case "reusable" -> config.getReusableConfig().events;
            default -> null;
        };
    }

    private void runModuleEvent(
            EventSettings event,
            Location location,
            OfflinePlayer player,
            String entityName,
            EntitySnapshot snapshot,
            String vesselType) {
        if (event == null || !event.enabled) return;
        effectHandler.playEffects(event.effects, location, false);
        actionHandler.process(player, event.actions.values(), command -> {
            String parsed = command;
            String safeWorld = location != null && location.getWorld() != null
                    ? location.getWorld().getName()
                    : "unknown";
            String safeEntityName = entityName != null && !entityName.isBlank()
                    ? entityName
                    : (snapshot != null && snapshot.getEntityType() != null
                            ? snapshot.getEntityType().name().toLowerCase(Locale.ROOT)
                            : "unknown");
            String safeEntityType = snapshot != null && snapshot.getEntityType() != null
                    ? snapshot.getEntityType().name().toLowerCase(Locale.ROOT)
                    : "unknown";

            return parsed.replace("<entity_name>", safeEntityName)
                    .replace("<entity_type>", safeEntityType)
                    .replace("<world>", safeWorld)
                    .replace("<vessel_type>", vesselType);
        });
    }
}
