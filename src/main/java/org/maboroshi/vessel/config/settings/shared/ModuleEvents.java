package org.maboroshi.vessel.config.settings.shared;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;

@Configuration
public class ModuleEvents {
    @Comment("Capture event settings for this module")
    public VesselEvent capture = VesselEvent.captureDefault();

    @Comment("Release event settings for this module")
    public VesselEvent release = VesselEvent.releaseDefault();
}
