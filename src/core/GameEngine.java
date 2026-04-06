package core;

import entities.Enemy;
import entities.Player;
import graphics.Raycaster;
import world.MapLoader;
import java.util.ArrayList;
import java.util.Random;

import entities.Item;
import items.HealthPack;
import items.Blaster;
import interfaces.IConsumable;
import interfaces.IEquippable;

public class GameEngine {

    public int[][] worldMap;
    public Player player;
    public Raycaster raycaster;
    private long lastTime;
    private double pathTimer = 0;
    
    // 🎓 OOP Flex: Dynamic Scaling Variables!
    public int currentLevel = 1; 
    public ArrayList<Enemy> enemies; 
    
    public ArrayList<Item> floorItems;
    public GameState currentState = GameState.MENU; 

    public GameEngine() {
        this.raycaster = new Raycaster();
        this.player = new Player(1.5, 1.5, 1, 0, 0, -0.66);
        this.enemies = new ArrayList<>();
        this.floorItems = new ArrayList<>();
        this.lastTime = System.nanoTime();
    }

    public void reset() {
        currentState = GameState.LOADING;

        new Thread(() -> {
            try {
                // Generate a brand new map for the new level!
                ProcessBuilder pb = new ProcessBuilder("python", "scripts/map-generator.py");
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
        for (int x = 0; x < worldMap.length; x++) {
            for (int y = 0; y < worldMap[x].length; y++) {
                if (worldMap[x][y] == 2) {
                    player.posX = x + 1.5; 
                    player.posY = y + 0.5;
                    playerSpawned = true;
                    break;
                }
            }
            if (playerSpawned) break; 
        }

        if (!playerSpawned) {
            player.posX = 1.5;
            player.posY = 1.5;
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
        int enemiesToSpawn = Math.min(currentLevel, validSpawns.size());
        
        for (int i = 0; i < enemiesToSpawn; i++) {
            int enemyIndex = rand.nextInt(validSpawns.size());
            Enemy newEnemy = new Enemy(validSpawns.get(enemyIndex)[0] + 0.5, validSpawns.get(enemyIndex)[1] + 0.5);
            enemies.add(newEnemy);
            validSpawns.remove(enemyIndex);
        }
        
        floorItems.clear();
        floorItems.add(new HealthPack(player.posX, player.posY + 1.0)); 
        floorItems.add(new Blaster(player.posX + 1.0, player.posY)); 
    }

    public int[][] tick(boolean w, boolean a, boolean s, boolean d) {
        long currentTime = System.nanoTime();
        double deltaTime = (currentTime - lastTime) / 1000000000.0;
        this.lastTime = currentTime;

        if (currentState == GameState.PLAYING) {
            player.move(w, a, s, d, worldMap, deltaTime);

            pathTimer += deltaTime;
            boolean shouldUpdatePath = false;
            if (pathTimer > 0.5) {
                shouldUpdatePath = true;
                pathTimer = 0;
            }

            for (Enemy e : enemies) {
                if (shouldUpdatePath) {
                    e.updatePath(worldMap, (int) player.posX, (int) player.posY);
                }
                e.move(deltaTime);

                double dist = Math.sqrt(Math.pow(player.posX - e.posX, 2) + Math.pow(player.posY - e.posY, 2));
                if (dist < 0.8) {
                    currentState = GameState.GAME_OVER;
                    System.out.println("💀 YOU DIED!");
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