package org.maboroshi.vessel.config.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import java.util.List;

@Configuration
public class ModuleSettings {
    public static final List<String> DEFAULT_ENTITY_BLACKLIST = List.of("ENDER_DRAGON", "WITHER", "WARDEN");

    @Comment("Toggle the consumable vessel module.")
    public ConsumableConfiguration consumable = new ConsumableConfiguration();

    @Comment("Toggle the reusable vessel module.")
    public ReusableConfiguration reusable = new ReusableConfiguration();
}
