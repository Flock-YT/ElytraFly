[English](README.md) | **中文**

# ElytraPlus 鞘翅飞行插件

穿上鞘翅，输入 `/fly`，就能像创造模式一样自由飞行，无需烟花。只有实际飞行时才会消耗耐久。

## 安装

1. 从 [下载页面](https://github.com/Flock-YT/ElytraPlus/releases) 下载 `ElytraPlus-版本号.jar`。
2. 关闭服务端，将文件放入服务端的 `plugins` 文件夹，再启动服务端。
3. 穿上有耐久的鞘翅，使用 OP 账号在生存模式下体验。

适用于 Minecraft **1.9 至 26.3** 的 Bukkit、Spigot、Paper、Purpur 服务端，不支持 Folia。已进行自动兼容检查，尚未逐版本实服测试；建议先在测试服试用。

升级时先备份 `plugins/ElytraPlus` 文件夹，再停服替换旧插件文件。已有配置会保留。

## 怎么飞

1. 把鞘翅穿在胸甲栏，不能只放在背包里。
2. 在游戏中输入 `/fly` 开启飞行。
3. 像创造模式一样双击跳跃键起飞；默认按住空格上升、Shift 下降。
4. 落地后再次输入 `/fly`，即可关闭。

**鞘翅损坏、脱下鞘翅或进入禁飞世界时会停止飞行，可能摔伤。** 请留意耐久，安全落地后再关闭。损坏的鞘翅会保留，可以修复后继续使用。死亡或重新登录后，需要重新输入 `/fly`。

## 让普通玩家也能用

默认只有 OP 可以使用。请在你使用的权限插件中，给玩家或玩家组添加 `elytraplus.use` 权限；想让所有普通玩家都能用，就添加到默认玩家组。

| 权限名称 | 作用 | 默认谁有 |
| --- | --- | --- |
| `elytraplus.use` | 使用鞘翅飞行 | OP |
| `elytraplus.bypass.durability` | 本插件飞行不消耗耐久，仍需穿戴可用鞘翅 | 无人 |
| `elytraplus.bypass.world` | 在禁飞世界也能飞 | OP |

**测试世界限制时，请使用没有绕过权限的普通玩家账号。** OP 默认不受世界限制。

## 修改设置

配置文件在 `plugins/ElytraPlus/config.yml`，首次启动后自动生成。默认配置就能使用，按需修改即可。

- `true` 表示开启，`false` 表示关闭。
- 只改需要的值，保留每行开头的空格，不要用 Tab 缩进。
- 保存后重启服务端生效，本插件没有重载命令。

### 不想消耗耐久

找到 `durability`，把它下面的 `enabled: true` 改为 `enabled: false`。即使关闭消耗，也不能用已经损坏的鞘翅起飞。

默认会考虑鞘翅的「耐久」附魔，等级越高越省耐久。想自己设置消耗概率，可以看配置文件中 `custom-chance` 的注释。

### 禁止某些世界飞行

世界限制默认关闭（`type: false`）。

找到 `settings` 下面的 `world-list`，替换为以下内容，保留前面的空格。这个例子禁止在下界和末地使用：

```yaml
  world-list:
    type: BLACKLIST
    worlds:
      - "world_nether"
      - "world_the_end"
```

- 把世界名换成你服务器实际使用的名称，大小写必须一致，通常就是世界文件夹名。
- 想只允许列表中的世界飞行，把 `BLACKLIST` 改为 `WHITELIST`。
- 想允许所有世界，把 `type` 改为 `false`（即 `type: false`）。列表中的世界名不再参与限制，`worlds` 仍须保持列表格式；权限和鞘翅装备等其他条件仍然生效。

### 语言和提示文字

`language: zh_CN` 使用中文，改成 `language: en_US` 使用英文。

想修改游戏里的提示，打开 `plugins/ElytraPlus/lang/zh_CN.yml`（英文对应 `en_US.yml`），修改引号里的文字即可。支持 `&` 颜色代码；改成 `""` 可以隐藏该条提示。保存后重启生效。

从旧版升级时，原来写在 `config.yml` 的 `messages` 提示需要手动复制到对应语言文件中。

### 关闭使用统计

找到 `bstats`，把它下面的 `enabled: true` 改成 `enabled: false`，保存后重启即可。

## 常见问题

- **提示没有权限？** 请让管理员给你添加 `elytraplus.use` 权限。
- **提示开启了但没飞起来？** 开启后需要自己双击跳跃键，不会自动升空。
- **提示已经拥有飞行能力？** 创造、旁观模式或其他插件已经允许你飞行，无需再次开启。若有多个插件使用 `/fly`，可用 `/elytraplus:fly` 指定本插件的命令。
- **修改配置后插件不能用了？** 检查缩进和填写的值，服务端控制台会指出有问题的设置。也可以恢复备份后重启。

遇到其他问题，可以到 [问题反馈](https://github.com/Flock-YT/ElytraPlus/issues) 附上服务端版本、问题出现的步骤和报错信息。

[MIT 许可证](LICENSE)。

[![ElytraPlus 插件安装量统计图](https://bstats.org/signatures/bukkit/ElytraPlus.svg)](https://bstats.org/plugin/bukkit/ElytraPlus/34208)
