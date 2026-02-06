"# Minecraft_ClientBot" 
Minecraft Client Bot (Autonomous Agent)
*"A client-side autonomous bot featuring custom A pathfinding and an asynchronous behavior stack architecture."**

This is a Fabric-based (MC 1.21) client-side mod that implements a fully autonomous agent. It goes beyond simple pathfinding by incorporating Context Awareness and a Task Planning System.

The core innovation is its custom "Behavior Stack" architecture, which translates abstract path nodes into atomic operation queues. It utilizes a Multi-threaded Asynchronous Callback System to execute physical interactions (mining, jumping, walking), ensuring the main game thread never freezes during complex tasks.

Key Features
1. Custom A* Pathfinding Engine
Source: net.wtf.PathFinding.Astar

Logic: A custom implementation of the A* algorithm, specifically optimized for Minecraft's voxel environment. It features a custom Heuristic Cost and G_cost calculation.

3D Navigation: Capable of handling complex 3D movement, including verticality logic like JumpUpForward and JumpDown, ensuring the bot can navigate uneven terrain.

2. "Wocao" Behavior Stack Architecture
Source: net.wtf.AutoRun.Wocao

Translation Layer: Features a unique "Translation Layer" that iterates through raw path nodes. It analyzes the relative position of the next node (e.g., JumpUpForward) and intelligently generates the corresponding operation sequence.

Context Awareness Example: If the movement is "Jump Forward", the system automatically checks for blocks above the player's head and inserts a Break operation if an obstruction is detected.

Atomic Operations: Deconstructs complex behaviors into atomic units: WAIT, WALK, BREAK, PLACE, and JUMP.

3. Asynchronous Execution & Callback System
Source: net.wtf.AutoRun.* (BreakBlock, Walking, etc.)

Thread Safety: All physical interactions are offloaded to separate Worker Threads. This prevents the Main Render Thread from blocking (Black Screen issues) during time-consuming tasks like mining obsidian.

Event-Driven: Utilizes a custom Callback interface (onComplete) to chain tasks. The next action in the queue is only triggered once the previous physical action has successfully completed.

Commands & Usage
This mod registers client-side debug commands to trigger pathfinding and behavior execution.

1. Calculate Path
Sets the target coordinates and initiates the A* algorithm.

Code snippet
/wtf debug wtfAlgo <x> <y> <z> true
x, y, z: Target integer coordinates.

true: Confirm execution.

Visuals: Renders a green path line (RenderLine) and block outlines (BlockRender) in the world.

2. Execute Behavior Queue
Translates the calculated path into the behavior queue and begins execution.

Code snippet
/wtf debug StartWocao
The bot will automatically start moving, jumping, breaking blocks, or bridging gaps based on the plan.

3. Debug Tools
/wtf debug HighlightPos Jump/Forward/Down: Manually adds single actions to the render queue for testing the rendering system.

Project Structure
Plaintext
src/main/java/net/wtf
├── PathFinding/
│   └── Astar.java       # Core Pathfinding Algorithm
├── AutoRun/
│   ├── Wocao.java       # Behavior Queue & Translator (Core Logic)
│   ├── BreakBlock.java  # Async Block Breaking Task
│   ├── Walking.java     # Async Movement Control Task
│   ├── PlaceBlock.java  # Block Placement Logic
│   └── WaitToReact.java # Thread Scheduling
├── RenderingSystem/
│   ├── RenderLine.java  # Visualizing the path vector
│   └── BlockRender.java # Visualizing target blocks
└── ClientSideCommands.java # Command Registration
Tech Stack
Language: Java 21

Framework: Fabric Loader (0.16.5) + Fabric API

Game Version: Minecraft 1.21

Build Tool: Gradle