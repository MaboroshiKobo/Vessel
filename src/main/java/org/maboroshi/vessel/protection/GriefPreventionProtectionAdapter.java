package org.maboroshi.vessel.protection;

import java.util.Locale;
import java.util.function.Supplier;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.maboroshi.vessel.util.Log;

/**
 * GriefPrevention integration: gates capture/release on a configurable claim permission and
 * delegates the actual trust/ownership decision to GriefPrevention itself.
 *
 * <p>Vessel compiles against {@code com.griefprevention:GriefPrevention:16.18.2-SNAPSHOT} (the
 * newest version actually published to CodeMC's Maven repo as of this writing — verified via its
 * {@code maven-metadata.xml}, which jumps straight from 16.18.2-SNAPSHOT to 16.19-SNAPSHOT with
 * nothing in between). A newer, non-Maven-published build, GriefPrevention 16.18.7, was also checked
 * directly via {@code javap} against its actual jar.
 *
 * <p>{@code Claim#checkPermission(Player, ClaimPermission, Event)} already composes owner, explicit
 * trust, public trust, subdivision-inherits-parent-trust, Admin Claim, and {@code ignoreclaims}-mode
 * bypass — this adapter does not re-implement any of that, it only maps Vessel's config-facing
 * {@link ClaimAction} onto GriefPrevention's {@link ClaimPermission} and surfaces its denial reason.
 *
 * <p><b>Version-drift trap, confirmed by direct comparison of both jars:</b> 16.18.2-SNAPSHOT's
 * {@code ClaimPermission} enum has {@code Inventory} but no {@code Container}. 16.18.7 has
 * <em>both</em> — {@code Container} was introduced sometime between those two releases (not at
 * 18.0.0 as changelogs elsewhere might suggest; that's just the next version tracked upstream on
 * GitHub, not necessarily where the rename actually shipped). Since Vessel can only compile against
 * 16.18.2-SNAPSHOT, it deliberately maps to {@code Inventory} — the one name confirmed to exist in
 * both versions checked. If the Maven dependency is ever bumped to a version where {@code Inventory}
 * is removed (not just deprecated), switch {@link #toClaimPermission} to {@code Container} and update
 * this comment.
 */
public final class GriefPreventionProtectionAdapter implements ProtectionAdapter {
    // Package-private (not private) so its config-string parsing and GP-enum mapping can be
    // exercised directly by unit tests without needing a live GriefPrevention plugin instance.
    enum ClaimAction {
        ACCESS,
        CONTAINER,
        BUILD
    }

    private final ClaimPermission capturePermission;
    private final ClaimPermission releasePermission;

    public GriefPreventionProtectionAdapter(String captureSetting, String releaseSetting) {
        this.capturePermission = toClaimPermission(parseAction(captureSetting, ClaimAction.CONTAINER));
        this.releasePermission = toClaimPermission(parseAction(releaseSetting, ClaimAction.BUILD));
    }

    @Override
    public ProtectionResult canCapture(Player player, Location location) {
        return check(player, location, capturePermission);
    }

    @Override
    public ProtectionResult canRelease(Player player, Location location) {
        return check(player, location, releasePermission);
    }

    @Override
    public String getName() {
        return "GriefPrevention";
    }

    private ProtectionResult check(Player player, Location location, ClaimPermission permission) {
        if (player == null || location == null || location.getWorld() == null) {
            return ProtectionResult.denied(null);
        }

        DataStore dataStore = GriefPrevention.instance.dataStore;
        Claim claim = dataStore.getClaimAt(location, false, null);
        if (claim == null) {
            return ProtectionResult.ALLOWED;
        }

        Supplier<String> denial = claim.checkPermission(player, permission, null);
        return denial == null ? ProtectionResult.ALLOWED : ProtectionResult.denied(denial.get());
    }

    static ClaimAction parseAction(String configured, ClaimAction fallback) {
        if (configured == null) return fallback;
        try {
            return ClaimAction.valueOf(configured.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            Log.warn("Invalid GriefPrevention permission mapping '" + configured + "', falling back to "
                    + fallback.name() + ".");
            return fallback;
        }
    }

    static ClaimPermission toClaimPermission(ClaimAction action) {
        return switch (action) {
            case ACCESS -> ClaimPermission.Access;
            case CONTAINER -> ClaimPermission.Inventory;
            case BUILD -> ClaimPermission.Build;
        };
    }
}
