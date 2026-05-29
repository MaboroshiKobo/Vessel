package org.maboroshi.vessel.config;

import de.exlll.configlib.ConfigLib;
import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import java.io.File;
import java.nio.file.Path;
import org.maboroshi.vessel.Vessel;
import org.maboroshi.vessel.config.settings.MainConfig;
import org.maboroshi.vessel.config.settings.MainConfig.MainConfiguration;
import org.maboroshi.vessel.config.settings.MessageConfig;
import org.maboroshi.vessel.config.settings.MessageConfig.MessageConfiguration;

public class ConfigManager {
    private final File dataFolder;
    private MainConfiguration mainConfig;
    private MessageConfiguration messageConfig;

    private static final YamlConfigurationProperties PROPERTIES = ConfigLib.BUKKIT_DEFAULT_PROPERTIES.toBuilder()
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .build();

    public ConfigManager(Vessel plugin, File dataFolder) {
        this.dataFolder = dataFolder;
    }

    public void loadConfig() {
        this.mainConfig = MainConfig.load(dataFolder);
    }

    public void loadMessages() {
        this.messageConfig = MessageConfig.load(dataFolder);
    }

    public void saveConfig() {
        Path settingsPath = new File(dataFolder, "config.yml").toPath();
        YamlConfigurations.save(settingsPath, MainConfiguration.class, mainConfig, PROPERTIES);
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
}
