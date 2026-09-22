package cn.ericcraft.elytraFly;

import cn.ericcraft.elytraFly.command.FlyCommand;
import cn.ericcraft.elytraFly.compat.BukkitElytraAccess;
import cn.ericcraft.elytraFly.config.FlightSettings;
import cn.ericcraft.elytraFly.config.Messages;
import cn.ericcraft.elytraFly.listener.FlightListener;
import cn.ericcraft.elytraFly.manager.FlightManager;
import cn.ericcraft.elytraFly.task.FlightCheckTask;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public final class ElytraFly extends JavaPlugin {
    private FlightManager flights;
    private BukkitTask task;

    @Override
    public void onEnable() {
        try {
            saveDefaultConfig();
            YamlConfiguration config = new YamlConfiguration();
            config.load(new File(getDataFolder(), "config.yml"));
            YamlConfiguration defaults;
            try (InputStreamReader reader = new InputStreamReader(
                    Objects.requireNonNull(getResource("config.yml"), "Missing bundled config.yml"), StandardCharsets.UTF_8)) {
                defaults = new YamlConfiguration();
                defaults.load(reader);
            }
            FlightSettings settings = new FlightSettings(config);
            Messages messages = new Messages(config, defaults);
            flights = new FlightManager(settings, messages, new BukkitElytraAccess(),
                    uuid -> getServer().getPlayer(uuid), () -> ThreadLocalRandom.current().nextDouble(), getLogger());
            PluginCommand command = Objects.requireNonNull(getCommand("fly"), "Missing /fly in plugin.yml");
            command.setExecutor(new FlyCommand(flights, messages));
            getServer().getPluginManager().registerEvents(new FlightListener(flights, messages), this);
            task = getServer().getScheduler().runTaskTimer(this, new FlightCheckTask(flights), settings.interval, settings.interval);
            getLogger().info("ElytraFly enabled (Bukkit compatibility mode).");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Cannot enable ElytraFly: " + e.getMessage(), e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (task != null) { task.cancel(); task = null; }
        if (flights != null) { flights.shutdown(); flights = null; }
    }
}
