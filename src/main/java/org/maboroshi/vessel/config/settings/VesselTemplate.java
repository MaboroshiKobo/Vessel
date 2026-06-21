package org.maboroshi.vessel.config.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.ConfigLib;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.maboroshi.vessel.config.objects.CommandAction;
import org.maboroshi.vessel.config.objects.FilterRule;
import org.maboroshi.vessel.config.objects.FilterRule.FilterMode;
import org.maboroshi.vessel.config.objects.effects.EffectGroup;

@Configuration
public class VesselTemplate {
    @Comment("Item settings.")
    public ItemSettings item = new ItemSettings(
            "egg",
            "sniffer_egg",
            "<light_purple>Fragile Vessel</light_purple>",
            List.of("<gray>Right-click a mob to capture it.</gray>", "<red>Breaks upon release.</red>"),
            List.of(
                    "<gray>Right-click to release the captured mob.</gray>",
                    "<red>Breaks upon release.</red>",
                    "<reset>",
                    "<gray>Contains <white><entity_name></white> of type <white><entity_type></white>.</gray>"),
            Map.of("<entity_type>", "<entity_type>_spawn_egg"));

    @Comment("Vessel behavior settings.")
    public BehaviorSettings behavior = new BehaviorSettings();

    @Comment("Vessel restriction settings.")
    public RestrictionSettings restrictions = new RestrictionSettings();

    @Comment("Event configuration.")
    public EventRegistry events = new EventRegistry();

    public static VesselTemplate load(File vesselTemplate) {
        YamlConfigurationProperties properties = ConfigLib.PAPER_DEFAULT_PROPERTIES.toBuilder()
                .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
                .build();
        return YamlConfigurations.update(vesselTemplate.toPath(), VesselTemplate.class, properties);
    }

    @Configuration
    public static class ItemSettings {
        @Comment({"The material ID for the empty vessel.", "Nexo custom items are supported."})
        public String material = "egg";

        @Comment({"The default material ID for the filled vessel.", "Nexo custom items are supported."})
        public String filledMaterial = "sniffer_egg";

        @Comment("Display name for the empty vessel.")
        public String displayName = "Vessel";

        @Comment("Lore lines for the empty vessel.")
        public List<String> lore = new ArrayList<>();

        @Comment("Lore lines for the filled vessel.")
        public List<String> filledLore = new ArrayList<>();

        @Comment({
            "Specific material overrides based on the captured entity type.",
            "If an entity is not listed here, it falls back to 'filled-material' above."
        })
        public Map<String, String> materialOverrides = new HashMap<>();

        public ItemSettings() {}

        public ItemSettings(
                String material,
                String filledMaterial,
                String displayName,
                List<String> lore,
                List<String> filledLore,
                Map<String, String> materialOverrides) {
            this.material = material;
            this.filledMaterial = filledMaterial;
            this.displayName = displayName;
            this.lore = lore;
            this.filledLore = filledLore;
            this.materialOverrides = materialOverrides;
        }
    }

    @Configuration
    public static class BehaviorSettings {
        @Comment("Does the item get consumed upon releasing an entity?")
        public boolean consumeOnRelease = true;

        @Comment("Does the item turn back into an empty vessel upon release?")
        public boolean returnEmptyVessel = false;
    }

    @Configuration
    public static class ExclusionSettings {
        @Comment("Should a player be blocked from capturing their own tamed pets?")
        public boolean tamed = false;

        @Comment("Should a player be blocked from capturing pets tamed by other players?")
        public boolean othersTamed = true;

        @Comment("Should named mobs be excluded from capture?")
        public boolean named = false;

        @Comment({"Spawn reasons filtered from capture.", "Accepted values: NONE, BLACKLIST, WHITELIST."})
        public FilterRule spawnReasons = new FilterRule(FilterMode.BLACKLIST, List.of("CUSTOM"));
    }

    @Configuration
    public static class RestrictionSettings {
        @Comment("Exclude certain mobs based on specific states (tamed status, custom names, etc.).")
        public ExclusionSettings exclusions = new ExclusionSettings();

        @Comment("World filter rules for vessel usage.")
        public FilterRule worlds = new FilterRule();

        @Comment({
            "Entity filter rules for vessel usage.",
            "For a full list of supported group permissions and their included mobs, see:",
            "https://docs.maboroshi.org/projects/vessel/features/commands-permissions/"
        })
        public FilterRule entities = new FilterRule(FilterMode.BLACKLIST, List.of("ENDER_DRAGON", "WITHER", "WARDEN"));
    }

    @Configuration
    public static class EventRegistry {
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
                                Map.of(
                                        "capture",
                                        Sound.sound(Key.key("entity.item.pickup"), Sound.Source.MASTER, 1f, 1f)),
                                Collections.emptyMap()),
                        Collections.emptyMap());
            }

            public static EventSettings releaseDefault() {
                return new EventSettings(
                        true,
                        new EffectGroup(
                                Map.of(
                                        "release",
                                        Sound.sound(Key.key("entity.item.break"), Sound.Source.MASTER, 1f, 1f)),
                                Collections.emptyMap()),
                        Collections.emptyMap());
            }
        }
    }
}
