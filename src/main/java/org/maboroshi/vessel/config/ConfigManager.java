package org.maboroshi.vessel.config;

import de.exlll.configlib.ConfigLib;
import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.maboroshi.vessel.Vessel;
import org.maboroshi.vessel.config.settings.MainConfig;
import org.maboroshi.vessel.config.settings.MainConfig.MainConfiguration;
import org.maboroshi.vessel.config.settings.MessageConfig;
import org.maboroshi.vessel.config.settings.MessageConfig.MessageConfiguration;
import org.maboroshi.vessel.config.settings.modules.ConsumableConfiguration;
import org.maboroshi.vessel.config.settings.modules.ReusableConfiguration;

public class ConfigManager {
    private final File dataFolder;
    private MainConfiguration mainConfig;
    private MessageConfiguration messageConfig;
    private ConsumableConfiguration consumableConfig;
    private ReusableConfiguration reusableConfig;

    private static final YamlConfigurationProperties PROPERTIES = ConfigLib.BUKKIT_DEFAULT_PROPERTIES.toBuilder()
            .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
            .build();

    public ConfigManager(Vessel plugin, File dataFolder) {
        this.dataFolder = dataFolder;
    }

    public void loadConfig() {
        this.mainConfig = MainConfig.load(dataFolder);
        File modulesDir = new File(dataFolder, "modules");
        try {
            Files.createDirectories(modulesDir.toPath());
        } catch (IOException ignored) {
        }

        Path consumablePath = new File(modulesDir, "consumable.yml").toPath();
        Path reusablePath = new File(modulesDir, "reusable.yml").toPath();

        ConsumableConfiguration consumable =
                YamlConfigurations.update(consumablePath, ConsumableConfiguration.class, PROPERTIES);
        ReusableConfiguration reusable =
                YamlConfigurations.update(reusablePath, ReusableConfiguration.class, PROPERTIES);

        this.consumableConfig = consumable;
        this.reusableConfig = reusable;

        saveConfig();
    }

    public void loadMessages() {
        this.messageConfig = MessageConfig.load(dataFolder);
    }

    public void saveConfig() {
        Path settingsPath = new File(dataFolder, "config.yml").toPath();
        YamlConfigurations.save(settingsPath, MainConfiguration.class, mainConfig, PROPERTIES);

        File modulesDir = new File(dataFolder, "modules");
        try {
            Files.createDirectories(modulesDir.toPath());
        } catch (IOException ignored) {
        }

        Path consumablePath = new File(modulesDir, "consumable.yml").toPath();
        Path reusablePath = new File(modulesDir, "reusable.yml").toPath();

        if (this.consumableConfig != null) {
            YamlConfigurations.save(consumablePath, ConsumableConfiguration.class, this.consumableConfig, PROPERTIES);
        }
        if (this.reusableConfig != null) {
            YamlConfigurations.save(reusablePath, ReusableConfiguration.class, this.reusableConfig, PROPERTIES);
        }
    }

    public void saveMessages() {
        Path path = new File(dataFolder, "messages.yml").toPath();
        YamlConfigurations.save(path, MessageConfiguration.class, messageConfig, PROPERTIES);
    }

    public MainConfiguration getMainConfig() {
        return mainConfig;
    }

    public ConsumableConfiguration getConsumableConfig() {
        return consumableConfig;
    }

    public ReusableConfiguration getReusableConfig() {
        return reusableConfig;
    }

    public MessageConfiguration getMessageConfig() {
        return messageConfig;
    }
}
