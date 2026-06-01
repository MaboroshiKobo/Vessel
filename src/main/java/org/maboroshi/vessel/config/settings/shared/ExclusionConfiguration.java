package org.maboroshi.vessel.config.settings.shared;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import java.util.List;

@Configuration
public class ExclusionConfiguration {
    @Comment("Should tamed mobs be excluded from capture?")
    public boolean tamed = true;

    @Comment("Should named mobs be excluded from capture?")
    public boolean named = false;

    @Comment({"Spawn reasons allowed for capture.", "Accepted values: NONE, BLACKLIST, WHITELIST."})
    public FilterConfiguration spawnReasons = new FilterConfiguration(FilterMode.BLACKLIST, List.of("CUSTOM"));
}
