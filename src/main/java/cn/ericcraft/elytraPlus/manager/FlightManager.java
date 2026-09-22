package cn.ericcraft.elytraPlus.manager;

import cn.ericcraft.elytraPlus.compat.ElytraAccess;
import cn.ericcraft.elytraPlus.config.FlightSettings;
import cn.ericcraft.elytraPlus.config.Messages;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.DoubleSupplier;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Owns plugin flight sessions. All calls must run on the Bukkit main thread. */
public final class FlightManager {
    private final Set<UUID> sessions = new HashSet<>();
    private final FlightSettings settings;
    private final Messages messages;
    private final ElytraAccess items;
    private final Function<UUID, Player> players;
    private final DoubleSupplier random;
    private final Logger logger;

    public FlightManager(FlightSettings settings, Messages messages, ElytraAccess items,
                         Function<UUID, Player> players, DoubleSupplier random, Logger logger) {
        this.settings = settings;
        this.messages = messages;
        this.items = items;
        this.players = players;
        this.random = random;
        this.logger = logger;
    }

    public boolean hasSession(UUID uuid) { return sessions.contains(uuid); }

    public void toggle(Player player) {
        if (stop(player, "flight-disabled")) return;
        if (nativeFlight(player) || player.getAllowFlight() || player.isFlying()) {
            messages.send(player, "already-can-fly");
            return;
        }
        String rejection = rejection(player, false);
        if (rejection != null) {
            messages.send(player, rejection);
            return;
        }
        if (player.isDead() || !player.isOnline()) return;
        player.setAllowFlight(true);
        sessions.add(player.getUniqueId());
        messages.send(player, "flight-enabled");
    }

    /** Returns whether the player still owns a valid plugin session. */
    public boolean validate(Player player) {
        if (!hasSession(player.getUniqueId())) return false;
        if (!player.isOnline() || player.isDead() || nativeFlight(player)) {
            stop(player, null);
            return false;
        }
        if (!player.getAllowFlight()) {
            stop(player, "flight-revoked");
            return false;
        }
        String reason = rejection(player, true);
        if (reason != null) {
            stop(player, reason);
            return false;
        }
        return true;
    }

    public boolean stop(Player player, String message) {
        if (!sessions.remove(player.getUniqueId())) return false;
        if (!nativeFlight(player)) {
            player.setFlying(false);
            player.setAllowFlight(false);
        }
        if (message != null && player.isOnline()) messages.send(player, message);
        return true;
    }

    /** A successful mode transition will let the server manage flight from here. */
    public void enteringNativeMode(Player player) {
        sessions.remove(player.getUniqueId());
    }

    public void tick() {
        for (UUID uuid : new HashSet<>(sessions)) {
            Player player = players.apply(uuid);
            if (player == null) {
                sessions.remove(uuid);
                continue;
            }
            try {
                checkAndDamage(player);
            } catch (RuntimeException e) {
                logger.log(Level.SEVERE, "Flight check failed for " + uuid + "; closing session", e);
                stop(player, "flight-error");
            }
        }
    }

    private void checkAndDamage(Player player) {
        if (!validate(player) || !player.isFlying() || !settings.durabilityEnabled ||
                player.hasPermission("elytraplus.bypass.durability")) return;
        ItemStack chest = player.getInventory().getChestplate();
        if (items.isUnbreakable(chest)) return;
        double chance = settings.vanillaFormula ? 1.0 / (items.unbreakingLevel(chest) + 1.0) : settings.customChance;
        if (chance <= 0 || (chance < 1 && random.getAsDouble() >= chance)) return;
        items.damageOne(chest);
        player.getInventory().setChestplate(chest);
        if (!items.isUsable(chest)) {
            stop(player, "elytra-broke-midair");
            // String sound API avoids enum/interface binary changes across Bukkit versions.
            player.playSound(player.getLocation(), "entity.item.break", 1.0f, 1.0f);
        }
    }

    public void shutdown() {
        for (UUID uuid : new HashSet<>(sessions)) {
            Player player = players.apply(uuid);
            if (player != null) stop(player, null);
        }
        sessions.clear();
    }

    private String rejection(Player player, boolean active) {
        if (!player.hasPermission("elytraplus.use")) return active ? "permission-revoked" : "no-permission";
        if (!settings.allowsWorld(player.getWorld().getName()) && !player.hasPermission("elytraplus.bypass.world")) {
            return "world-disabled";
        }
        ItemStack chest = player.getInventory().getChestplate();
        if (chest == null || chest.getType() != Material.ELYTRA) return active ? "no-elytra-equipped" : "no-elytra";
        if (!items.isUsable(chest)) return active ? "elytra-broke-midair" : "elytra-broken";
        return null;
    }

    private static boolean nativeFlight(Player player) {
        return player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR;
    }
}
