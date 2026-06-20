package org.maboroshi.vessel.config.settings.components;

import de.exlll.configlib.Comment;
import de.exlll.configlib.Configuration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ItemSettings {
    @Comment({"The material ID for the empty vessel.", "Nexo custom items are supported."})
    public String material = "egg";

    @Comment({"The default material ID for the filled vessel.", "Nexo custom items are supported."})
    public String filledMaterial = "sniffer_egg";

    @Comment("Display name for the empty vessel.")
    public String displayName = "Vessel";

    @Comment("Lore lines for the empty vessel.")
    public List<String> lore = new ArrayList<>();

    @Comment("Lore lines for the filled vessel.")
    public List<String> filledLore = new ArrayList<>();

    @Comment({
        "Specific material overrides based on the captured entity type.",
        "If an entity is not listed here, it falls back to 'filled-material' above."
    })
    public Map<String, String> materialOverrides = new HashMap<>();

    public ItemSettings() {}

    public ItemSettings(
            String material,
            String filledMaterial,
            String displayName,
            List<String> lore,
            List<String> filledLore,
            Map<String, String> materialOverrides) {
        this.material = material;
        this.filledMaterial = filledMaterial;
        this.displayName = displayName;
        this.lore = lore;
        this.filledLore = filledLore;
        this.materialOverrides = materialOverrides;
    }
}
