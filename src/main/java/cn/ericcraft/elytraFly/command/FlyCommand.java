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

    public FlyCommand(FlightManager flightManager) {
        this.flightManager = flightManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 检查指令发送者是否为玩家
        if (!(sender instanceof Player)) {
            sender.sendMessage("该指令只能由玩家执行。");
            return true;
        }

        Player player = (Player) sender;

        // 检查玩家是否拥有 elytrafly.use 权限
        if (!player.hasPermission("elytrafly.use")) {
            player.sendMessage(ChatColor.RED + "你没有权限使用此指令。");
            return true;
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack chestplate = inventory.getChestplate();

        // 检查玩家是否穿着鞘翅
        if (chestplate != null && chestplate.getType() == Material.ELYTRA) {
            // 检查鞘翅是否已经损坏
            if (chestplate.getItemMeta() instanceof Damageable) {
                if (((Damageable) chestplate.getItemMeta()).getDamage() >= chestplate.getType().getMaxDurability()) {
                    player.sendMessage(ChatColor.RED + "你的鞘翅已经损坏，无法飞行！");
                    return true;
                }
            }

            // 切换飞行模式
            if (flightManager.isFlying(player.getUniqueId())) {
                // 如果已经在飞行，则关闭飞行
                player.setAllowFlight(false);
                player.setFlying(false);
                flightManager.removePlayer(player.getUniqueId());
                player.sendMessage(ChatColor.AQUA + "[飞行系统] " + ChatColor.GREEN + "鞘翅飞行模式已关闭。");
            } else {
                // 如果未在飞行，则开启飞行
                player.setAllowFlight(true);
                flightManager.addPlayer(player.getUniqueId());
                player.sendMessage(ChatColor.AQUA + "[飞行系统] " + ChatColor.GREEN + "鞘翅飞行模式已开启！");
            }
        } else {
            player.sendMessage(ChatColor.AQUA + "[飞行系统] " + ChatColor.RED + "你必须穿戴鞘翅才能使用此指令。");
        }
        return true;
    }
}
