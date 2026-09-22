package cn.ericcraft.elytraFly;

import cn.ericcraft.elytraFly.config.FlightSettings;
import cn.ericcraft.elytraFly.config.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConfigurationTest {
    @Test void defaultsAndSnapshot() {
        YamlConfiguration config = new YamlConfiguration();
        FlightSettings settings = new FlightSettings(config);
        assertEquals(20, settings.interval); assertTrue(settings.durabilityEnabled);
        assertTrue(settings.allowsWorld("world")); assertFalse(settings.allowsWorld("example_world_name"));
        config.set("settings.world-list.worlds", Collections.singletonList("world"));
        assertTrue(settings.allowsWorld("world"));
    }
    @Test void whitelistIsCaseSensitiveAndModeIsCaseInsensitive() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("settings.world-list.type", "whitelist");
        config.set("settings.world-list.worlds", Arrays.asList("World", "World"));
        FlightSettings settings = new FlightSettings(config);
        assertTrue(settings.allowsWorld("World")); assertFalse(settings.allowsWorld("world"));
    }
    @Test void rejectsMalformedValuesWithTheirPaths() {
        invalid("settings.check-interval", 0); invalid("settings.check-interval", -1);
        invalid("settings.check-interval", 1.5); invalid("settings.check-interval", "20");
        invalid("settings.durability.custom-chance", Double.NaN);
        invalid("settings.durability.custom-chance", Double.POSITIVE_INFINITY);
        invalid("settings.durability.custom-chance", -0.1); invalid("settings.durability.custom-chance", 1.1);
        invalid("settings.durability.enabled", "true");
        invalid("settings.world-list.type", "ALLOW");
        invalid("settings.world-list.worlds", "world");
        invalid("settings.world-list.worlds", Arrays.asList("world", 7));
        invalid("settings.durability", false);
    }
    private void invalid(String key, Object value) {
        YamlConfiguration config = new YamlConfiguration(); config.set(key, value);
        assertTrue(assertThrows(IllegalArgumentException.class, () -> new FlightSettings(config)).getMessage().contains(key));
    }
    @Test void messageDefaultsColorAndExplicitSilence() {
        YamlConfiguration defaults = new YamlConfiguration();
        defaults.set("messages.prefix", "&bPrefix "); defaults.set("messages.test", "&aText");
        YamlConfiguration config = new YamlConfiguration(); CommandSender sender = mock(CommandSender.class);
        new Messages(config, defaults).send(sender, "test"); verify(sender).sendMessage("§bPrefix §aText");
        reset(sender); config.set("messages.test", ""); new Messages(config, defaults).send(sender, "test");
        verifyNoInteractions(sender);
        config.set("messages.test", 1); assertThrows(IllegalArgumentException.class, () -> new Messages(config, defaults));
    }
}
