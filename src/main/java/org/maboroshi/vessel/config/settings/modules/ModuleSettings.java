package org.maboroshi.vessel.config.settings.modules;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import java.util.List;

@Configuration
public class ModuleSettings {
    public static final List<String> DEFAULT_ENTITY_BLACKLIST = List.of("ENDER_DRAGON", "WITHER", "WARDEN", "PLAYER");

    @Comment("Toggle the consumable vessel module.")
    public ConsumableConfiguration consumable = new ConsumableConfiguration();

    @Comment("Toggle the reusable vessel module.")
    public ReusableConfiguration reusable = new ReusableConfiguration();
}
