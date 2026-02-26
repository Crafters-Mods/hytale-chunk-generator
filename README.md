# 🚀 Chunk Loader Generate

## Overview
Chunk Loader Generate is a high-performance server-side plugin for Hytale designed to pre-generate world terrain. Inspired by the "Chunky" mod from Minecraft, it allows administrators to force the world generator to render and save chunks within a specified radius, significantly reducing exploration lag for players.

## ⚡ Core Features
- **Square Spiral Algorithm**: Generates chunks starting from a center point and moving outwards to prioritize immediate play areas.
- **Async Processing**: Runs on background threads to ensure the main game loop remains smooth.
- **TPS Throttling**: Automatically pauses or slows down generation if the server's Ticks Per Second (TPS) drop below a healthy threshold.
- **Persistence**: Saves progress to a session.json file, allowing the task to resume automatically after server restarts.

## 🎮 Player Usage

Jump straight into the action with simple commands:

| Command | Description |
|---------|-------------|
| `/chunk-generator radius <n>` | Sets the generation radius. |
| `/chunk-generator start <x> <z>` | Starts generation at specific coordinates. |
| `/chunk-generator pause` | Pauses the current generation task. |

Once started, everything else is automatic:
- Chunk generation
- Progress tracking
- TPS monitoring
- Session persistence

## 🧙 Admin Control & Configuration

Chunk Loader Generate gives admins full control without complexity.

### ⚙️ Configuration System

All generation behavior is configurable via `mods/ChunkGenerator/ChunkGenerator.json`:
- Generation radius settings
- Performance parameters
- TPS thresholds
- Session persistence options
- Debug & performance options

No complex setup. No hard dependencies.

## 🛠️ Technical Specifications

- **Plugin Version:** 1.0.2
- **File Size:** 1,346,265 bytes
- **MD5 Checksum:** c6776dcf47a6eddf5b60e139dcab9d766ed29155a94983eb4e1592a39ad9c084

### Dependencies

| Name | Version | Description |
|------|---------|-------------|
| HytaleServer | 2026.02.19-1a311a592 | Core Hytale server |
| HyUI | 0.9.0 | Enhanced user interface |
| MultipleHUD | 1.0.4 | Multiple HUD integration |

## 🎯 Key Features

- ✅ **High-Performance Generation** - Optimized algorithms for fast chunk generation
- ✅ **Async Processing** - Background processing keeps server responsive
- ✅ **TPS Throttling** - Automatic performance protection
- ✅ **Session Persistence** - Generation resumes after server restarts
- ✅ **Configurable Radius** - Flexible generation area settings
- ✅ **Integration Ready** - Works with popular UI/HUD plugins

## 🎯 Ideal For

- Server administrators
- Large multiplayer servers
- Content creators
- Custom map development
- PvE-focused servers

---

## 📢 Discord

Enter in our discord for more informations or support: [MiilhoZinho's Mods](https://discord.gg/miilhozinho)