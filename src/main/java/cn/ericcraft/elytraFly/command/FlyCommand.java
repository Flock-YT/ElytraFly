package cn.ericcraft.elytraFly.command;

import cn.ericcraft.elytraFly.manager.FlightManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;

/**
 * /fly 指令的执行器
 * <p>
 * 负责处理玩家输入的 /fly 指令，并根据玩家状态和权限进行相应的操作
 */
public class FlyCommand implements CommandExecutor {

    private final FlightManager flightManager;
    private final cn.ericcraft.elytraFly.ElytraFly plugin;

    public FlyCommand(FlightManager flightManager, cn.ericcraft.elytraFly.ElytraFly plugin) {
        this.flightManager = flightManager;
        this.plugin = plugin;
    }

    private String getMessage(String key) {
        String prefix = plugin.getConfig().getString("messages.prefix", "");
        String msg = plugin.getConfig().getString("messages." + key);
        if (msg == null)
            return "";
        return ChatColor.translateAlternateColorCodes('&', prefix + msg);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 检查指令发送者是否为玩家
        if (!(sender instanceof Player)) {
            sender.sendMessage(getMessage("only-player"));
            return true;
        }

        Player player = (Player) sender;

        // 检查玩家是否拥有 elytrafly.use 权限
        if (!player.hasPermission("elytrafly.use")) {
            player.sendMessage(getMessage("no-permission"));
            return true;
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack chestplate = inventory.getChestplate();

        // 检查玩家是否穿着鞘翅
        if (chestplate != null && chestplate.getType() == Material.ELYTRA) {
            // 检查鞘翅是否已经损坏
            if (chestplate.getItemMeta() instanceof Damageable) {
                if (((Damageable) chestplate.getItemMeta()).getDamage() >= chestplate.getType().getMaxDurability()) {
                    player.sendMessage(getMessage("elytra-broken"));
                    return true;
                }
            }

            // World Check
            String worldName = player.getWorld().getName();
            String listType = plugin.getConfig().getString("settings.world-list.type", "BLACKLIST");
            java.util.List<String> worlds = plugin.getConfig().getStringList("settings.world-list.worlds");

            boolean allowed = true;
            if ("WHITELIST".equalsIgnoreCase(listType)) {
                if (!worlds.contains(worldName))
                    allowed = false;
            } else { // BLACKLIST
                if (worlds.contains(worldName))
                    allowed = false;
            }

            if (!allowed && !player.hasPermission("elytrafly.bypass.world")) {
                player.sendMessage(getMessage("world-disabled"));
                return true;
            }

            // 切换飞行模式
            if (flightManager.isFlying(player.getUniqueId())) {
                // 如果已经在飞行，则关闭飞行
                player.setAllowFlight(false);
                player.setFlying(false);
                flightManager.removePlayer(player.getUniqueId());
                player.sendMessage(getMessage("flight-disabled"));
            } else {
                // 如果未在飞行，则开启飞行
                player.setAllowFlight(true);
                flightManager.addPlayer(player.getUniqueId());
                player.sendMessage(getMessage("flight-enabled"));
            }
        } else {
            player.sendMessage(getMessage("no-elytra"));
        }
        return true;
    }
}
