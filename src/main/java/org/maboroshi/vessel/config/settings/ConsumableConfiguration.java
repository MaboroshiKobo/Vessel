package org.maboroshi.vessel.config.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import java.util.List;
import java.util.Map;
import org.maboroshi.vessel.config.settings.components.ItemSettings;
import org.maboroshi.vessel.config.settings.components.ModuleEvents;
import org.maboroshi.vessel.config.settings.components.RestrictionSettings;

@Configuration
public class ConsumableConfiguration {
    @Comment("Enable or disable the use of consumable vessels.")
    public boolean enabled = true;

    @Comment("Item configuration settings for this vessel type.")
    public ItemSettings item = new ItemSettings(
            "amethyst_shard",
            "echo_shard",
            "<light_purple>Fragile Vessel</light_purple>",
            List.of("<gray>Right-click a mob to capture it.</gray>", "<red>Breaks upon release.</red>"),
            List.of(
                    "<gray>Right-click to release the captured mob.</gray>",
                    "<red>Breaks upon release.</red>",
                    "<reset>",
                    "<gray>Contains <white><entity_name></white> of type <white><entity_type></white>.</gray>"),
            Map.of("COW", "cow_spawn_egg"));

    @Comment("Capture and release restrictions.")
    public RestrictionSettings restrictions = new RestrictionSettings();

    @Comment("Event configuration")
    public ModuleEvents events = new ModuleEvents();
}
