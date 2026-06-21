package org.maboroshi.vessel.util;

import org.bukkit.Bukkit;
import org.maboroshi.vessel.Vessel;
import org.maboroshi.vessel.config.ConfigManager;

public class Logger {
    private final Vessel plugin;
    private final MessageUtils messageUtils;

    public Logger(Vessel plugin, MessageUtils messageUtils) {
        this.plugin = plugin;
        this.messageUtils = messageUtils;
    }

    private ConfigManager getConfig() {
        return plugin.getConfigManager();
    }

    private void log(String colorTag, String message) {
        messageUtils.send(Bukkit.getConsoleSender(), "<prefix> " + colorTag + message);
    }

    public void debug(String message) {
        ConfigManager config = getConfig();
        if (config != null && config.getMainConfig().debug) log("<gray>[DEBUG] </gray>", message);
    }

    public void info(String message) {
        log("<white>", message);
    }

    public void warn(String message) {
        log("<yellow>", message);
    }

    public void error(String message) {
        log("<red>", message);
    }
}
