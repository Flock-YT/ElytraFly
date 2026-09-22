[English](README.md) | **中文**

# ElytraFly 鞘翅飞行插件

[![CI](https://github.com/Flock-YT/ElytraFly/actions/workflows/compatibility-matrix.yml/badge.svg)](https://github.com/Flock-YT/ElytraFly/actions/workflows/compatibility-matrix.yml)
[![许可证：MIT](https://img.shields.io/badge/license-MIT-yellow.svg)](LICENSE)

穿戴可用的鞘翅，通过 `/fly` 开启类似创造模式的自由飞行。开启后需要自行起飞；只有实际飞行才会按配置消耗耐久。

## 兼容范围

目标覆盖 Minecraft **1.9—26.3**，使用传统 Bukkit 主线程模型的 CraftBukkit、Spigot、Paper、Purpur 及兼容分支。**不支持 Folia 的区域线程模型**，不对模组混合服作专项兼容承诺。

发布单个 Java 8 字节码 JAR；服务器仍应使用它自身要求的 Java 版本（新服可能要求 Java 17、21、25 等）。Java 8 字节码并不代表新版 Minecraft 可以在 Java 8 上运行。

[兼容矩阵与验收记录](docs/COMPATIBILITY.md) 分别列出编译基线、同一 JAR 的 API 测试和真实服务端实测。API 测试通过不等于所有服务端、所有补丁版本均已实测。

## 安装与升级

1. 停止服务端，备份现有插件 JAR 和 `plugins/ElytraFly/config.yml`。
2. 安装 `ElytraFly-<版本>.jar`，不要安装 `original-` 开头的 Maven 中间产物。
3. 启动服务端。首次安装自动生成配置，已有配置不会被覆盖。
4. 修改配置后重启。配置非法时插件会拒绝启用，并在日志中说明具体键。

本次重写保留插件名称、`/fly`、权限节点和已有配置键，旧配置可以继续使用。缺失的设置和新消息采用内置默认值，不会自动重写用户文件。详细行为变化见[迁移说明](docs/MIGRATION.md)。

## 指令、权限与飞行规则

| 指令或权限 | 默认 | 用途 |
| --- | --- | --- |
| `/fly` | 玩家命令 | 为自己开关鞘翅飞行；不接受额外参数。 |
| `elytrafly.use` | OP | 允许开启并持续使用鞘翅飞行。 |
| `elytrafly.bypass.durability` | 无人 | 不消耗耐久，但仍必须穿戴可用的鞘翅。 |
| `elytrafly.bypass.world` | OP | 忽略世界白名单或黑名单。 |

- 创造、旁观模式及已经拥有飞行能力的玩家不会被接管。
- 已开启的会话可以随时通过 `/fly` 关闭，即使权限、装备或世界条件已经改变。
- 切换世界和尝试起飞时检查限制；定时任务持续检查权限、装备和耐久。权限或装备变化最迟在下个检查周期关闭飞行。
- 死亡、重生、离线、被踢出或停用插件时清理会话，重新登录不自动恢复。
- 切换到创造／旁观模式后，飞行交由服务端管理。
- 耐久耗尽时保留可修复的鞘翅，不删除装备。关闭耐久消耗、绕过权限或不可破坏属性均不能让已损坏的鞘翅变为可用。
- 停飞后按原版处理下落和伤害，不提供落地保护。

Bukkit 没有统一的飞行所有权标识。本插件拒绝接管开启前已有的飞行能力，也不会恢复被其他插件撤销的能力；但无法识别会话期间另一插件再次授予同一个 `allowFlight=true` 标志的情况。请避免让多个插件同时管理同一玩家的飞行。

## 配置

| 配置项 | 默认值 | 说明 |
| --- | --- | --- |
| `settings.check-interval` | `20` | 正整数，检查与扣耐久间隔，单位 tick；通常 20 tick 为一秒。 |
| `settings.durability.enabled` | `true` | 实际飞行时是否尝试扣耐久。 |
| `settings.durability.use-vanilla-formula` | `true` | 使用 `1 / (耐久附魔等级 + 1)` 的扣耐久概率。 |
| `settings.durability.custom-chance` | `0.5` | 关闭原版概率公式后，每周期扣一点的概率，必须是有限的 `[0,1]` 数值。 |
| `settings.world-list.type` | `BLACKLIST` | `BLACKLIST` 或 `WHITELIST`，模式名忽略大小写。 |
| `settings.world-list.worlds` | 示例世界 | 世界名列表，精确匹配且区分大小写。 |
| `messages.*` | 见默认配置 | 支持 `&` 颜色代码；空字符串关闭对应提示，缺失键使用内置文本。 |

支持不可破坏属性及新版自定义最大耐久。损伤达到最大耐久减一时停飞；旧版 Bukkit 的鞘翅最大耐久报告差异由兼容层修正。插件直接修改装备损伤，不模拟原版滑翔，也不触发 `PlayerItemDamageEvent`；与第三方物品系统联动需单独确认。

## 构建与验证

建议使用 JDK 21 或 25 和 Maven 3.x 构建；完整 API 矩阵本地运行需要 JDK 25。

```bash
mvn clean verify
./scripts/verify-release-jar.sh
./scripts/verify-api-compatibility.sh
# 仅检查指定 API（使用此前构建的同一个 JAR）：
./scripts/verify-api-compatibility.sh 1.9 1.21 26.3
```

产物：`target/ElytraFly-<版本>.jar`。回归测试覆盖配置、会话与耐久规则；API 测试在隔离进程中使用真实 API 和测试替身检查插件类加载、物品适配及基础飞行流程，不启动 Minecraft 服务端。

CI 先构建一次，再将同一 JAR 分发到 API／Java 矩阵。校验脚本检查版本、Java 8 字节码、资源、签名及误打包的 API／测试依赖。Spigot 1.9 引用的旧聊天库快照已不可获取，构建使用固定 `provided` 聊天库代替；运行时不打包它，也不调用其专有接口。

## 发布与结构

`pom.xml` 是版本唯一来源，构建时写入 `plugin.yml`。推送到 `main` 的版本变更会触发现有发布流程；工作流执行测试、兼容矩阵、JAR 校验和 SHA-256 生成后创建不可覆盖的 Release。版本后缀标记为预发布，也支持从 `main` 手动发布。

- `config`：不可变配置与消息。
- `compat`：仅使用公开 Bukkit API 的物品兼容层。
- `manager`：飞行会话和业务规则。
- `command`、`listener`、`task`：命令、事件与周期检查入口。
- `src/test`、`scripts`：回归测试和产物验证。

[MIT 许可证](LICENSE)。
