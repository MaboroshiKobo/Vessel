package org.maboroshi.vessel.config.settings;

import de.exlll.configlib.Comment;
import de.exlll.configlib.ConfigLib;
import de.exlll.configlib.Configuration;
import de.exlll.configlib.NameFormatters;
import de.exlll.configlib.YamlConfigurationProperties;
import de.exlll.configlib.YamlConfigurations;
import java.io.File;
import java.nio.file.Path;

public class MessageConfig {

    public static MessageConfiguration load(File dataFolder) {
        YamlConfigurationProperties properties = ConfigLib.BUKKIT_DEFAULT_PROPERTIES.toBuilder()
                .setNameFormatter(NameFormatters.LOWER_KEBAB_CASE)
                .build();
        Path messagesFile = new File(dataFolder, "messages.yml").toPath();
        return YamlConfigurations.update(messagesFile, MessageConfiguration.class, properties);
    }

    @Configuration
    public static class MessageConfiguration {
        @Comment("The global prefix used in messages. Use <prefix> in other messages to include it.")
        public String prefix = "<color:#F2CDCD><bold>Vessel</bold> ➟ </color>";

        @Comment("Core gameplay messages for capturing and releasing entities.")
        public GeneralMessages general = new GeneralMessages();

        @Comment("Command-related messages and responses.")
        public CommandMessages commands = new CommandMessages();

        @Comment("Administrative messages.")
        public AdminMessages admin = new AdminMessages();

        @Comment("Help command messages and entry format.")
        public HelpMessages help = new HelpMessages();

        @Configuration
        public static class GeneralMessages {
            @Comment("Message shown when a player lacks permission to use a vessel.")
            public String cannotUseVessel = "<red>You cannot use this vessel!</red>";

            @Comment("Message shown when a player lacks permission to capture this entity.")
            public String cannotCapture = "<red>You cannot capture <entity_type>!</red>";

            @Comment("Message shown when a player lacks permission to release this entity.")
            public String cannotRelease = "<red>You cannot release <entity_type>!</red>";

            @Comment("Message shown when a player tries to capture another player's tamed pet.")
            public String cannotCaptureTamed = "<red>You cannot capture someone else's pet!</red>";

            @Comment("Message shown when a player tries to release a tamed mob that is excluded.")
            public String cannotReleaseTamed = "<red>You cannot release this pet!</red>";

            @Comment("Message shown when a player tries to capture a named mob while named mobs are excluded.")
            public String cannotCaptureNamed = "<red>You cannot capture this named mob!</red>";

            @Comment("Message shown when a player tries to release a named mob while named mobs are excluded.")
            public String cannotReleaseNamed = "<red>You cannot release this named mob!</red>";

            @Comment("Message shown when a player attempts to use the vessel on a blacklisted entity or spawn reason.")
            public String blacklistedEntity = "<red>You cannot use the vessel on <entity_type>!</red>";

            @Comment("Message shown when a player cannot capture in the current protected area.")
            public String cannotCaptureHere = "<red>You cannot capture mobs here!</red>";

            @Comment("Message shown when a player cannot capture in the current world.")
            public String cannotCaptureWorld = "<red>You cannot capture mobs in <world>!</red>";

            @Comment("Message shown when a player cannot release in the current protected area.")
            public String cannotReleaseHere = "<red>You cannot release mobs here!</red>";

            @Comment("Message shown when a player cannot release in the current world.")
            public String cannotReleaseWorld = "<red>You cannot release mobs in <world>!</red>";
        }

        @Configuration
        public static class CommandMessages {
            @Comment("Plugin info message shown by /vessel. Supports <version> and <authors> tags.")
            public String pluginInfo = "<green>Vessel Plugin v<version> by <authors>.</green>";

            @Comment("Invalid type message")
            public String invalidType =
                    "<red>Invalid vessel type or module disabled! Valid types: consumable, reusable.</red>";

            @Comment("Invalid amount message. Supports <min> and <max> tags.")
            public String invalidAmount = "<red>Amount must be between <min> and <max>.</red>";

            @Comment("Message sent to command sender when giving vessels. Supports <target>, <amount>, <type> tags.")
            public String giveSender = "<green>Gave <target> <amount> <type> vessel(s).</green>";

            @Comment("Message sent to player when receiving vessels. Supports <amount>, <type> tags.")
            public String givePlayer = "<green>You received <amount> <type> vessel(s).</green>";
        }

        @Configuration
        public static class AdminMessages {
            @Comment("Message shown when reload succeeds.")
            public String reloadSuccess = "<green>Vessel configuration reloaded.</green>";

            @Comment("Message shown when reload fails.")
            public String reloadFail = "<red>Failed to reload configuration: <error></red>";
        }

        @Configuration
        public static class HelpMessages {
            @Comment("Help header line shown by /vessel help")
            public String header = "<prefix> <white>Vessel commands:</white>";

            @Comment("Show usage for plugin info")
            public String show = "<prefix> <white>/vessel</white> - <gray>Shows plugin info</gray>";

            @Comment("Help: give usage")
            public String give =
                    "<prefix> <white>/vessel give <player> <type> <amount> [-s]</white> - <gray>Give vessels (permission: vessel.command.give)</gray>";

            @Comment("Help: reload usage")
            public String reload =
                    "<prefix> <white>/vessel reload</white> - <gray>Reloads plugin config (permission: vessel.command.reload)</gray>";
        }
    }
}
