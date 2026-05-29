package org.maboroshi.vessel;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.maboroshi.vessel.config.ConfigManager;

public final class Vessel extends JavaPlugin {
    private static Vessel plugin;

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        plugin = this;
        this.configManager = new ConfigManager(this, getDataFolder());

        try {
            configManager.loadConfig();
            configManager.loadMessages();
        } catch (Exception e) {
            getLogger().severe("Failed to load configuration: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        @SuppressWarnings("unused")
        Metrics metrics = new Metrics(this, 31642);
    }

    @Override
    public void onDisable() {}

    public static Vessel getPlugin() {
        return plugin;
    }
}
