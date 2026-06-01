package org.maboroshi.vessel.config.settings.shared;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.maboroshi.vessel.config.objects.CommandAction;
import org.maboroshi.vessel.config.objects.effects.EffectGroup;
import org.maboroshi.vessel.config.objects.effects.SoundEffect;

@Configuration
public class VesselEvent {
    @Comment("Enable this event processing block.")
    public boolean enabled = true;

    @Comment("Visual/Audio effects to play when this event triggers.")
    public EffectGroup effects = new EffectGroup();

    @Comment("Actions that execute when the event triggers.")
    public Map<String, CommandAction> actions = new HashMap<>();

    public VesselEvent() {}

    public VesselEvent(boolean enabled, EffectGroup effects, Map<String, CommandAction> actions) {
        this.enabled = enabled;
        this.effects = effects;
        this.actions = actions;
    }

    public static VesselEvent captureDefault() {
        return new VesselEvent(
                true,
                new EffectGroup(
                        new HashMap<>(Map.of("capture_effect", new SoundEffect("entity.item.pickup", 1f, 1f))),
                        new HashMap<>()),
                new HashMap<>(Map.of(
                        "capture_action",
                        new CommandAction(
                                100.0, List.of("msg <player> You captured <entity_name> of type <entity_type>!")))));
    }

    public static VesselEvent releaseDefault() {
        return new VesselEvent(
                true,
                new EffectGroup(
                        new HashMap<>(Map.of("release_effect", new SoundEffect("entity.item.break", 1f, 1f))),
                        new HashMap<>()),
                new HashMap<>(Map.of(
                        "release_action",
                        new CommandAction(
                                100.0, List.of("msg <player> You released <entity_name> of type <entity_type>!")))));
    }
}
