# 🚀 RayForge 3D Engine

A custom-built, native desktop 3D game engine written from scratch in Java, utilizing Raycasting mathematics (similar to Wolfenstein 3D). It features a completely decoupled architecture, procedural map generation via Python, and native graphics rendering via Java Swing.

## 🧠 Academic Core Concepts

This project was built to demonstrate advanced proficiency in two core computer science domains:

### 1. Object-Oriented Programming (OOP)
* **Inheritance & Interfaces:** The rendering loop (`Main.java`) utilizes Java Swing by extending `JPanel` and implementing `Runnable` and `KeyListener` for multi-threaded, hardware-accelerated rendering and input processing.
* **Encapsulation & State Management:** The engine follows a strict Game Manager pattern. `GameEngine.java` encapsulates the world state, ensuring the player math, raycasting math, and rendering loop are decoupled and independently testable.
* **Process Delegation:** The Java engine acts as a master controller, using `ProcessBuilder` to execute external OS-level scripts (Python) and handle asynchronous thread waiting.

### 2. Data Structures & Algorithms (DSA)
* **Procedural Generation (DFS/Random Walk):** `map-generator.py` utilizes algorithm-driven random walks to procedurally carve out 100x100 2D mazes, ensuring a guaranteed valid path between the generated entrance and exit.
* **Algorithmic Optimizations:** The raycaster relies on the **Digital Differential Analyzer (DDA)** algorithm. Instead of checking every pixel, DDA rapidly calculates intersection points on the 2D grid, making it lightweight enough to render 1920x1080 rays at 60 FPS purely on the CPU.
* **Axis-Aligned Bounding Box (AABB) Collision:** The `Player` class implements AABB math against the 2D `int[][]` map array, utilizing vector projection (`Math.signum`) to slide smoothly against walls without clipping through corners.

## 🛠️ Tech Stack
* **Frontend/Renderer:** Native Java (`javax.swing.*`, `java.awt.Graphics`)
* **Backend Engine:** Java 17+
* **Procedural Map Generator:** Python 3.x
* **Data Transfer:** JSON (bridging Python algorithms to Java logic)

## 🎮 How to Run

### Prerequisites
* Ensure you have the Java Development Kit (JDK) installed (Java 17 or higher recommended).
* Ensure Python 3 is installed and added to your system PATH.

### Compilation & Execution

#### Option 1: Manual Compilation
1. Clone the repository and navigate to the project root.
2. Compile all Java classes:
   ```bash
   cd src
   javac core/*.java entities/*.java graphics/*.java world/*.java items/*.java interfaces/*.java
   ```
3. Run the game from the project root:
   ```bash
   java -cp src core.Main
   ```

#### Option 2: Using the Build Script (Recommended)
Create a `build.sh` script in the project root:
```bash
#!/bin/bash
cd src
javac core/*.java entities/*.java graphics/*.java world/*.java items/*.java interfaces/*.java
cd ..
java -cp src core.Main
```
Make it executable and run:
```bash
chmod +x build.sh
./build.sh
```

#### For Windows Users
Create `build.bat`:
```batch
@echo off
cd src
javac core\*.java entities\*.java graphics\*.java world\*.java items\*.java interfaces\*.java
cd ..
java -cp src core.Main
```

### Controls
* **W A S D** - Move around and rotate (Smooth fluid rotation controls)
* **SPACE** - Fire currently equipped weapon
* **1, 2, 3** - Swap active weapon inventory slots
* **ENTER** - Start game / Restart after death/victory
* **ESC** - Exit (via window close button)

### Game Mechanics
* 🟢 **Medkits**: Collect health packs to restore HP and universally refill all weapon ammunition pools by 50!
* 🔵 **Weapons**: Collect weapons (Blaster, Shotgun, Rifle) to equip in your inventory (max 3 slots). Picking up duplicate weapons automatically extracts and stacks the ammo.  
* 🔴 **Combat**: Enemies swarm players using proper A* routing. Survive by balancing active gun cooldowns, muzzle flashes, and spacing. Player kill-count is tracked natively!
* 🟩 **Progression**: Reach the exit (green wall portal) to progress to the next wave.
* **Wave System**: Each floor spawns increasingly more challenging clusters of enemies.

## 📁 Project Structure
```
RayForge/
├── src/
│   ├── core/              # Engine loop and state management
│   │   ├── Main.java      # Entry point, rendering thread, input handling
│   │   ├── GameEngine.java # Game logic coordinator, entity spawning
│   │   └── GameState.java # State enum (MENU, PLAYING, GAME_OVER, etc.)
│   ├── entities/          # Game objects
│   │   ├── Entity.java    # Base entity class with position
│   │   ├── Player.java    # Player with AABB collision & inventory
│   │   ├── Enemy.java     # A* pathfinding AI enemy
│   │   └── Item.java      # Base collectible item class
│   ├── graphics/          # Rendering pipeline
│   │   ├── Raycaster.java # DDA raycasting algorithm
│   │   └── Renderer.java  # Screen drawing, sprites, HUD, minimap
│   ├── world/             # Map loading
│   │   └── MapLoader.java # JSON map parser with boundary enforcement
│   ├── interfaces/        # Behavior contracts
│   │   ├── IConsumable.java # Items that can be consumed
│   │   └── IEquippable.java # Items that can be equipped as weapons
│   └── items/             # Concrete item implementations
│       ├── HealthPack.java # Restores player health
│       └── Blaster.java   # Ranged weapon with aim cone
├── scripts/
│   └── map-generator.py   # Procedural maze generation
├── .gitignore
└── README.md
```

## 🎯 Technical Deep Dive

### Advanced Raycasting & Rendering Pipeline
The `Raycaster.java` implements an upgraded DDA (Digital Differential Analyzer) algorithm:
1. Calculates ray intersections on the 2D plane based on dynamic camera bounds.
2. Evaluates the sub-block coordinate intersection (`wallX`) to perform accurate UV texture mapping seamlessly!
3. Computes floor-casting and panoramic sky-casting logic for environmental fidelity.
4. Generates a robust Z-buffer depth map exported straight to the sprite rendering core.

**Key Optimization:** Only calculates grid intersections, not every pixel natively along the ray.

### A* Pathfinding
Enemies use A* in `Enemy.java`:
- **Heuristic:** Manhattan distance (optimal for grid-based 4-directional movement)
- **Update Frequency:** Recalculates path incrementally based on internal cycle timers to sustain performance
- **Data Structure:** Priority queue (`PriorityQueue<Node>`) for efficient node selection

### Sprite Rendering & Depth Sorting
`Renderer.drawSprites()` implements a robust billboarding technique:
1. Stores all environment sprites (loot & enemies) alongside their relative geometric distances via an internal `SpriteInfo` tracker.
2. Sorts sprites recursively in descending order (Painter's algorithm) ensuring distant entities render correctly behind close entities natively fixing overlap rendering artifacts.
3. Scales projections dramatically based mathematically against Z-buffer clipping thresholds.
4. Maps texture alpha layers logically blending UI elements like crosshairs, recoil punchbacks, and dynamic minimap overlays.

### Procedural Generation
`map-generator.py` creates mazes using:
- **Algorithm:** Random walk with gradual width expansion
- **Start/Exit:** Entrance (tile type 2) at top edge, exit (tile type 3) at bottom
- **Path Guarantee:** Algorithm ensures continuous path from start to exit
- **Width Control:** Gradually expands corridor width as it approaches exit

**Map Tile Types:**
- `0` = Empty walkable space
- `1` = Wall
- `2` = Entrance spawn point
- `3` = Exit portal

## 🔧 Extending the Engine

### Adding New Weapons
1. Create a new class in `items/` that extends `Item` and implements `IEquippable`
2. Implement the `fire(GameEngine engine)` method with your weapon logic
3. Spawn it in `GameEngine.spawnEntities()`:
   ```java
   floorItems.add(new YourWeapon(x, y));
   ```

**Example:** The `Blaster.java` uses dot product for aim cone detection.

### Adding New Consumables
1. Create a class extending `Item` and implementing `IConsumable`
2. Implement `consume(Player player)` method
3. Set `isCollected = true` to mark for removal
4. Add to `floorItems` list in `GameEngine`

### Adding New Enemy Types
1. Extend `Enemy.java` or create a new class extending `Entity`
2. Implement custom movement behavior (override `move()` method)
3. Spawn in `GameEngine.spawnEntities()` loop

### Modifying Map Generation
Edit `scripts/map-generator.py`:
- **Map Size:** Change `map_size` parameter (currently 100)
- **Corridor Width:** Adjust `branch_thickness` parameter (currently 7)
- **Algorithm:** Replace `generate_base_branch()` with different maze algorithms (DFS, Prim's, Kruskal's, etc.)

## 📊 Performance Metrics
* **Resolution:** Dynamic Fullscreen (auto-scales via Java Toolkit natively)
* **Target FPS:** 60 FPS Engine Clock (capped at ~16ms per logic tick to prevent physics glitches)
* **Ray Count:** Adapts seamlessly to the horizontal width of your monitor resolution.
* **Rendering:** CPU-based with fully parsed JSON Map scaling, Textured AABB rendering, floor/sky mapping, and Z-buffered Sprite Depth sorting.
* **Pathfinding:** A* recalculated dynamically per enemy cluster based on cycle timers.

## 🐛 Known Limitations
* Python must be in system PATH for procedural map generation
* Single-threaded rendering constraints (no active GPU hardware acceleration contexts utilized intentionally)
* Maps are regenerated entirely each transition (forming the primary wave scaling mechanic seamlessly)

## 🚧 Future Enhancements
- [ ] Animated enemy/weapon state machines (Reloading sequences)
- [ ] Sound effects and spatial MIDI music integration
- [ ] Save/load system for progress serialization
- [ ] Boss enemies with advanced behavior trees
- [ ] Verticality (jump/crouch mechanical height displacements)
- [ ] Expand minimap bounds scaling parameters
- [ ] Multiplayer networking pipeline

## 📜 License
This project is open source and available for educational purposes.

## 🙏 Acknowledgments
Built as a demonstration of:
- Computer graphics (raycasting)
- Algorithm design (A*, procedural generation)
- Software architecture (OOP patterns)
- System integration (Java ↔ Python)

Inspired by Wolfenstein 3D (id Software, 1992) and modern raycasting tutorials.
