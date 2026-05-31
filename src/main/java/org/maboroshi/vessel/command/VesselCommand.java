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

@Command(name = "vessel")
public class VesselCommand {

    private final Vessel plugin;
    private final Logger log;

    public VesselCommand(Vessel plugin) {
        this.plugin = plugin;
        this.log = plugin.getPluginLogger();
    }

    @Execute
    @Permission("vessel.command")
    void execute(@Context CommandSender sender) {
        sender.sendRichMessage("<green>Vessel Plugin v" + plugin.getPluginMeta().getVersion() + " by "
                + plugin.getPluginMeta().getAuthors() + ".");
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
            sender.sendRichMessage("<red>Invalid vessel type or module disabled! Valid types: consumable, reusable.");
            return;
        }
        if (amount < 1 || amount > 64) {
            sender.sendRichMessage("<red>Amount must be between 1 and 64.</red>");
            return;
        }
        item.setAmount(amount);
        player.getInventory().addItem(item);
        sender.sendRichMessage("<green>Gave " + player.getName() + " " + amount + " " + type + " vessel(s).");
        player.sendRichMessage("<green>You received " + amount + " " + type + " vessel(s).");
        if (isSilent) {
            log.info("Gave " + player.getName() + " " + amount + " " + type + " vessel(s) silently.");
        }
    }
}
