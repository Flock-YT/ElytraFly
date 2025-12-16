package cn.ericcraft.elytraFly;

import cn.ericcraft.elytraFly.command.FlyCommand;
import cn.ericcraft.elytraFly.manager.FlightManager;
import cn.ericcraft.elytraFly.task.FlightCheckTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

/**
 * 插件主类
 * <p>
 * 负责插件的启用和停用逻辑，以及初始化各个组件
 */
public final class ElytraFly extends JavaPlugin {

    private FlightManager flightManager;

    @Override
    public void onEnable() {
        // 初始化飞行管理器
        flightManager = new FlightManager();

        // 注册 /fly 指令，并将飞行管理器注入到指令执行器中
        this.getCommand("fly").setExecutor(new FlyCommand(flightManager));

        // 启动飞行检查任务，并将飞行管理器注入
        new FlightCheckTask(flightManager).runTaskTimer(this, 0L, 20L); // 每秒检查一次

        getLogger().info("ElytraFly 插件已成功加载！");
    }

    @Override
    public void onDisable() {
        // 插件关闭时，确保所有通过本插件飞行的玩家都停止飞行
        if (flightManager != null) {
            for (UUID playerUUID : flightManager.getFlyingPlayers().keySet()) {
                Player player = Bukkit.getPlayer(playerUUID);
                if (player != null) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                }
            }
        }
        getLogger().info("ElytraFly 插件已成功卸载！");
    }
}
