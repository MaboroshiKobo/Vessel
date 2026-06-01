package org.maboroshi.vessel.config.settings.shared;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import java.util.List;

@Configuration
public class FilterConfiguration {
    @Comment({"How this filter should be interpreted.", "Accepted values: NONE, BLACKLIST, WHITELIST."})
    public FilterMode mode = FilterMode.NONE;

    @Comment("Values used by the selected filter mode.")
    public List<String> values = List.of();

    public FilterConfiguration() {}

    public FilterConfiguration(FilterMode mode, List<String> values) {
        this.mode = mode;
        this.values = values;
    }
}
