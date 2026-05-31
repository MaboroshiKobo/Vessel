package org.maboroshi.vessel.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownHandler {
    private final Map<UUID, Long> interactionCooldowns = new HashMap<>();

    public boolean isOnCooldown(UUID playerId, long cooldownMs) {
        Long last = interactionCooldowns.get(playerId);
        return last != null && System.currentTimeMillis() - last < cooldownMs;
    }

    public void setCooldown(UUID playerId) {
        interactionCooldowns.put(playerId, System.currentTimeMillis());
    }
}
