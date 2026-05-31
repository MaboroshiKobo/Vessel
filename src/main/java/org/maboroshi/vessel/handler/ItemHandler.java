package org.maboroshi.vessel.handler;

import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemHandler {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private ItemHandler() {}

    public static void applyText(ItemMeta meta, String displayName, List<String> lore) {
        meta.displayName(noItalic(MINI_MESSAGE.deserialize(displayName)));
        meta.lore(lore.stream().map(ItemHandler::parseLine).toList());
    }

    public static Component parseLine(String line) {
        return noItalic(MINI_MESSAGE.deserialize(line));
    }

    private static Component noItalic(Component component) {
        return component.decoration(TextDecoration.ITALIC, false);
    }
}
