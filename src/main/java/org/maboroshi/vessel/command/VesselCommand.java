package org.maboroshi.vessel.command;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.List;
import java.util.stream.IntStream;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.maboroshi.vessel.Vessel;
import org.maboroshi.vessel.config.ConfigManager;
import org.maboroshi.vessel.util.Logger;
import org.maboroshi.vessel.util.MessageUtils;
import org.maboroshi.vessel.util.UpdateChecker;

public class VesselCommand {

    private final Vessel plugin;

    public VesselCommand(Vessel plugin) {
        this.plugin = plugin;
    }

    @Suggestions("vesselTypes")
    public List<String> suggestVesselTypes(CommandContext<CommandSourceStack> context, String input) {
        return plugin.getConfigManager().getTemplateKeys().stream().toList();
    }

    @Suggestions("vesselAmounts")
    public List<String> suggestVesselAmounts(CommandContext<CommandSourceStack> context, String input) {
        return IntStream.rangeClosed(1, 64).mapToObj(String::valueOf).toList();
    }

    @Command("vessel [fallback]")
    @Permission("vessel.command.help")
    public void onHelpFallback(CommandSourceStack source, @Argument("fallback") @Nullable String fallback) {
        help(source);
    }

    @Command("vessel about")
    @Permission("vessel.command.about")
    public void onAbout(CommandSourceStack source) {
        CommandSender sender = source.getSender();
        MessageUtils messageUtils = plugin.getMessageUtils();
        ConfigManager config = plugin.getConfigManager();

        messageUtils.send(
                sender,
                config.getMessageConfig().prefix
                        + "Plugin Version: <gray><version></gray>, Authors: <gray><authors></gray>",
                messageUtils.tag("version", plugin.getPluginMeta().getVersion()),
                messageUtils.tag(
                        "authors", String.join(", ", plugin.getPluginMeta().getAuthors())));

        new UpdateChecker(plugin).checkForUpdates(sender);
    }

    @Command("vessel reload")
    @Permission("vessel.command.reload")
    public void onReload(CommandSourceStack source) {
        CommandSender sender = source.getSender();
        if (plugin.reload()) {
            plugin.getMessageUtils().send(sender, plugin.getConfigManager().getMessageConfig().commands.reloadSuccess);

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.updateCommands();
            }
        } else {
            plugin.getMessageUtils()
                    .send(
                            sender,
                            plugin.getConfigManager().getMessageConfig().commands.reloadFail,
                            plugin.getMessageUtils().tag("error", "Check console for details"));
        }
    }

    @Command("vessel help")
    @Permission("vessel.command.help")
    public void help(CommandSourceStack source) {
        CommandSender sender = source.getSender();
        MessageUtils messageUtils = plugin.getMessageUtils();
        ConfigManager config = plugin.getConfigManager();

        messageUtils.send(sender, config.getMessageConfig().help.header);
        messageUtils.send(sender, config.getMessageConfig().help.about);
        messageUtils.send(sender, config.getMessageConfig().help.help);
        messageUtils.send(sender, config.getMessageConfig().help.give);
        messageUtils.send(sender, config.getMessageConfig().help.reload);
    }

    @Command("vessel give <player> <type> <amount>")
    @Permission("vessel.command.give")
    public void give(
            CommandSourceStack source,
            @Argument("player") Player player,
            @Argument(value = "type", suggestions = "vesselTypes") String type,
            @Argument(value = "amount", suggestions = "vesselAmounts") int amount,
            @Flag(value = "silent", aliases = "s") boolean isSilent) {

        CommandSender sender = source.getSender();
        MessageUtils messageUtils = plugin.getMessageUtils();
        ConfigManager config = plugin.getConfigManager();
        Logger log = plugin.getPluginLogger();

        if (amount < 1 || amount > 64) {
            messageUtils.send(
                    sender,
                    config.getMessageConfig().commands.invalidAmount,
                    messageUtils.tag("min", 1),
                    messageUtils.tag("max", 64));
            return;
        }

        ItemStack item = plugin.getVesselManager().createEmptyVessel(type);
        if (item == null) {
            messageUtils.send(sender, config.getMessageConfig().commands.invalidType);
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
