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
* Ensure you have the Java Development Kit (JDK) installed.
* Ensure Python 3 is installed and added to your system PATH.

### Compilation & Execution
1. Clone the repository and navigate to the `src` folder.
2. Compile the Java classes:
   ```bash
   javac *.java