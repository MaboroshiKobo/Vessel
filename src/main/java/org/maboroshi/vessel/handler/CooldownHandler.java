package org.maboroshi.vessel.handler;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CooldownHandler {
    private final Cache<UUID, Long> interactionCooldowns =
            CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES).build();

    public boolean isOnCooldown(UUID playerId, long cooldownMs) {
        Long last = interactionCooldowns.getIfPresent(playerId);
        return last != null && System.currentTimeMillis() - last < cooldownMs;
    }

    public void setCooldown(UUID playerId) {
        interactionCooldowns.put(playerId, System.currentTimeMillis());
    }

    public void clearCooldowns() {
        interactionCooldowns.invalidateAll();
    }
}
