package cn.ericcraft.elytraFly.listener;

import cn.ericcraft.elytraFly.ElytraFly;
import cn.ericcraft.elytraFly.manager.FlightManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * 玩家死亡监听器
 * <p>
 * 负责处理玩家死亡和重生事件，以确保飞行状态的同步
 */
public class PlayerDeathListener implements Listener {

    private final FlightManager flightManager;
    private final JavaPlugin plugin;

    public PlayerDeathListener(FlightManager flightManager, JavaPlugin plugin) {
        this.flightManager = flightManager;
        this.plugin = plugin;
    }

    /**
     * 当玩家重生时触发
     *
     * @param event 玩家重生事件
     */
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // 检查玩家是否在我们的飞行列表中
        if (flightManager.isFlying(playerUUID)) {
            // 玩家重生后，服务端的 allowFlight 会被重置为 false
            // 我们需要将玩家从飞行列表中移除，以同步状态
            flightManager.removePlayer(playerUUID);

            // 延迟发送消息，确保玩家能看到
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline()) {
                        player.sendMessage(ChatColor.AQUA + "[飞行系统] " + ChatColor.YELLOW + "你已重生，鞘翅飞行模式已自动关闭。");
                    }
                }
            }.runTaskLater(plugin, 20L); // 延迟1秒 (20 ticks) 后发送
        }
    }
}
