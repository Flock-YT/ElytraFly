[English](README.md) | **中文**

# ElytraFly 鞘翅飞行插件

[![CI](https://github.com/Flock-YT/ElytraFly/actions/workflows/compatibility-matrix.yml/badge.svg)](https://github.com/Flock-YT/ElytraFly/actions/workflows/compatibility-matrix.yml)
[![GitHub Release](https://img.shields.io/github/v/release/Flock-YT/ElytraFly)](https://github.com/Flock-YT/ElytraFly/releases/latest)
[![许可证：MIT](https://img.shields.io/badge/license-MIT-yellow.svg)](LICENSE)

ElytraFly 是一款轻量级 Spigot/Paper 插件，允许拥有权限的玩家在穿戴鞘翅时使用类似创造模式的飞行。插件保留鞘翅穿戴要求和可配置的耐久消耗，在提高飞行便利性的同时保留生存模式的成本。

## 兼容范围

- Minecraft **1.21 或更高版本**。
- Spigot 或 Paper 服务端。
- Java **21 或更高版本**；如果服务端本身要求更新的 Java，请遵循服务端要求。

CI 会使用最低支持版本和部分较新版本的 Spigot API 编译插件。编译成功不等同于已完成服务端实测，尤其是 Minecraft 新版本刚发布时。

## 功能

- 穿戴可用的鞘翅后，通过 `/fly` 开关类似创造模式的飞行。
- 仅在玩家实际飞行时消耗耐久。
- 可使用原版耐久附魔概率（`1 / (等级 + 1)`），也可设置自定义概率。
- 鞘翅被卸下或达到耐久上限、玩家重生或离线、插件停用时，自动关闭由本插件管理的飞行状态。
- 使用世界白名单或黑名单限制飞行开启。
- 自定义全部玩家提示，并支持传统 `&` 颜色代码。
- 分别控制普通使用权限和管理绕过权限。

## 安装

1. 从 [Releases 页面](https://github.com/Flock-YT/ElytraFly/releases)下载 `ElytraFly-<版本>.jar`。
2. 将 JAR 放入服务端的 `plugins` 目录。
3. 重启服务端。
4. 如有需要，编辑 `plugins/ElytraFly/config.yml`，然后重启服务端以加载文件改动。

不要安装名称以 `original-` 开头的文件；这类文件是 Maven 的中间构建产物，正式发布流程不会上传它们。

## 指令与权限

| 指令或权限 | 默认 | 用途 |
| --- | --- | --- |
| `/fly` | OP | 为指令发送者开关鞘翅飞行。 |
| `elytrafly.use` | OP | 允许使用 `/fly`。 |
| `elytrafly.bypass.durability` | 无人 | 阻止 ElytraFly 消耗鞘翅耐久。 |
| `elytrafly.bypass.world` | OP | 开启飞行时忽略世界白名单或黑名单限制。 |

`/fly` 只能由玩家执行。世界限制会在开启飞行时检查。

## 配置

默认配置文件生成在 `plugins/ElytraFly/config.yml`。

| 配置项 | 默认值 | 说明 |
| --- | --- | --- |
| `settings.check-interval` | `20` | 飞行状态与耐久检查间隔，单位为 tick（通常 20 tick 为 1 秒）。 |
| `settings.durability.enabled` | `true` | 是否在插件管理的飞行期间消耗鞘翅耐久。 |
| `settings.durability.use-vanilla-formula` | `true` | 是否使用原版耐久附魔概率。 |
| `settings.durability.custom-chance` | `0.5` | 关闭原版公式后，每次检查消耗 1 点耐久的概率。 |
| `settings.world-list.type` | `BLACKLIST` | `BLACKLIST` 允许除列表外的世界；`WHITELIST` 仅允许列表内世界。 |
| `settings.world-list.worlds` | 示例条目 | 当前名单模式使用的世界名称，精确匹配且区分大小写。 |
| `messages.*` | 见配置文件 | 前缀及玩家提示，支持传统 `&` 颜色代码。 |

自定义耐久概率应使用 `0.0`（永不消耗）到 `1.0`（每次检查都消耗 1 点）之间的小数。请让 `check-interval` 保持大于零；过小的值会增加调度开销和耐久消耗频率。

## 本地构建

需要 JDK 21 和 Maven 3.x。

```bash
mvn clean verify
./scripts/verify-release-jar.sh
```

可发布文件位于 `target/ElytraFly-<版本>.jar`。校验脚本会检查 JAR 内的插件版本、Java 21 字节码、必要资源、签名元数据，以及是否意外打包 Bukkit API。

`pom.xml` 中的项目版本是唯一的发布版本来源。Maven 会在构建时把版本写入 `plugin.yml`，无需再维护第二处硬编码版本。

## 发布流程

1. 修改 `pom.xml` 中的 `<version>`，并提交本次发布所需的改动。
2. 在本地运行 `mvn clean verify` 和 `./scripts/verify-release-jar.sh`。
3. 将版本改动推送到 `master`。

发布工作流会检测版本变化，执行完整构建与兼容性矩阵，校验实际发布的 JAR，生成 SHA-256 校验文件，并创建不可覆盖的 `v<版本>` GitHub Release。已有标签不会被覆盖。带后缀的版本（例如 `1.1.0-rc.1`）会作为预发布版本发布。也可以在 GitHub Actions 中从 `master` 手动启动发布。

## 项目结构

```text
src/main/java/        插件实现
src/main/resources/   Bukkit 元数据和默认配置
.github/workflows/    CI、兼容性检查与发布自动化
scripts/              发布创建与产物校验脚本
```

## 许可证

ElytraFly 使用 [MIT 许可证](LICENSE)。
