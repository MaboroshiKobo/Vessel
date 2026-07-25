package org.maboroshi.vessel.util;

import java.util.function.BooleanSupplier;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.maboroshi.vessel.config.ConfigManager;

public final class Messages {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static ConfigManager configManager;
    private static BooleanSupplier papiSupplier = () -> false;

    private Messages() {}

    public static void init(ConfigManager config) {
        configManager = config;
        papiSupplier = () -> Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public static void send(Audience receiver, String message, TagResolver... tags) {
        if (receiver == null || message == null || message.isEmpty()) return;

        String parsedMessage = message;
        if (papiSupplier.getAsBoolean() && receiver instanceof Player player) {
            parsedMessage = PlaceholderAPI.setPlaceholders(player, parsedMessage);
        }

        String prefix = (configManager != null && configManager.getMessageConfig() != null)
                ? configManager.getMessageConfig().prefix
                : "";

        TagResolver prefixTag = Placeholder.parsed("prefix", prefix);
        TagResolver finalResolver;

        if (receiver instanceof Player player) {
            finalResolver = TagResolver.resolver(
                    TagResolver.resolver(tags), prefixTag, Placeholder.unparsed("player", player.getName()));
        } else {
            finalResolver = TagResolver.resolver(TagResolver.resolver(tags), prefixTag);
        }

        receiver.sendMessage(MINI_MESSAGE.deserialize(parsedMessage, finalResolver));
    }

    public static TagResolver tag(String key, Object value) {
        return Placeholder.unparsed(key, String.valueOf(value));
    }

    public static TagResolver tagParsed(String key, String value) {
        return Placeholder.parsed(key, value);
    }
}
