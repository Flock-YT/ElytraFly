package cn.ericcraft.elytraFly.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 飞行管理器
 * <p>
 * 负责管理所有正在使用鞘翅飞行的玩家
 */
public class FlightManager {

    // 使用Map存储正在飞行的玩家 (UUID) 和一个布尔值 (通常为true)
    private final Map<UUID, Boolean> flyingPlayers = new HashMap<>();

    /**
     * 检查玩家是否正在飞行
     *
     * @param uuid 玩家的UUID
     * @return 如果玩家在飞行列表中，则返回true
     */
    public boolean isFlying(UUID uuid) {
        return flyingPlayers.containsKey(uuid);
    }

    /**
     * 将玩家添加到飞行列表
     *
     * @param uuid 玩家的UUID
     */
    public void addPlayer(UUID uuid) {
        flyingPlayers.put(uuid, true);
    }

    /**
     * 从飞行列表中移除玩家
     *
     * @param uuid 玩家的UUID
     */
    public void removePlayer(UUID uuid) {
        flyingPlayers.remove(uuid);
    }

    /**
     * 获取所有正在飞行的玩家的Map
     *
     * @return 包含所有飞行玩家UUID的Map
     */
    public Map<UUID, Boolean> getFlyingPlayers() {
        return flyingPlayers;
    }
}
