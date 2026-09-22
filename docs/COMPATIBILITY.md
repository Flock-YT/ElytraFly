# 兼容矩阵与验收 / Compatibility and validation

目标范围为 Minecraft 1.9—26.3、传统 Bukkit 主线程模型。未知分支与未列出的补丁版本按公开 API 兼容，不代表逐一实测；Folia 与模组混合服不在专项适配范围。

Target: Minecraft 1.9–26.3 on the traditional Bukkit main-thread model. Unlisted forks and patch versions are API compatibility targets, not individually certified. Folia and modded hybrids are outside dedicated support.

## 验证层级 / Validation levels

1. **编译 / Compilation**：以 Spigot 1.9 API 编译，`--release 8`，Java class major version ≤ 52。所有 API 和测试依赖均不进入发布 JAR。
2. **API 运行检查 / API execution**：使用同一 JAR、不同真实 Spigot API 依赖，在独立 JVM 中解析插件描述、加载插件类、调用物品兼容层并运行基础飞行流程。玩家、物品元数据和库存使用测试替身；早期 `Damageable` 的协变 `clone()` 测试接口在运行时编译，生产 JAR 不重新编译。
3. **服务端加载 / Server loading**：需要真正启动对应 Minecraft 服务端，确认插件加载与注册成功。
4. **玩法实测 / Gameplay**：需要玩家连接服务端，确认起飞、下落、游戏模式与事件行为。

API fixtures are not a Minecraft server. In particular, they cannot certify plugin-loader remapping, game-mode timing, client synchronization, falling damage or third-party plugin interactions.

## 本地记录 / Local results

检查日期 / Date: **2026-09-22**。本地 JDK 为 **Zulu 25.0.3**；CI 已配置按版本使用 Java 8、16、17、21、25，远程 CI 结果需以实际运行记录为准。

| API | 本地同一 JAR API 检查 / Local API fixture | CI Java |
| --- | --- | --- |
| 1.9, 1.9.4 | 通过 / Passed | 8 |
| 1.10.2, 1.11.2, 1.12.2 | 通过 / Passed | 8 |
| 1.13.2, 1.14.4, 1.15.2, 1.16.5 | 通过 / Passed | 8 |
| 1.17.1 | 通过 / Passed | 16 |
| 1.18.2, 1.19.4, 1.20.3 | 通过 / Passed | 17 |
| 1.20.6, 1.21, 1.21.11 | 通过 / Passed | 21 |
| 26.1.2, 26.2, 26.3 | 通过 / Passed | 25 |

本地回归测试 **27 项通过**，产物校验通过。上述 **19 个 API** 均使用同一份最终 JAR 检查通过。

Local regression suite: **27 passed**; artifact validation passed; **19 API versions** passed with the same final JAR.

Artifact SHA-256: `b37b12b6eb97d570ebee8ba6d98ff7f259ff491ac752fa28ba590fde5948bffa`

运行日志位于 `target/compatibility/<API>.log`，JAR 摘要在 `target/compatibility/artifact.sha256`。每轮检查确认 JAR 摘要未变化。

Logs: `target/compatibility/<API>.log`; artifact digest: `target/compatibility/artifact.sha256`. The runner checks that the artifact digest is unchanged.

## 真实服务端 / Real servers

当前环境没有配置好的 Minecraft 测试服或玩家客户端，因此以下项目**未验证**，不能把 API 检查替代为服务端实测结论。

No configured Minecraft test server or player client was available in this environment. The following checks are **not performed**; API fixture success does not substitute for them.

| 服务端 / Server | 优先验收版本 / Representative versions | 加载 / Load | 玩法 / Gameplay |
| --- | --- | --- | --- |
| CraftBukkit | 1.9、1.12.2、26.3（有可用构建时 / where available） | 未验证 / Not tested | 未验证 / Not tested |
| Spigot | 1.9、1.13.2、1.20.6、26.3 | 未验证 / Not tested | 未验证 / Not tested |
| Paper | 1.16.5、1.21.11、26.3（有可用构建时 / where available） | 未验证 / Not tested | 未验证 / Not tested |
| Purpur | 1.21.11、26.3（有可用构建时 / where available） | 未验证 / Not tested | 未验证 / Not tested |

### 服务端验收步骤 / Server acceptance checklist

使用测试世界、各服务端要求的 Java 和同一份 JAR。记录具体服务端构建号、Java 版本、JAR SHA-256 和测试结果。

Use a test world, the server's required Java version and the same JAR. Record the exact server build, Java version, artifact SHA-256 and results.

- 启动无链接错误，`/fly` 注册成功；无效配置拒绝启用并报告键。 Verify startup/command registration and rejection of invalid configuration.
- 非 OP 生存玩家有 `elytrafly.use` 时可开启、自行起飞、落地后不扣耐久、再次 `/fly` 关闭。 Test enable, takeoff, grounded durability and toggle off as a non-OP player.
- 缺少／卸下／损坏鞘翅、撤销权限、进入禁飞世界均按规则停飞；空中关闭按原版坠落。 Test equipment, permission and world restrictions, including falling after revocation.
- 普通鞘翅损伤 430 可飞；扣到 431 后停飞、物品仍可修复。验证自定义最大耐久、不可破坏、耐久附魔、概率 0/1 与两个绕过权限。 Test durability boundaries, metadata and bypasses.
- 死亡、重生、跨世界、退出、踢出、重连和停服不残留会话。 Test lifecycle cleanup and reconnect.
- 创造／旁观玩家或预先已有飞行能力不会被接管；飞行期间切换模式不破坏原版能力。 Test native/external flight and game-mode transitions.
- 若有其他飞行或物品插件，验证共同使用时的行为。 Test installed flight/item integrations separately.

回归测试另外覆盖消息默认值、空文本、配置快照、非法数值和确定性概率边界。

Regression tests additionally cover message defaults/suppression, immutable settings, invalid values and deterministic probability boundaries.
