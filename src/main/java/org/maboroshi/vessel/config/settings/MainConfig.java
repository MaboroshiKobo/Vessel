package org.maboroshi.vessel.config.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.ConfigLib;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.maboroshi.vessel.config.objects.effects.EffectGroup;
import org.maboroshi.vessel.config.objects.effects.SoundEffect;

public class MainConfig {
    public static MainConfiguration load(File dataFolder) {
        YamlConfigurationProperties properties = ConfigLib.BUKKIT_DEFAULT_PROPERTIES.toBuilder()
                .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
                .build();
        Path configFile = new File(dataFolder, "config.yml").toPath();
        return YamlConfigurations.update(configFile, MainConfiguration.class, properties);
    }

    @Configuration
    public static class MainConfiguration {
        @Comment("Enable debug mode to see detailed logs in the console.")
        public boolean debug = false;

        @Comment("Control which parts of the plugin should be active.")
        public ModuleSettings modules = new ModuleSettings();
    }

    @Configuration
    public static class ModuleSettings {
        @Comment("Toggle the consumable vessel module.")
        public ConsumableConfiguration consumable = new ConsumableConfiguration();

        @Comment("Toggle the reusable vessel module.")
        public ReusableConfiguration reusable = new ReusableConfiguration();
    }

    @Configuration
    public static class ConsumableConfiguration {
        @Comment("Enable or disable the use of consumable vessels.")
        public boolean enabled = true;

        @Comment("The material ID for the consumable vessel item.")
        public String item = "amethyst_shard";

        @Comment("Display name for the empty consumable vessel component.")
        public String displayName = "<light_purple>Fragile Vessel</light_purple>";

        @Comment("Lore lines for the empty consumable vessel item.")
        public List<String> lore =
                List.of("<gray>Right-click a mob to capture it.</gray>", "<red>Breaks upon release.</red>");

        @Comment("Lore lines for the filled consumable vessel item.")
        public List<String> filledLore = List.of(
                "<gray>Right-click to release the captured mob.</gray>",
                "<red>Breaks upon release.</red>",
                "<reset>",
                "<gray>Contains <entity_name> of type <entity_type>.</gray>");

        @Comment("List of entity types that cannot be captured in consumable vessels.")
        public List<String> blacklistedMobs = List.of("ENDER_DRAGON", "WITHER", "WARDEN", "PLAYER");

        @Comment("Effects to play when capturing or releasing entities with consumable vessels.")
        public EffectGroup captureEffects = new EffectGroup(
                new HashMap<>(Map.of("notification", new SoundEffect("block.note_block.pling", 1f, 1f))),
                new HashMap<>());

        @Comment("Effects to play when capturing or releasing entities with consumable vessels.")
        public EffectGroup releaseEffects = new EffectGroup(
                new HashMap<>(Map.of("notification", new SoundEffect("block.note_block.pling", 1f, 1f))),
                new HashMap<>());
    }

    @Configuration
    public static class ReusableConfiguration {
        @Comment("Enable or disable the use of reusable vessels.")
        public boolean enabled = true;

        @Comment("The material ID for the reusable vessel item.")
        public String item = "echo_shard";

        @Comment("Display name for the empty reusable vessel component.")
        public String displayName = "<dark_aqua>Enduring Vessel</dark_aqua>";

        @Comment("Lore lines for the empty reusable vessel item.")
        public List<String> lore = List.of(
                "<gray>Right-click a mob to capture it.</gray>", "<green>Can be reused multiple times.</green>");

        @Comment("Lore lines for the filled consumable vessel item.")
        public List<String> filledLore = List.of(
                "<gray>Right-click to release the captured mob.</gray>",
                "<green>Can be reused multiple times.</green>",
                "<reset>",
                "<gray>Contains <entity_name> of type <entity_type>.</gray>");

        @Comment("List of entity types that cannot be captured in reusable vessels.")
        public List<String> blacklistedMobs = List.of("ENDER_DRAGON", "WITHER", "WARDEN", "PLAYER");

        @Comment("Effects to play when capturing or releasing entities with reusable vessels.")
        public EffectGroup captureEffects = new EffectGroup(
                new HashMap<>(Map.of("notification", new SoundEffect("block.note_block.pling", 1f, 1f))),
                new HashMap<>());

        @Comment("Effects to play when capturing or releasing entities with reusable vessels.")
        public EffectGroup releaseEffects = new EffectGroup(
                new HashMap<>(Map.of("notification", new SoundEffect("block.note_block.pling", 1f, 1f))),
                new HashMap<>());
    }
}
