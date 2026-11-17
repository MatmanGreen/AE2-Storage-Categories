# AE2 Storage Categories

A NeoForge mod for **Applied Energistics 2** that organizes items into customizable, collapsible categories directly inside existing ME terminals.

## AI Disclosure

**This mod's code is 100% AI-generated using OpenAI Codex.** The project owner provided the concept, requirements, feedback, and in-game testing; they did not manually write the mod's code. Custom block textures were also generated with AI.

This statement applies to this mod's own code, not to Minecraft, AE2, NeoForge, Gradle, or other third-party dependencies and tooling.

## Features

- **ME Category Controller:** a custom category name, priority, and nine AE2 View Cell slots.
- Configure filters using the existing **Cell Workbench**.
- Supports wired terminals and Wireless Crafting Terminals.
- Remembers collapsed categories; unmatched items appear under **Miscellaneous**.
- Uses the standard AE2 item list while searching. Press **F8** to toggle the category view.

The mod changes the display, not AE2's storage system.

## Target Versions

| Component | Version |
|---|---|
| Minecraft | 1.21.1 |
| NeoForge | 21.1.248 |
| Applied Energistics 2 | 19.2.17 |
| AE2 Wireless Terminal Library (optional) | 19.5.1 |

Developed for **ATM10 Aeronautics 0.5.1**. Current version: **0.5.0 — prototype**.

## Download

**[Download experimental v0.5.0 (.jar)](https://github.com/MatmanGreen/AE2-Storage-Categories/raw/refs/heads/main/downloads/ae2-storage-categories-0.5.0-prototype.jar)**

Close Minecraft, place the JAR in your instance's `mods` folder, then start the game. Do not extract it. Remove older versions of this mod first. [Quick installation guide](downloads/README.md).

## Installation and Usage

1. Place the mod JAR in your `mods` folder. Remove older versions of this mod first.
2. Connect an **ME Category Controller** to a powered ME network.
3. Configure View Cells, insert them into the controller, and name your category.
4. Open a terminal and click category headers to expand or collapse them.

Higher priorities appear first and take precedence when filters overlap. Multiplayer requires the mod on both the server and clients; testing has primarily focused on single-player.

**Prototype notice:** Use a test world first. Some interface text is currently in German, including the miscellaneous category label, “Sonstiges.” This is an unofficial AE2 add-on.


