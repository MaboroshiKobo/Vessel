package org.maboroshi.vessel.command;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.flag.Flag;
import dev.rollczi.litecommands.annotations.permission.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.maboroshi.vessel.Vessel;
import org.maboroshi.vessel.config.ConfigManager;
import org.maboroshi.vessel.util.Logger;
import org.maboroshi.vessel.util.MessageUtils;

@Command(name = "vessel")
public class VesselCommand {

    private final Vessel plugin;
    private final ConfigManager config;
    private final MessageUtils messageUtils;
    private final Logger log;

    public VesselCommand(Vessel plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.messageUtils = plugin.getMessageUtils();
        this.log = plugin.getPluginLogger();
    }

    @Execute
    @Permission("vessel.command")
    void execute(@Context CommandSender sender) {
        messageUtils.send(
                sender,
                config.getMessageConfig().commands.pluginInfo,
                messageUtils.tag("version", plugin.getPluginMeta().getVersion()),
                messageUtils.tag(
                        "authors", String.join(", ", plugin.getPluginMeta().getAuthors())));
    }

    @Execute(name = "reload")
    @Permission("vessel.command.reload")
    void reload(@Context CommandSender sender) {
        try {
            config.loadConfig();
            config.loadMessages();
            messageUtils.send(sender, config.getMessageConfig().general.reloadSuccess);
            log.info("Configuration reloaded by " + sender.getName());
        } catch (Exception e) {
            messageUtils.send(
                    sender, config.getMessageConfig().general.reloadFail, messageUtils.tag("error", e.getMessage()));
            log.error("Failed to reload configuration: " + e.getMessage());
        }
    }

    @Execute(name = "help")
    @Permission("vessel.command.help")
    void help(@Context CommandSender sender) {
        messageUtils.send(sender, config.getMessageConfig().help.header);
        messageUtils.send(sender, config.getMessageConfig().help.show);
        messageUtils.send(sender, config.getMessageConfig().help.give);
        messageUtils.send(sender, config.getMessageConfig().help.reload);
    }

    @Execute(name = "give")
    @Permission("vessel.command.give")
    void give(
            @Context CommandSender sender,
            @Arg Player player,
            @Arg String type,
            @Arg int amount,
            @Flag({"-silent", "-s"}) boolean isSilent) {
        ItemStack item = plugin.getVesselManager().createEmptyVessel(type);
        if (item == null) {
            messageUtils.send(sender, config.getMessageConfig().commands.invalidType);
            return;
        }
        if (amount < 1 || amount > 64) {
            messageUtils.send(
                    sender,
                    config.getMessageConfig().commands.invalidAmount,
                    messageUtils.tag("min", 1),
                    messageUtils.tag("max", 64));
            return;
        }
        item.setAmount(amount);

        player.getInventory()
                .addItem(item)
                .values()
                .forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));

        if (!isSilent) {
            messageUtils.send(
                    sender,
                    config.getMessageConfig().commands.giveSender,
                    messageUtils.tag("target", player.getName()),
                    messageUtils.tag("amount", amount),
                    messageUtils.tag("type", type));
            messageUtils.send(
                    player,
                    config.getMessageConfig().commands.givePlayer,
                    messageUtils.tag("amount", amount),
                    messageUtils.tag("type", type));
        } else {
            log.info("Gave " + player.getName() + " " + amount + " " + type + " vessel(s) silently.");
        }
    }
}
