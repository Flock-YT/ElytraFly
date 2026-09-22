package cn.ericcraft.elytraFly.config;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Messages {
    private final Map<String, String> messages;

    /** Loads language files once; config.yml messages are deliberately not consulted. */
    public static Messages load(File dataFolder, ConfigurationSection config,
                                Function<String, InputStream> resources)
            throws IOException, InvalidConfigurationException {
        Object language = config.contains("language") ? config.get("language") : "zh_CN";
        if (!"zh_CN".equals(language) && !"en_US".equals(language)) {
            throw new IllegalArgumentException("language must be zh_CN or en_US");
        }
        Path directory = dataFolder.toPath().resolve("lang");
        Files.createDirectories(directory);
        for (String name : new String[]{"zh_CN", "en_US"}) {
            Path file = directory.resolve(name + ".yml");
            if (!Files.exists(file)) {
                try (InputStream input = resource(resources, "lang/" + name + ".yml")) {
                    Files.copy(input, file);
                }
            }
        }
        String path = "lang/" + language + ".yml";
        YamlConfiguration defaults = new YamlConfiguration();
        try (InputStreamReader reader = new InputStreamReader(resource(resources, path), StandardCharsets.UTF_8)) {
            defaults.load(reader);
        }
        YamlConfiguration selected = new YamlConfiguration();
        try (InputStreamReader reader = new InputStreamReader(
                Files.newInputStream(dataFolder.toPath().resolve(path)), StandardCharsets.UTF_8)) {
            selected.load(reader);
        } catch (InvalidConfigurationException e) {
            throw new InvalidConfigurationException(path + ": " + e.getMessage(), e);
        }
        try {
            return new Messages(selected, defaults);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(path + ": " + e.getMessage(), e);
        }
    }

    private static InputStream resource(Function<String, InputStream> resources, String path) {
        return Objects.requireNonNull(resources.apply(path), "Missing bundled " + path);
    }

    public Messages(ConfigurationSection config, ConfigurationSection defaults) {
        if (config.contains("messages") && !config.isConfigurationSection("messages")) {
            throw new IllegalArgumentException("messages must be a section");
        }
        String prefix = read(config, defaults, "prefix");
        Map<String, String> resolved = new HashMap<>();
        for (String key : defaults.getConfigurationSection("messages").getKeys(false)) {
            String text = read(config, defaults, key);
            resolved.put(key, text.isEmpty() ? "" : ChatColor.translateAlternateColorCodes('&', prefix + text));
        }
        messages = Collections.unmodifiableMap(resolved);
    }

    public void send(CommandSender sender, String key) {
        String text = messages.get(key);
        if (text != null && !text.isEmpty()) sender.sendMessage(text);
    }

    private static String read(ConfigurationSection config, ConfigurationSection defaults, String key) {
        String path = "messages." + key;
        Object value = config.contains(path) ? config.get(path) : defaults.get(path);
        if (!(value instanceof String)) throw new IllegalArgumentException(path + " must be a string");
        return (String) value;
    }
}
