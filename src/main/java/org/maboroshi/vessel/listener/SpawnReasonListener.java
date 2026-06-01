package org.maboroshi.vessel.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.maboroshi.vessel.Vessel;
import org.maboroshi.vessel.util.NamespacedKeys;

public class SpawnReasonListener implements Listener {
    public SpawnReasonListener(Vessel plugin) {}

    @EventHandler(ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        event.getEntity()
                .getPersistentDataContainer()
                .set(
                        NamespacedKeys.SPAWN_REASON,
                        org.bukkit.persistence.PersistentDataType.STRING,
                        event.getSpawnReason().name());
    }
}
