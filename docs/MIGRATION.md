# 重写版迁移说明 / Migration notes

现有配置、权限、插件名称和 `/fly` 保持兼容。此次工作没有自动递增发布版本；仓库中的构建版本仍由 `pom.xml` 决定。

Existing configuration, permissions, plugin identity and `/fly` remain compatible. This work does not automatically bump the release version; `pom.xml` remains authoritative.

## 行为变化 / Behavior changes

| 项目 / Area | 新行为 / New behavior |
| --- | --- |
| 关闭 / Disable | 已有会话优先关闭，不再被装备、权限或世界检查阻止。 Active sessions can always be switched off. |
| 世界与权限 / Eligibility | 开启后继续执行限制；世界变化和起飞时立即检查，其余最迟下个检查周期。 Restrictions remain active throughout flight. |
| 已有飞行 / Existing flight | 不接管创造、旁观或其他来源已赋予的飞行。 Native or existing external flight is not taken over. |
| 生命周期 / Lifecycle | 死亡时即清理，退出和踢出立即清理；重新登录不会续飞。 Sessions end on death, disconnect or kick and never resume on login. |
| 耐久 / Durability | 剩余一点时停飞并保留物品；旧版最大耐久报告被修正。 Stop at one remaining durability and retain the item. |
| 不扣耐久 / Damage bypass | 仅跳过扣除，仍拒绝损坏鞘翅；支持不可破坏及自定义最大耐久。 Bypass skips damage, not usability checks. |
| 无效配置 / Invalid settings | 拒绝启用并报告具体键，不再静默容忍错误。 Invalid settings prevent enablement with a diagnostic. |
| 空文本 / Empty messages | `""` 关闭整条提示，包括前缀。 Empty messages suppress the entire notification, including prefix. |
| 额外参数 / Arguments | `/fly foo` 显示用法，不切换状态。 Extra arguments display usage without toggling. |

新增消息 / New messages: `already-can-fly`, `permission-revoked`, `flight-revoked`, `flight-error`, `usage`。旧配置缺少它们时使用默认文本；可从默认配置复制后自定义。

Missing new messages fall back to bundled text. Copy them into your configuration to customize them. Configuration edits still require a restart; there is no reload command or persistent session storage.

## 升级与回退 / Upgrade and rollback

停服后备份 JAR 与配置，替换 JAR 后启动。先用普通生存玩家验证，OP 默认绕过世界限制，不能用于验证禁飞世界规则。回退时停服并恢复备份 JAR 与配置。没有数据库迁移。

Stop the server, back up the JAR/configuration, replace the JAR and restart. Test restrictions with a non-OP survival player: OPs bypass world restrictions by default. Roll back by stopping the server and restoring the backups. No database migration is involved.

不增加坠落保护；多个插件同时改同一玩家的飞行标志仍存在所有权歧义。详情见 README。

No fall protection is added. Concurrent flight-flag modifications by other plugins still have ownership ambiguity; see the README.
