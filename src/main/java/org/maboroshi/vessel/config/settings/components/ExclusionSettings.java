package org.maboroshi.vessel.config.settings.components;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import java.util.List;
import org.maboroshi.vessel.config.settings.components.FilterSettings.FilterMode;

@Configuration
public class ExclusionSettings {
    @Comment("Should a player be blocked from capturing their own tamed pets?")
    public boolean tamed = false;

    @Comment("Should a player be blocked from capturing pets tamed by other players?")
    public boolean othersTamed = true;

    @Comment("Should named mobs be excluded from capture?")
    public boolean named = false;

    @Comment({"Spawn reasons filtered from capture.", "Accepted values: NONE, BLACKLIST, WHITELIST."})
    public FilterSettings spawnReasons = new FilterSettings(FilterMode.BLACKLIST, List.of("CUSTOM"));
}
