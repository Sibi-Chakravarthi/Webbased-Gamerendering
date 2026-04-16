<div align="center">

# 🚀 RayForge Engine

**A highly-optimized, procedural 2.5D Raycasting Engine built natively in Java.**

[![Java 17+](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Python 3](https://img.shields.io/badge/Python-3-3776AB?style=for-the-badge&logo=python&logoColor=white)](https://www.python.org/)
[![Architectural Pattern](https://img.shields.io/badge/Pattern-Game_Loop-4B32C3?style=for-the-badge)](#)

*RayForge utilizes classic 90s DOOM-style pseudo-3D mathematics pushed to a modern engine logic standard. No external GPU libraries. Pure CPU computational power mapped directly via Java2D Data Buffers.*

</div>

## ✨ Key Features
- **True Texture Mapping**: Ray-intersections query precise sub-block horizontal planes calculating strict 2D-to-UV bounds mappings. Includes Floor-Casting and Panoramic Box geometries.
- **Dynamic Z-Buffering**: Advanced Painter’s Algorithm mapping rendering depths dynamically ensuring precise sprite overlap without collision artifacts. 
- **A* Pathfinding Artificial Intelligence**: Real-time Manhattan distance routing recalculated asynchronously mapped across a dynamic Grid space. 
- **Python Procedural Generation Bridge**: The Java core dispatches an executable pipeline requesting unique JSON 100x100 architectural grids directly mapped out via internal python walker-node functions per wave layer.
- **Fully Weaponized Arsenals**: Diverse, modular implementations utilizing `interfaces` for multi-slotted DOOM combat structures (Blasters, Shotguns, Rifles) featuring spread checks, ammo stacking logic, active Crosshairs, HUD updates, recoil displacement modifiers, and tracking loops.
- **Dynamic Window Resizing Environment**: Natively scales up into any Desktop layout stripping borders for pure immersive edge-to-edge tracking.

---

## 🎮 How to Play

### Installation & Execution
RayForge Engine uses **Java 17+** natively and relies on **Python 3** purely for backend procedural generation hooks. 

**Option 1 - Manual Launch:**
Navigate inside the root project directory and compile straight from the source:
```bash
cd src
javac core/*.java entities/*.java graphics/*.java world/*.java items/*.java interfaces/*.java
cd ..
java -cp src core.Main
```

### Controls & Navigation
RayForge tracks fluid continuous keyboard states.
* **Movement:** `[W] [A] [S] [D]` parameters standard routing and rotation.
* **Attack:** `[SPACEBAR]` triggers continuous fire depending on active weapon firing thresholds. 
* **Swap Weapons:** `[1] [2] [3]` quick switches rendering/inventory states organically.
* **Flow State:** `[ENTER]` handles dynamic interactions (Starting generation processing / Advancing after Victory / Respawning).

*Survive the wave thresholds, scavenge weapons/ammo, and reach the Green Portal exit to force-spawn deeper wave complexes incrementally.*

---

## 📂 Architecture & File Structure

The project was explicitly decoupled isolating Graphical projections from rigid body logical matrices guaranteeing clean scaling parameters continuously. 

```text
RayForge-Engine/
├── src/
│   ├── core/                        # Engine Loops & State Dispatchers
│   │   ├── Main.java                # Hardware Interface & Frame Processing KeyListeners
│   │   ├── GameEngine.java          # World Master State encapsulated configurations
│   │   └── GameState.java           # Internal enum flags defining global render views
│   │ 
│   ├── graphics/                    # Rendering Pipelines 
│   │   ├── Raycaster.java           # DDA (Digital Differential Analyzer) mathematical engine
│   │   ├── Renderer.java            # HUD, Texture Scaling, MiniMap, & Painter's sorting logic
│   │   └── Texture.java             # Low-level ImageIO Pixel Array extractors
│   │
│   ├── entities/                    # AABB collision bodies
│   │   ├── Entity.java              # Standard Geometric inheritance setup 
│   │   ├── Player.java              # Complex collision routing / inventory matrices
│   │   ├── Enemy.java               # Advanced A* heuristic queue-tree nodes
│   │   └── Item.java                # Geometric anchor point bounds for ground items
│   │
│   ├── items/                       # Polymorphic item blueprints
│   │   ├── Blaster.java             # Base accurate hitrate hitscan mechanics
│   │   ├── Shotgun.java             # Wide-array piercing dispersion mechanics
│   │   ├── Rifle.java               # Low-cool down automated fire logic loops
│   │   └── HealthPack.java          # Multi-consumable object returning Player HP/Ammos
│   │
│   ├── interfaces/                  # Strict contractual rulesets 
│   │   ├── IConsumable.java         # Handlers overriding player state loops 
│   │   └── IEquippable.java         # Enforcer methods isolating custom gun logics 
│   │
│   └── world/                       # JSON bridging mechanisms
│       └── MapLoader.java           # Array parser converting generated coordinates
│
├── scripts/
│   └── map-generator.py             # Python DFS traversal carving JSON boundaries
│
└── res/
    └── textures/                    # Isolated Native Resource Packages handling `.png` conversions
```

---

## 🔧 Technical Masterclass Pipeline

### 1. The DDA Algorithm (`Raycaster.java`)
Unlike brute force engines checking every microscopic coordinate vector to identify walls, RayForge traces rays along explicit grid bound intercepts scaling directly against mathematical delta increments:
- Traces screen bounds exactly parallel across dynamically acquired camera sizes native to the user's desktop view-ports.
- Intercept depths are mapped cleanly into a `Z-Buffer` float array handling distortion calculations mathematically to defeat standard "Fish-Eye" phenomena intrinsically. 

### 2. Depth Buffering (`Renderer.java`)
Instead of allowing entities to "clip" dynamically across arrays as standard 2D render loops frequently mistake, all visible Sprites are evaluated per-frame, encapsulated into temporary `SpriteInfo` structures checking relative distances to the Player plane, and inherently sorted descending to ensure back-rendered sprites never draw atop visually closer targets.

### 3. A* Search Logic (`Enemy.java`)
Pathfinding prevents brute force looping. Grid calculations are parsed natively using prioritized queuing algorithms searching lowest possible `F-Costs` globally. The heuristic enforces Manhattan-distance calculation blocks bypassing diagonal logic mapping clean grid-based routes incrementally across wave timers. 


## 📋 Known Constraints
* Due to isolated backend logic parsing, Python 3 is absolutely mandatory within the OS Path configurations before executing runtime.
* To achieve pure unadulterated Native CPU rendering parameters, modern graphical hardware accelerations have been expressly ignored keeping it tightly encapsulated in standard `AWT/Swing` configurations.

---
<div align="center">
<i>Forged meticulously for foundational Computer Science algorithm implementations.</i>
</div>
