package org.maboroshi.vessel;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.maboroshi.vessel.command.VesselCommand;
import org.maboroshi.vessel.config.ConfigManager;
import org.maboroshi.vessel.handler.ActionHandler;
import org.maboroshi.vessel.handler.CooldownHandler;
import org.maboroshi.vessel.handler.EffectHandler;
import org.maboroshi.vessel.handler.VesselEventHandler;
import org.maboroshi.vessel.listener.CaptureListener;
import org.maboroshi.vessel.listener.ReleaseListener;
import org.maboroshi.vessel.listener.SpawnReasonListener;
import org.maboroshi.vessel.manager.VesselManager;
import org.maboroshi.vessel.protection.ProtectionService;
import org.maboroshi.vessel.util.Keys;
import org.maboroshi.vessel.util.Logger;
import org.maboroshi.vessel.util.MessageUtils;
import org.maboroshi.vessel.util.UpdateChecker;

public final class Vessel extends JavaPlugin {
    private static Vessel plugin;

    private ConfigManager configManager;
    private Logger log;
    private EffectHandler effectHandler;
    private CooldownHandler cooldownHandler;
    private ActionHandler actionHandler;
    private VesselManager vesselManager;
    private ProtectionService protectionService;
    private MessageUtils messageUtils;

    @Override
    public void onEnable() {
        plugin = this;
        Keys.init(this);
        this.configManager = new ConfigManager(this, getDataFolder());

        try {
            configManager.loadConfig();
            configManager.loadMessages();
            this.messageUtils = new MessageUtils(this.configManager);
        } catch (Exception e) {
            getLogger().severe("Failed to load configuration: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.log = new Logger(this, messageUtils);
        this.effectHandler = new EffectHandler(log);
        this.cooldownHandler = new CooldownHandler();
        this.actionHandler = new ActionHandler(this);
        this.vesselManager = new VesselManager(this);
        this.protectionService = ProtectionService.create(this);

        getServer().getPluginManager().registerEvents(new VesselEventHandler(this), this);
        getServer().getPluginManager().registerEvents(new SpawnReasonListener(this), this);
        getServer().getPluginManager().registerEvents(new CaptureListener(this), this);
        getServer().getPluginManager().registerEvents(new ReleaseListener(this), this);

        PaperCommandManager<CommandSourceStack> commandManager = PaperCommandManager.builder()
                .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                .buildOnEnable(this);

        AnnotationParser<CommandSourceStack> annotationParser =
                new AnnotationParser<>(commandManager, CommandSourceStack.class);

        annotationParser.parse(new VesselCommand(this));

        @SuppressWarnings("unused")
        Metrics metrics = new Metrics(this, 31642);

        new UpdateChecker(this).checkForUpdates(getServer().getConsoleSender());
    }

    public boolean reload() {
        try {
            configManager.loadConfig();
            configManager.loadMessages();
            this.messageUtils = new MessageUtils(this.configManager);

            if (cooldownHandler != null) {
                cooldownHandler.clearCooldowns();
            }

            this.vesselManager = new VesselManager(this);

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.updateCommands();
            }

            return true;
        } catch (Exception e) {
            getLogger().severe("Failed to reload Vessel configuration: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void onDisable() {}

    public static Vessel getPlugin() {
        return plugin;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public Logger getPluginLogger() {
        return log;
    }

    public EffectHandler getEffectHandler() {
        return effectHandler;
    }

    public CooldownHandler getCooldownHandler() {
        return cooldownHandler;
    }

    public VesselManager getVesselManager() {
        return vesselManager;
    }

    public MessageUtils getMessageUtils() {
        return messageUtils;
    }

    public ActionHandler getActionHandler() {
        return actionHandler;
    }

    public ProtectionService getProtectionService() {
        return protectionService;
    }
}
