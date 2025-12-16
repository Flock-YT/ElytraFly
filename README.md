# ElytraFly

**English** | [中文](README_CN.md)

ElytraFly is a lightweight Spigot plugin designed for Minecraft 1.21+. It allows players equipped with an Elytra to toggle creative-like flight mode using a command, while maintaining balanced gameplay mechanics.

## Features

*   **Creative Flight**: Toggle flight mode with `/fly` while wearing an Elytra.
*   **Realistic Durability**:
    *   Durability is consumed only when the player is actually flying.
    *   Fully supports the vanilla **Unbreaking** enchantment algorithm (chance to consume durability = `1 / (level + 1)`).
*   **Smart Checks**:
    *   Flight mode is automatically disabled if the player unequips the Elytra or swaps it for another item (e.g., a chestplate).
    *   Flight is disabled immediately if the Elytra breaks during flight.
*   **Permission System**: Control who can use the flight ability.
*   **Localization**: Full Chinese feedback messages and code comments.

## Commands

*   `/fly` - Toggles Elytra flight mode on or off.
    *   **Requirement**: Must be wearing an Elytra.

## Permissions

*   `elytrafly.use` - Allows access to the `/fly` command.
    *   **Default**: OP only.

## Installation

1.  Download the plugin `.jar` file.
2.  Place it into your server's `plugins` folder.
3.  Restart the server.

## Requirements

*   Spigot/Paper 1.21 or higher.
*   Java 21 (or compatible version for your server software).
