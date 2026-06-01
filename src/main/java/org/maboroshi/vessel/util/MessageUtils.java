package org.maboroshi.vessel.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.Player;
import org.maboroshi.vessel.config.ConfigManager;

public class MessageUtils {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final ConfigManager config;
    private final boolean hasPAPI;

    public MessageUtils(ConfigManager config) {
        this.config = config;
        this.hasPAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    public TagResolver getEntityTags(Entity entity) {
        if (entity == null) return TagResolver.empty();
        return TagResolver.resolver(
                tagParsed("entity_name", entity.getName() == null ? "" : entity.getName()),
                tag("entity_type", entity.getType().toString()));
    }

    public TagResolver getSnapshotTags(EntitySnapshot snapshot) {
        if (snapshot == null) return TagResolver.empty();
        return TagResolver.resolver(
                tagParsed("entity_type", snapshot.getEntityType().toString()));
    }

    public Component parse(Audience receiver, String message, TagResolver... tags) {
        if (message == null || message.isEmpty()) return Component.empty();

        String parsedMessage = message;
        if (hasPAPI && receiver instanceof Player player) {
            parsedMessage = PlaceholderAPI.setPlaceholders(player, parsedMessage);
        }

        TagResolver prefixTag = Placeholder.parsed(
                "prefix", config != null && config.getMessageConfig() != null ? config.getMessageConfig().prefix : "");

        TagResolver finalResolver;
        if (receiver instanceof Player player) {
            finalResolver = TagResolver.resolver(
                    TagResolver.resolver(tags), prefixTag, Placeholder.unparsed("player", player.getName()));
        } else {
            finalResolver = TagResolver.resolver(TagResolver.resolver(tags), prefixTag);
        }

        return MINI_MESSAGE.deserialize(parsedMessage, finalResolver);
    }

    public Component parse(CommandSender sender, String message, TagResolver... tags) {
        if (sender instanceof Player player) return parse((Audience) player, message, tags);
        if (message == null || message.isEmpty()) return Component.empty();

        String parsedMessage = message;
        TagResolver prefixTag = Placeholder.parsed(
                "prefix", config != null && config.getMessageConfig() != null ? config.getMessageConfig().prefix : "");

        TagResolver finalResolver = TagResolver.resolver(TagResolver.resolver(tags), prefixTag);
        return MINI_MESSAGE.deserialize(parsedMessage, finalResolver);
    }

    public void send(Audience receiver, String message, TagResolver... tags) {
        if (message == null || message.isEmpty()) return;
        receiver.sendMessage(parse(receiver, message, tags));
    }

    public void send(CommandSender sender, String message, TagResolver... tags) {
        if (sender == null || message == null || message.isEmpty()) return;
        sender.sendMessage(parse(sender, message, tags));
    }

    public TagResolver tag(String key, Object value) {
        return Placeholder.unparsed(key, String.valueOf(value));
    }

    public TagResolver tagParsed(String key, String value) {
        return Placeholder.parsed(key, value);
    }
}
