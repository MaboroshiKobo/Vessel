package org.maboroshi.vessel.config;

import de.exlll.configlib.ConfigLib;
import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.maboroshi.vessel.Vessel;
import org.maboroshi.vessel.config.settings.MainConfig;
import org.maboroshi.vessel.config.settings.MainConfig.MainConfiguration;
import org.maboroshi.vessel.config.settings.MessageConfig;
import org.maboroshi.vessel.config.settings.MessageConfig.MessageConfiguration;
import org.maboroshi.vessel.config.settings.VesselTemplate;

public class ConfigManager {
    private final File dataFolder;
    private final Vessel plugin;

    private MainConfiguration mainConfig;
    private MessageConfiguration messageConfig;
    private Map<String, VesselTemplate> vesselTemplates;

    private static final YamlConfigurationProperties PROPERTIES = ConfigLib.PAPER_DEFAULT_PROPERTIES.toBuilder()
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .build();

    public ConfigManager(Vessel plugin, File dataFolder) {
        this.dataFolder = dataFolder;
        this.plugin = plugin;
        this.vesselTemplates = new HashMap<>();
    }

    public void loadConfig() {
        this.mainConfig = MainConfig.load(dataFolder);
        loadVesselTemplates();
    }

    private void loadVesselTemplates() {
        vesselTemplates.clear();

        File templateFolder = new File(dataFolder, "vessels");
        if (!templateFolder.exists()) {
            templateFolder.mkdirs();
        }

        File defaultConsumableTemplate = new File(templateFolder, "consumable.yml");
        if (!defaultConsumableTemplate.exists()) {
            VesselTemplate.load(defaultConsumableTemplate);
        }

        File defaultReusableTemplate = new File(templateFolder, "reusable.yml");
        if (!defaultReusableTemplate.exists()) {
            VesselTemplate reusableTemplate = new VesselTemplate();

            reusableTemplate.item.displayName = "<dark_aqua>Enduring Vessel</dark_aqua>";
            reusableTemplate.item.lore = List.of(
                    "<gray>Right-click a mob to capture it.</gray>", "<green>Can be reused multiple times.</green>");
            reusableTemplate.item.filledLore = List.of(
                    "<gray>Right-click to release the captured mob.</gray>",
                    "<green>Can be reused multiple times.</green>",
                    "<reset>",
                    "<gray>Contains <white><entity_name></white> of type <white><entity_type></white>.</gray>");
            reusableTemplate.behavior.returnEmptyVessel = true;

            YamlConfigurations.save(
                    defaultReusableTemplate.toPath(), VesselTemplate.class, reusableTemplate, PROPERTIES);
        }

        File[] files = templateFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();

                if (fileName.contains(" ")) {
                    plugin.getPluginLogger()
                            .warn("Vessel template '" + fileName + "' contains spaces and was skipped.");
                    continue;
                }

                String id = fileName.substring(0, fileName.lastIndexOf('.')).toLowerCase(Locale.ROOT);
                VesselTemplate template = VesselTemplate.load(file);
                vesselTemplates.put(id, template);
            }
        }
    }

    public void loadMessages() {
        this.messageConfig = MessageConfig.load(dataFolder);
    }

    public void saveConfig() {
        Path settingsPath = new File(dataFolder, "config.yml").toPath();
        YamlConfigurations.save(settingsPath, MainConfiguration.class, mainConfig, PROPERTIES);

        for (Map.Entry<String, VesselTemplate> entry : vesselTemplates.entrySet()) {
            Path path = new File(dataFolder, "vessels/" + entry.getKey() + ".yml").toPath();
            YamlConfigurations.save(path, VesselTemplate.class, entry.getValue(), PROPERTIES);
        }
    }

    public void saveMessages() {
        Path path = new File(dataFolder, "messages.yml").toPath();
        YamlConfigurations.save(path, MessageConfiguration.class, messageConfig, PROPERTIES);
    }

    public MainConfiguration getMainConfig() {
        return mainConfig;
    }

    public MessageConfiguration getMessageConfig() {
        return messageConfig;
    }

    public VesselTemplate getVesselTemplate(String id) {
        return vesselTemplates.get(id.toLowerCase(Locale.ROOT));
    }

    public Map<String, VesselTemplate> getVesselTemplates() {
        return vesselTemplates;
    }

    public Collection<String> getTemplateKeys() {
        return vesselTemplates.keySet();
    }
}
