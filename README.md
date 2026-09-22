**English** | [中文](README_CN.md)

# ElytraFly

[![CI](https://github.com/Flock-YT/ElytraFly/actions/workflows/compatibility-matrix.yml/badge.svg)](https://github.com/Flock-YT/ElytraFly/actions/workflows/compatibility-matrix.yml)
[![License: MIT](https://img.shields.io/badge/license-MIT-yellow.svg)](LICENSE)

Equip a usable elytra and run `/fly` to enable creative-style flight. Enabling flight does not force takeoff. Durability is consumed only while actually flying.

## Compatibility

Targets Minecraft **1.9–26.3** on the traditional Bukkit main-thread model: CraftBukkit, Spigot, Paper, Purpur and compatible forks. **Folia is not supported.** Modded hybrid servers are not specifically certified.

One JAR targets Java 8 bytecode. Run your server on the Java version that the server requires; modern Minecraft may require Java 17, 21 or 25. The bytecode target does not lower Minecraft's Java requirements.

See the [compatibility matrix](docs/COMPATIBILITY.md) for compilation, unchanged-JAR API checks and real-server validation. Passing API checks does not certify every fork or patch release.

## Installation and migration

1. Stop the server and back up the existing JAR and `plugins/ElytraFly/config.yml`.
2. Install `ElytraFly-<version>.jar`. Do not install Maven's `original-` intermediate artifact.
3. Start the server. Existing configuration is preserved; first startup creates the default file.
4. Restart after configuration changes. Invalid configuration prevents plugin enablement and logs the offending key.

The rewrite preserves the plugin name, `/fly`, permissions and configuration keys. Missing settings and new messages use bundled defaults without rewriting your file. See [migration notes](docs/MIGRATION.md).

## Commands and behavior

| Command / permission | Default | Purpose |
| --- | --- | --- |
| `/fly` | Player-only command | Toggle your elytra flight session; no arguments. |
| `elytrafly.use` | OP | Enable and continue using flight. |
| `elytrafly.bypass.durability` | Nobody | Skip durability consumption, but still require a usable elytra. |
| `elytrafly.bypass.world` | OP | Bypass world restrictions. |

- Creative/spectator players and players who already have flight are not taken over.
- An active session can always be switched off, even after losing permission or changing equipment/world.
- World changes and takeoff attempts validate eligibility immediately. Periodic checks catch permission and equipment changes by the next interval.
- Death, respawn, disconnect, kick and plugin disable clear sessions. Reconnecting does not resume flight.
- Switching into creative/spectator releases the session to the server's game-mode handling.
- Exhausted elytra remain equipped and repairable. Disabling damage, bypass permissions and unbreakable metadata do not make an already exhausted item usable.
- Flight removal retains vanilla falling and damage; no landing protection is added.

Bukkit exposes no flight ownership token. ElytraFly avoids taking over pre-existing flight and does not restore flight revoked by another plugin. It cannot detect another plugin granting the same `allowFlight=true` value during an active session. Avoid concurrent flight management for the same player.

## Configuration

| Key | Default | Meaning |
| --- | --- | --- |
| `settings.check-interval` | `20` | Positive integer ticks between checks and damage attempts, normally 20 ticks per second. |
| `settings.durability.enabled` | `true` | Attempt damage while actually flying. |
| `settings.durability.use-vanilla-formula` | `true` | Damage probability `1 / (Unbreaking level + 1)`. |
| `settings.durability.custom-chance` | `0.5` | Probability of one damage per interval when the vanilla formula is disabled; finite number in `[0,1]`. |
| `settings.world-list.type` | `BLACKLIST` | `BLACKLIST` or `WHITELIST`, case-insensitive mode name. |
| `settings.world-list.worlds` | Example world | Exact, case-sensitive world names. |
| `messages.*` | Bundled configuration | Legacy `&` colors; empty strings suppress messages, absent keys use defaults. |

Unbreakable metadata and modern custom maximum durability are respected. Damage reaching maximum minus one disables flight; the compatibility layer corrects the old Bukkit elytra durability report. Damage is written directly, without simulating vanilla gliding or firing `PlayerItemDamageEvent`; third-party item integrations need separate validation.

## Build and verification

Use Maven 3.x and preferably JDK 21 or 25 to build. The complete local API matrix requires JDK 25.

```bash
mvn clean verify
./scripts/verify-release-jar.sh
./scripts/verify-api-compatibility.sh
# Selected APIs, using the same previously built JAR:
./scripts/verify-api-compatibility.sh 1.9 1.21 26.3
```

Output: `target/ElytraFly-<version>.jar`. Regression tests cover configuration, sessions and durability rules. API checks use real API dependencies and test doubles in separate processes to check class loading, item adapters and basic flight behavior; they do not boot Minecraft.

CI builds once and distributes the same JAR to its API/Java matrix. Artifact checks cover version, Java 8 bytecode, resources, signature metadata and accidentally bundled API/test dependencies. The unavailable historical Spigot 1.9 chat snapshot is replaced with a pinned `provided` build dependency; it is not bundled or used through proprietary chat APIs.

## Releases and structure

The Maven version is the single release version source and is filtered into `plugin.yml`. A version change pushed to `main` triggers the existing release workflow: tests, compatibility checks, JAR validation, SHA-256 and a non-overwriting Release. Suffixed versions are prereleases; manual release from `main` remains available.

- `config`: immutable settings and message rendering.
- `compat`: item adapters using public Bukkit APIs only.
- `manager`: flight sessions and rules.
- `command`, `listener`, `task`: command, event and periodic entry points.
- `src/test`, `scripts`: regression tests and artifact verification.

[MIT license](LICENSE).
