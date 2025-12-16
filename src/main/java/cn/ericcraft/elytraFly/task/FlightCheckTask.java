package cn.ericcraft.elytraFly.task;

import cn.ericcraft.elytraFly.manager.FlightManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * 飞行检查任务
 * <p>
 * 这是一个周期性任务，负责检查所有飞行玩家的状态，并在满足条件时消耗鞘翅耐久
 */
public class FlightCheckTask extends BukkitRunnable {

    private final FlightManager flightManager;

    public FlightCheckTask(FlightManager flightManager) {
        this.flightManager = flightManager;
    }

    @Override
    public void run() {
        // 使用迭代器来安全地从Map中移除元素，防止并发修改异常
        Iterator<Map.Entry<UUID, Boolean>> iterator = flightManager.getFlyingPlayers().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Boolean> entry = iterator.next();
            UUID playerUUID = entry.getKey();
            Player player = Bukkit.getPlayer(playerUUID);

            // 如果玩家下线，则从列表中移除
            if (player == null || !player.isOnline()) {
                iterator.remove();
                continue;
            }

            PlayerInventory inventory = player.getInventory();
            ItemStack chestplate = inventory.getChestplate();

            // 检查玩家是否还穿着鞘翅
            if (chestplate == null || chestplate.getType() != Material.ELYTRA) {
                // 如果玩家不再穿戴鞘翅，则取消飞行模式
                player.setAllowFlight(false);
                player.setFlying(false);
                iterator.remove();
                player.sendMessage(ChatColor.AQUA + "[飞行系统] " + ChatColor.RED + "你已不再穿戴鞘翅，飞行模式已关闭。");
                continue;
            }

            // 如果玩家正在飞行(isFlying)，则消耗鞘翅耐久
            if (player.isFlying()) {
                ItemMeta meta = chestplate.getItemMeta();
                if (meta instanceof Damageable) {
                    Damageable damageable = (Damageable) meta;

                    // 获取耐久附魔等级 (Enchantment.DURABILITY 对应 Unbreaking)
                    int unbreakingLevel = meta.getEnchantLevel(Enchantment.UNBREAKING);

                    // 计算扣除耐久的几率: 1 / (等级 + 1)
                    double chance = 1.0 / (unbreakingLevel + 1);

                    // 根据几率决定是否扣除耐久
                    if (Math.random() < chance) {
                        int currentDamage = damageable.getDamage();
                        int maxDurability = chestplate.getType().getMaxDurability();

                        // 增加1点耐久损伤
                        damageable.setDamage(currentDamage + 1);
                        chestplate.setItemMeta(meta);

                        // 检查鞘翅是否因此损坏
                        if (damageable.getDamage() >= maxDurability) {
                            inventory.setChestplate(null); // 移除损坏的鞘翅
                            player.setAllowFlight(false);
                            player.setFlying(false);
                            iterator.remove();
                            player.sendMessage(ChatColor.AQUA + "[飞行系统] " + ChatColor.RED + "你的鞘翅已经损坏！飞行模式已关闭。");
                            // 播放物品损坏的声音，给玩家明确的反馈
                            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                        }
                    }
                }
            }
        }
    }
}
