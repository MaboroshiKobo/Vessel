package org.maboroshi.vessel.config.settings.modules;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import java.util.List;
import org.maboroshi.vessel.config.settings.shared.ExclusionConfiguration;
import org.maboroshi.vessel.config.settings.shared.FilterConfiguration;
import org.maboroshi.vessel.config.settings.shared.FilterMode;
import org.maboroshi.vessel.config.settings.shared.ModuleEvents;

@Configuration
public class ReusableConfiguration {
    @Comment("Enable or disable the use of reusable vessels.")
    public boolean enabled = true;

    @Comment("Exclude certain mobs from reusable vessel capture and release logic.")
    public ExclusionConfiguration exclusions = new ExclusionConfiguration();

    @Comment("World filter for reusable vessels.")
    public FilterConfiguration worlds = new FilterConfiguration();

    @Comment("Entity filter for reusable vessels.")
    public FilterConfiguration entities =
            new FilterConfiguration(FilterMode.BLACKLIST, ModuleSettings.DEFAULT_ENTITY_BLACKLIST);

    @Comment({"The material ID for the reusable vessel item.", "Nexo custom items are supported."})
    public String item = "amethyst_shard";

    @Comment("Display name for the empty reusable vessel component.")
    public String displayName = "<dark_aqua>Enduring Vessel</dark_aqua>";

    @Comment("Lore lines for the empty reusable vessel item.")
    public List<String> lore =
            List.of("<gray>Right-click a mob to capture it.</gray>", "<green>Can be reused multiple times.</green>");

    @Comment("Lore lines for the filled reusable vessel item.")
    public List<String> filledLore = List.of(
            "<gray>Right-click to release the captured mob.</gray>",
            "<green>Can be reused multiple times.</green>",
            "<reset>",
            "<gray>Contains <white><entity_name></white> of type <white><entity_type></white>.</gray>");

    @Comment("Event configuration")
    public ModuleEvents events = new ModuleEvents();
}
