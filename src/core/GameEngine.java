package core;

import entities.Enemy;
import entities.Player;
import graphics.Raycaster;
import world.MapLoader;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import entities.Item;
import items.HealthPack;
import items.Blaster;
import items.Shotgun;
import items.Rifle;
import interfaces.IConsumable;
import interfaces.IEquippable;

public class GameEngine {

    public int[][] worldMap;
    public Player player;
    public Raycaster raycaster;
    private long lastTime;
    private double pathTimer = 0;

    public int currentLevel = 1; 
    public CopyOnWriteArrayList<Enemy> enemies; 
    
    public CopyOnWriteArrayList<Item> floorItems;
    public GameState currentState = GameState.MENU; 
    
    public double startPointX = -1, startPointY = -1;
    public double exitPointX = -1, exitPointY = -1;

    public GameEngine(int width, int height) {
        this.raycaster = new Raycaster(width, height);
        this.player = new Player(1.5, 1.5, 1, 0, 0, -0.66);
        this.player.pickupWeapon(new Blaster(-1, -1));
        this.enemies = new CopyOnWriteArrayList<>();
        this.floorItems = new CopyOnWriteArrayList<>();
        this.lastTime = System.nanoTime();
    }

    public void reset() {
        currentState = GameState.LOADING;

        new Thread(() -> {
            try {

                int numRooms = (currentLevel <= 4) ? 4 : 18;
                ProcessBuilder pb = new ProcessBuilder("python", "scripts/map-generator.py", String.valueOf(numRooms));
                Process p = pb.start();
                p.waitFor(); 

                worldMap = MapLoader.loadMap("map.json");
                
                if (worldMap == null) {
                    System.err.println("CRITICAL: Failed to load map.json.");
                    return; 
                }
                
                spawnEntities(); 

                currentState = GameState.PLAYING;
                lastTime = System.nanoTime(); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void spawnEntities() {

        boolean playerSpawned = false;
        startPointX = -1; startPointY = -1;
        exitPointX = -1; exitPointY = -1;
        
        for (int x = 0; x < worldMap.length; x++) {
            for (int y = 0; y < worldMap[x].length; y++) {
                if (worldMap[x][y] == 2) {
                    player.posX = x + 1.5; 
                    player.posY = y + 0.5;
                    startPointX = x + 0.5;
                    startPointY = y + 0.5;
                    playerSpawned = true;
                } else if (worldMap[x][y] == 3) {
                    exitPointX = x + 0.5;
                    exitPointY = y + 0.5;
                }
            }
        }

        if (!playerSpawned) {
            player.posX = 1.5;
            player.posY = 1.5;
            startPointX = 1.5;
            startPointY = 1.5;
        }

        ArrayList<int[]> validSpawns = new ArrayList<>();
        for (int x = 0; x < worldMap.length; x++) {
            for (int y = 0; y < worldMap[x].length; y++) {
                if (worldMap[x][y] == 0) {
                    double dx = (x + 0.5) - player.posX;
                    double dy = (y + 0.5) - player.posY;
                    if (Math.sqrt((dx * dx) + (dy * dy)) > 30.0) {
                        validSpawns.add(new int[]{x, y});
                    }
                }
            }
        }

        enemies.clear();
        Random rand = new Random();
        int enemiesToSpawn = Math.min(3 + (currentLevel * 3), validSpawns.size());
        
        for (int i = 0; i < enemiesToSpawn; i++) {
            int enemyIndex = rand.nextInt(validSpawns.size());
            Enemy newEnemy = new Enemy(validSpawns.get(enemyIndex)[0] + 0.5, validSpawns.get(enemyIndex)[1] + 0.5);
            enemies.add(newEnemy);
            validSpawns.remove(enemyIndex);
        }
        
        floorItems.clear();

        int maxHealthPacks = Math.min(3, currentLevel);
        for (int i = 0; i < maxHealthPacks; i++) {
            if (validSpawns.isEmpty()) break;
            int hpIndex = rand.nextInt(validSpawns.size());
            floorItems.add(new HealthPack(validSpawns.get(hpIndex)[0] + 0.5, validSpawns.get(hpIndex)[1] + 0.5));
            validSpawns.remove(hpIndex);
        }

        if (currentLevel == 1) {
            if (!validSpawns.isEmpty()) {
                int gunIndex = rand.nextInt(validSpawns.size());
                floorItems.add(new Blaster(validSpawns.get(gunIndex)[0] + 0.5, validSpawns.get(gunIndex)[1] + 0.5));
                validSpawns.remove(gunIndex);
            }
        } else if (currentLevel == 2) {
            if (!validSpawns.isEmpty()) {
                int gunIndex = rand.nextInt(validSpawns.size());
                floorItems.add(new Shotgun(validSpawns.get(gunIndex)[0] + 0.5, validSpawns.get(gunIndex)[1] + 0.5));
                validSpawns.remove(gunIndex);
            }
        } else if (currentLevel == 3) {
            if (!validSpawns.isEmpty()) {
                int gunIndex = rand.nextInt(validSpawns.size());
                floorItems.add(new Rifle(validSpawns.get(gunIndex)[0] + 0.5, validSpawns.get(gunIndex)[1] + 0.5));
                validSpawns.remove(gunIndex);
            }
        }
    }

    public int[][] tick(boolean w, boolean a, boolean s, boolean d) {
        long currentTime = System.nanoTime();
        double deltaTime = (currentTime - lastTime) / 1000000000.0;
        if (deltaTime > 0.1) deltaTime = 0.1; // Prevent huge lag spikes from overshooting logic
        this.lastTime = currentTime;

        if (currentState == GameState.PLAYING) {
            player.move(w, a, s, d, worldMap, deltaTime);

            pathTimer += deltaTime;
            boolean shouldUpdatePath = false;
            
            if (pathTimer > 0.5) {
                shouldUpdatePath = true;
                pathTimer = 0;
            }

            if (player.weaponCooldown > 0) {
                player.weaponCooldown -= deltaTime;
            }

            for (Enemy e : enemies) {
                if (shouldUpdatePath) {
                    e.updatePath(worldMap, (int) player.posX, (int) player.posY);
                }
                e.move(deltaTime);

                double dist = Math.sqrt(Math.pow(player.posX - e.posX, 2) + Math.pow(player.posY - e.posY, 2));
                if (dist < 1.0 && e.attackCooldown <= 0) {
                    player.health -= 10;
                    e.attackCooldown = 1.0; 
                    System.out.println("🩸 Enemy hits! HP drops to: " + player.health);
                    if (player.health <= 0) {
                        currentState = GameState.GAME_OVER;
                        System.out.println("💀 YOU DIED!");
                    }
                }
            }

            for (int i = floorItems.size() - 1; i >= 0; i--) {
                Item item = floorItems.get(i);
                double dx = player.posX - item.posX;
                double dy = player.posY - item.posY;
                if (Math.sqrt((dx * dx) + (dy * dy)) < 0.5 && !item.isCollected) {
                    if (item instanceof IConsumable) {
                        ((IConsumable) item).consume(player);
                        floorItems.remove(i); 
                    } else if (item instanceof IEquippable) {
                        if (player.pickupWeapon((IEquippable) item)) {
                            floorItems.remove(i); 
                        }
                    }
                }
            }

            if (worldMap[(int) player.posX][(int) player.posY] == 3) {
                currentLevel++;
                System.out.println("🎉 ESCAPED! Loading Level " + currentLevel + "...");
                reset();
            }
        }

        if (worldMap != null) {
            return raycaster.castRays(player, worldMap);
        }
        return null;
    }
}