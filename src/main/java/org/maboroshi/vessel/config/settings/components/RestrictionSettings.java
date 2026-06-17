package org.maboroshi.vessel.config.settings.components;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;

import org.maboroshi.vessel.config.settings.ModuleSettings;
import org.maboroshi.vessel.config.settings.components.FilterSettings.FilterMode;

@Configuration
public class RestrictionSettings {
    @Comment("Exclude certain mobs based on specific states (tamed status, custom names, etc.).")
    public ExclusionSettings exclusions = new ExclusionSettings();

    @Comment("World filter rules for vessel usage.")
    public FilterSettings worlds = new FilterSettings();

    @Comment({
        "Entity filter rules for vessel usage.",
        "For a full list of supported group permissions and their included mobs, see:",
        "https://docs.maboroshi.org/projects/vessel/features/commands-permissions/"
    })
    public FilterSettings entities =
            new FilterSettings(FilterMode.BLACKLIST, ModuleSettings.DEFAULT_ENTITY_BLACKLIST);
}