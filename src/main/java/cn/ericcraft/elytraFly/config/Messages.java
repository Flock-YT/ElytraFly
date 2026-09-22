package cn.ericcraft.elytraFly.config;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Messages {
    private final Map<String, String> messages;

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
