# ElytraFly

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**English** | [中文](README_CN.md)

ElytraFly is a lightweight and balanced Spigot plugin for Minecraft 1.21+ that enhances the Elytra experience. It allows players to toggle creative-like flight with a simple command, while preserving vanilla-style durability mechanics.

## ✨ Features

*   **Creative Flight**: Toggle flight mode with `/fly` while wearing an Elytra.
*   **Vanilla-Friendly Durability**:
    *   Durability is consumed only when the player is actually flying.
    *   Perfectly mimics the vanilla **Unbreaking** enchantment formula (`1 / (level + 1)` chance).
*   **Smart & Safe**:
    *   Flight is automatically disabled if the Elytra is unequipped or breaks mid-air.
    *   Handles server restarts, player deaths, and world changes gracefully to prevent exploits.
*   **Permission-Ready**: A simple permission node (`elytrafly.use`) to control access.

## 📋 Requirements

*   **Server**: Spigot / Paper 1.21 or higher.
*   **Java**: Version 21 or newer.

## 🚀 Installation

1.  Download the latest `.jar` from the [Releases page](https://github.com/your-username/your-repo-name/releases).
2.  Place it into your server's `plugins` folder.
3.  Restart the server.

## 🎮 Commands & Permissions

| Command | Permission        | Description                  | Default |
|---------|-------------------|------------------------------|---------|
| `/fly`  | `elytrafly.use`   | Toggles creative Elytra flight. | OP      |
