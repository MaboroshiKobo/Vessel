package org.maboroshi.vessel.util;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.maboroshi.vessel.Vessel;
import org.maboroshi.vessel.config.ConfigManager;

public class Logger {
    private final Vessel plugin;

    public Logger(Vessel plugin) {
        this.plugin = plugin;
    }

    private ConfigManager getConfig() {
        return plugin.getConfigManager();
    }

    private void log(String colorTag, String message) {
        ConfigManager config = getConfig();
        String prefix;
        if (config != null && config.getMessageConfig() != null) prefix = config.getMessageConfig().prefix;
        else prefix = "<color:#F2CDCD><bold>Vessel</bold> ➟ </color>";
        Bukkit.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize(prefix + colorTag + message));
    }

    public void debug(String message) {
        ConfigManager config = getConfig();
        if (config != null && config.getMainConfig().debug) log("<gray>[DEBUG] </gray>", message);
    }

    public void info(String message) {
        log("", message);
    }

    public void warn(String message) {
        log("<yellow>", message);
    }

    public void error(String message) {
        log("<red>", message);
    }
}
