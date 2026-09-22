**English** | [中文](README_CN.md)

# ElytraFly

[![CI](https://github.com/Flock-YT/ElytraFly/actions/workflows/compatibility-matrix.yml/badge.svg)](https://github.com/Flock-YT/ElytraFly/actions/workflows/compatibility-matrix.yml)
[![GitHub release](https://img.shields.io/github/v/release/Flock-YT/ElytraFly)](https://github.com/Flock-YT/ElytraFly/releases/latest)
[![License: MIT](https://img.shields.io/badge/license-MIT-yellow.svg)](LICENSE)

ElytraFly is a lightweight Spigot/Paper plugin that lets permitted players use creative-style flight while wearing an Elytra. It keeps the Elytra requirement and configurable durability cost, making flight convenient without removing survival-mode trade-offs.

## Compatibility

- Minecraft **1.21 or newer**.
- Spigot or Paper servers.
- Java **21 or newer**. Use the newer runtime required by the server itself when applicable.

The CI matrix compiles the plugin against the baseline and selected newer Spigot APIs. A successful compile is not a substitute for in-server testing, especially on newly released Minecraft versions.

## Features

- Toggle creative-style flight with `/fly` while wearing a usable Elytra.
- Consume durability only while the player is actually flying.
- Use the vanilla Unbreaking probability (`1 / (level + 1)`) or a custom durability chance.
- Automatically disable managed flight when the Elytra is removed or reaches its durability limit, after respawn, when the player leaves, and when the plugin stops.
- Restrict activation with a world whitelist or blacklist.
- Customize all player-facing messages and legacy `&` color codes.
- Provide separate permissions for normal use and administrative bypasses.

## Installation

1. Download `ElytraFly-<version>.jar` from the [Releases page](https://github.com/Flock-YT/ElytraFly/releases).
2. Put the JAR in the server's `plugins` directory.
3. Restart the server.
4. Edit `plugins/ElytraFly/config.yml` if needed, then restart the server to load file changes.

Do not install files whose names begin with `original-`; they are intermediate Maven build outputs and are not published by the release workflow.

## Commands and Permissions

| Command or permission | Default | Purpose |
| --- | --- | --- |
| `/fly` | OP | Toggle ElytraFly flight for the command sender. |
| `elytrafly.use` | OP | Allow use of `/fly`. |
| `elytrafly.bypass.durability` | Nobody | Prevent ElytraFly from consuming Elytra durability. |
| `elytrafly.bypass.world` | OP | Ignore the configured world whitelist or blacklist when enabling flight. |

`/fly` can only be run by a player. The world restriction is checked when flight is enabled.

## Configuration

The default configuration is created at `plugins/ElytraFly/config.yml`.

| Key | Default | Description |
| --- | --- | --- |
| `settings.check-interval` | `20` | Flying-state and durability check interval in ticks (20 ticks is normally one second). |
| `settings.durability.enabled` | `true` | Whether managed flight consumes Elytra durability. |
| `settings.durability.use-vanilla-formula` | `true` | Use the vanilla Unbreaking probability. |
| `settings.durability.custom-chance` | `0.5` | Chance of one durability point being consumed per check when the vanilla formula is disabled. |
| `settings.world-list.type` | `BLACKLIST` | `BLACKLIST` allows every world except those listed; `WHITELIST` allows only those listed. |
| `settings.world-list.worlds` | Example entry | Exact, case-sensitive world names used by the selected list type. |
| `messages.*` | See config | Prefix and player-facing messages; legacy `&` color codes are supported. |

For a custom durability probability, use a decimal from `0.0` (never consume durability) to `1.0` (consume one point every check). Keep `check-interval` above zero; very small values increase scheduler work and durability consumption frequency.

## Building

Requirements: JDK 21 and Maven 3.x.

```bash
mvn clean verify
./scripts/verify-release-jar.sh
```

The distributable artifact is written to `target/ElytraFly-<version>.jar`. The verification script checks the embedded plugin version, Java 21 bytecode, required resources, signature metadata, and accidental Bukkit API bundling.

The project version in `pom.xml` is the single release version source. Maven filters it into `plugin.yml` during the build; do not maintain a second hard-coded plugin version.

## Release Process

1. Update `<version>` in `pom.xml` and commit the intended release changes.
2. Run `mvn clean verify` and `./scripts/verify-release-jar.sh` locally.
3. Push the version change to `master`.

The release workflow detects the version change, runs the full build and compatibility matrix, verifies the exact JAR, records its SHA-256 checksum, and creates an immutable `v<version>` GitHub release. Existing tags are never overwritten. Versions containing a suffix, such as `1.1.0-rc.1`, are published as prereleases. A release can also be started manually from `master` through GitHub Actions.

## Project Layout

```text
src/main/java/        Plugin implementation
src/main/resources/   Bukkit metadata and default configuration
.github/workflows/    CI, compatibility checks, and release automation
scripts/              Release creation and artifact verification helpers
```

## License

ElytraFly is available under the [MIT License](LICENSE).
