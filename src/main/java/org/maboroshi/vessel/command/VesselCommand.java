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
import org.maboroshi.vessel.util.Logger;
import org.maboroshi.vessel.util.MessageUtils;

@Command(name = "vessel")
public class VesselCommand {

    private final Vessel plugin;
    private final Logger log;
    private final MessageUtils messageUtils;

    public VesselCommand(Vessel plugin) {
        this.plugin = plugin;
        this.log = plugin.getPluginLogger();
        this.messageUtils = plugin.getMessageUtils();
    }

    @Execute
    @Permission("vessel.command")
    void execute(@Context CommandSender sender) {
        messageUtils.send(
                sender,
                "<green>Vessel Plugin v" + plugin.getPluginMeta().getVersion() + " by "
                        + plugin.getPluginMeta().getAuthors() + ".</green>");
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
            messageUtils.send(
                    sender, "<red>Invalid vessel type or module disabled! Valid types: consumable, reusable.</red>");
            return;
        }
        if (amount < 1 || amount > 64) {
            messageUtils.send(sender, "<red>Amount must be between 1 and 64.</red>");
            return;
        }
        item.setAmount(amount);
        player.getInventory().addItem(item);
        messageUtils.send(
                sender, "<green>Gave " + player.getName() + " " + amount + " " + type + " vessel(s).</green>");
        messageUtils.send(player, "<green>You received " + amount + " " + type + " vessel(s).</green>");
        if (isSilent) {
            log.info("Gave " + player.getName() + " " + amount + " " + type + " vessel(s) silently.");
        }
    }
}
