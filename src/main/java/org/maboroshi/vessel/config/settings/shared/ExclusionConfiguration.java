package org.maboroshi.vessel.config.settings.shared;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import java.util.List;

@Configuration
public class ExclusionConfiguration {
    @Comment("Should a player be blocked from capturing their own tamed pets?")
    public boolean tamed = false;

    @Comment("Should a player be blocked from capturing pets tamed by other players?")
    public boolean othersTamed = true;

    @Comment("Should named mobs be excluded from capture?")
    public boolean named = false;

    @Comment({"Spawn reasons filtered from capture.", "Accepted values: NONE, BLACKLIST, WHITELIST."})
    public FilterConfiguration spawnReasons = new FilterConfiguration(FilterMode.BLACKLIST, List.of("CUSTOM"));
}
