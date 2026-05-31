package org.maboroshi.vessel.handler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.maboroshi.vessel.Vessel;
import org.maboroshi.vessel.api.event.VesselCaptureEvent;
import org.maboroshi.vessel.api.event.VesselReleaseEvent;
import org.maboroshi.vessel.config.ConfigManager;

public class VesselEventHandler implements Listener {
    private final ConfigManager config;
    private final EffectHandler effectHandler;
    private final ActionHandler actionHandler;

    public VesselEventHandler(Vessel plugin) {
        this.config = plugin.getConfigManager();
        this.effectHandler = plugin.getEffectHandler();
        this.actionHandler = plugin.getActionHandler();
    }

    @EventHandler
    public void onCapture(VesselCaptureEvent event) {
        if (event.isCancelled()) return;

        String vesselType = event.getVesselType();
        if ("consumable".equals(vesselType)) {
            effectHandler.playEffects(
                    config.getMainConfig().modules.consumable.events.capture.effects, event.getLocation(), false);
            actionHandler.process(
                    event.getPlayer(),
                    config.getMainConfig()
                            .modules
                            .consumable
                            .events
                            .capture
                            .actions
                            .values());
            if (config.getMainConfig().modules.consumable.events.capture != null
                    && config.getMainConfig().modules.consumable.events.capture.enabled) {
                effectHandler.playEffects(
                        config.getMainConfig().modules.consumable.events.capture.effects, event.getLocation(), false);
                actionHandler.process(
                        event.getPlayer(),
                        config.getMainConfig()
                                .modules
                                .consumable
                                .events
                                .capture
                                .actions
                                .values());
            }
        } else if ("reusable".equals(vesselType)) {
            effectHandler.playEffects(
                    config.getMainConfig().modules.reusable.events.capture.effects, event.getLocation(), false);
            actionHandler.process(
                    event.getPlayer(),
                    config.getMainConfig()
                            .modules
                            .reusable
                            .events
                            .capture
                            .actions
                            .values());
            if (config.getMainConfig().modules.reusable.events.capture != null
                    && config.getMainConfig().modules.reusable.events.capture.enabled) {
                effectHandler.playEffects(
                        config.getMainConfig().modules.reusable.events.capture.effects, event.getLocation(), false);
                actionHandler.process(
                        event.getPlayer(),
                        config.getMainConfig()
                                .modules
                                .reusable
                                .events
                                .capture
                                .actions
                                .values());
            }
        }
    }

    @EventHandler
    public void onRelease(VesselReleaseEvent event) {
        if (event.isCancelled()) return;

        String vesselType = event.getVesselType();
        if ("consumable".equals(vesselType)) {
            effectHandler.playEffects(
                    config.getMainConfig().modules.consumable.events.release.effects, event.getLocation(), false);
            actionHandler.process(
                    event.getPlayer(),
                    config.getMainConfig()
                            .modules
                            .consumable
                            .events
                            .release
                            .actions
                            .values());
            if (config.getMainConfig().modules.consumable.events.release != null
                    && config.getMainConfig().modules.consumable.events.release.enabled) {
                effectHandler.playEffects(
                        config.getMainConfig().modules.consumable.events.release.effects, event.getLocation(), false);
                actionHandler.process(
                        event.getPlayer(),
                        config.getMainConfig()
                                .modules
                                .consumable
                                .events
                                .release
                                .actions
                                .values());
            }
        } else if ("reusable".equals(vesselType)) {
            effectHandler.playEffects(
                    config.getMainConfig().modules.reusable.events.release.effects, event.getLocation(), false);
            actionHandler.process(
                    event.getPlayer(),
                    config.getMainConfig()
                            .modules
                            .reusable
                            .events
                            .release
                            .actions
                            .values());
            if (config.getMainConfig().modules.reusable.events.release != null
                    && config.getMainConfig().modules.reusable.events.release.enabled) {
                effectHandler.playEffects(
                        config.getMainConfig().modules.reusable.events.release.effects, event.getLocation(), false);
                actionHandler.process(
                        event.getPlayer(),
                        config.getMainConfig()
                                .modules
                                .reusable
                                .events
                                .release
                                .actions
                                .values());
            }
        }
    }
}
