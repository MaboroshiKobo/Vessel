package org.maboroshi.vessel.config.settings.components;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import java.util.Collections;
import java.util.Map;
import org.maboroshi.vessel.config.objects.CommandAction;
import org.maboroshi.vessel.config.objects.effects.EffectGroup;
import org.maboroshi.vessel.config.objects.effects.SoundEffect;

@Configuration
public class ModuleEvents {
    @Comment("Capture event settings for this module")
    public EventSettings capture = EventSettings.captureDefault();

    @Comment("Release event settings for this module")
    public EventSettings release = EventSettings.releaseDefault();

    @Configuration
    public static class EventSettings {
        @Comment("Enable this event processing block.")
        public boolean enabled = true;

        @Comment("Visual/Audio effects to play when this event triggers.")
        public EffectGroup effects = new EffectGroup();

        @Comment("Actions that execute when the event triggers.")
        public Map<String, CommandAction> actions = Collections.emptyMap();

        public EventSettings() {}

        public EventSettings(boolean enabled, EffectGroup effects, Map<String, CommandAction> actions) {
            this.enabled = enabled;
            this.effects = effects;
            this.actions = actions;
        }

        public static EventSettings captureDefault() {
            return new EventSettings(
                    true,
                    new EffectGroup(
                            Map.of("capture", new SoundEffect("entity.item.pickup", 1f, 1f)), Collections.emptyMap()),
                    Collections.emptyMap());
        }

        public static EventSettings releaseDefault() {
            return new EventSettings(
                    true,
                    new EffectGroup(
                            Map.of("release", new SoundEffect("entity.item.break", 1f, 1f)), Collections.emptyMap()),
                    Collections.emptyMap());
        }
    }
}
