package org.maboroshi.vessel.config.settings.modules;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import java.util.List;
import org.maboroshi.vessel.config.settings.shared.ExclusionConfiguration;
import org.maboroshi.vessel.config.settings.shared.FilterConfiguration;
import org.maboroshi.vessel.config.settings.shared.FilterMode;
import org.maboroshi.vessel.config.settings.shared.ModuleEvents;

@Configuration
public class ConsumableConfiguration {
    @Comment("Enable or disable the use of consumable vessels.")
    public boolean enabled = true;

    @Comment("Exclude certain mobs from consumable vessel capture and release logic.")
    public ExclusionConfiguration exclusions = new ExclusionConfiguration();

    @Comment("World filter for consumable vessels.")
    public FilterConfiguration worlds = new FilterConfiguration();

    @Comment("Entity filter for consumable vessels.")
    public FilterConfiguration entities =
            new FilterConfiguration(FilterMode.BLACKLIST, ModuleSettings.DEFAULT_ENTITY_BLACKLIST);

    @Comment({"The material ID for the consumable vessel item.", "Nexo custom items are supported."})
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
            "<gray>Contains <white><entity_name></white> of type <white><entity_type></white>.</gray>");

    @Comment("Event configuration")
    public ModuleEvents events = new ModuleEvents();
}
