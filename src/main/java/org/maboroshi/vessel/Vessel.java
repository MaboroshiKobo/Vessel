package org.maboroshi.vessel;

import dev.rollczi.litecommands.LiteCommands;
import dev.rollczi.litecommands.argument.ArgumentKey;
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory;
import dev.rollczi.litecommands.suggestion.SuggestionResult;
import java.util.stream.IntStream;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
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
import org.maboroshi.vessel.util.Logger;
import org.maboroshi.vessel.util.MessageUtils;
import org.maboroshi.vessel.util.NamespacedKeys;
import org.maboroshi.vessel.util.UpdateChecker;

public final class Vessel extends JavaPlugin {
    private static Vessel plugin;

    private ConfigManager configManager;
    private Logger log;
    private EffectHandler effectHandler;
    private CooldownHandler cooldownHandler;
    private ActionHandler actionHandler;
    private LiteCommands<CommandSender> commandManager;
    private VesselManager vesselManager;
    private ProtectionService protectionService;
    private MessageUtils messageUtils;

    @Override
    public void onEnable() {
        plugin = this;
        NamespacedKeys.load(this);
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

        this.commandManager = LiteBukkitFactory.builder("vessel", this)
                .commands(new VesselCommand(this))
                .argumentSuggestion(String.class, ArgumentKey.of("type"), SuggestionResult.of("consumable", "reusable"))
                .argumentSuggestion(
                        int.class,
                        SuggestionResult.of(IntStream.rangeClosed(1, 64)
                                .mapToObj(String::valueOf)
                                .toList()))
                .build();

        @SuppressWarnings("unused")
        Metrics metrics = new Metrics(this, 31642);

        new UpdateChecker(this).checkForUpdates();
    }

    public boolean reload() {
        try {
            configManager.loadConfig();
            configManager.loadMessages();
            this.messageUtils = new MessageUtils(this.configManager);
            
            if (cooldownHandler != null) {
                cooldownHandler.clearCooldowns();
            }
            
            return true;
        } catch (Exception e) {
            log.error("Failed to reload Vessel configuration: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void onDisable() {
        if (this.commandManager != null) {
            this.commandManager.unregister();
        }
    }

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
