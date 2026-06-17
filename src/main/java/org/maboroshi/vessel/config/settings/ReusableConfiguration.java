package org.maboroshi.vessel.config.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import java.util.List;
import java.util.Map;
import org.maboroshi.vessel.config.settings.components.ItemSettings;
import org.maboroshi.vessel.config.settings.components.ModuleEvents;
import org.maboroshi.vessel.config.settings.components.RestrictionSettings;

@Configuration
public class ReusableConfiguration {
    @Comment("Enable or disable the use of reusable vessels.")
    public boolean enabled = true;

    @Comment("Capture and release restrictions.")
    public RestrictionSettings restrictions = new RestrictionSettings();

    @Comment("Item settings for this vessel type.")
    public ItemSettings item = new ItemSettings(
            "amethyst_shard",
            "echo_shard",
            "<dark_aqua>Enduring Vessel</dark_aqua>",
            List.of("<gray>Right-click a mob to capture it.</gray>", "<green>Can be reused multiple times.</green>"),
            List.of(
                    "<gray>Right-click to release the captured mob.</gray>",
                    "<green>Can be reused multiple times.</green>",
                    "<reset>",
                    "<gray>Contains <white><entity_name></white> of type <white><entity_type></white>.</gray>"),
            Map.of("COW", "leather"));

    @Comment("Event configuration")
    public ModuleEvents events = new ModuleEvents();
}
