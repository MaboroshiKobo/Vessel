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
        CreatureSpawnEvent.SpawnReason spawnReason = event.getSpawnReason();
        if (spawnReason == CreatureSpawnEvent.SpawnReason.NATURAL) {
            return;
        }

        event.getEntity()
                .getPersistentDataContainer()
                .set(NamespacedKeys.SPAWN_REASON, org.bukkit.persistence.PersistentDataType.STRING, spawnReason.name());
    }
}
