package core;

import entities.Enemy;
import entities.Player;
import graphics.Raycaster;
import world.MapLoader;
import java.util.ArrayList;
import java.util.Random;

public class GameEngine {

    public int[][] worldMap;
    public Player player;
    public Enemy enemy;
    public Raycaster raycaster;
    private long lastTime;
    private double pathTimer = 0;

    public GameState currentState = GameState.MENU; 

    public GameEngine() {
        this.raycaster = new Raycaster();
        this.player = new Player(1.5, 1.5, 1, 0, 0, -0.66);
        this.enemy = new Enemy(8.5, 8.5);
        this.lastTime = System.nanoTime();
    }

    public void reset() {
        currentState = GameState.LOADING;

        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("python", "RayForge-Engine/scripts/map-generator.py");
                Process p = pb.start();
                p.waitFor(); 

                worldMap = MapLoader.loadMap("RayForge-Engine/map.json");
                spawnEntities(); 

                currentState = GameState.PLAYING;
                lastTime = System.nanoTime(); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void spawnEntities() {
        ArrayList<int[]> validSpawns = new ArrayList<>();
        for (int x = 0; x < worldMap.length; x++) {
            for (int y = 0; y < worldMap[x].length; y++) {
                if (worldMap[x][y] == 0) validSpawns.add(new int[]{x, y});
            }
        }

        if (validSpawns.size() < 2) return;

        Random rand = new Random();
        int playerIndex = rand.nextInt(validSpawns.size());
        player.posX = validSpawns.get(playerIndex)[0] + 0.5;
        player.posY = validSpawns.get(playerIndex)[1] + 0.5;
        validSpawns.remove(playerIndex); 

        int enemyIndex = rand.nextInt(validSpawns.size());
        enemy.posX = validSpawns.get(enemyIndex)[0] + 0.5;
        enemy.posY = validSpawns.get(enemyIndex)[1] + 0.5;
        
        enemy.clearPath();
    }

    public int[][] tick(boolean w, boolean a, boolean s, boolean d) {
        long currentTime = System.nanoTime();
        double deltaTime = (currentTime - lastTime) / 1000000000.0;
        this.lastTime = currentTime;

        if (currentState == GameState.PLAYING) {
            player.move(w, a, s, d, worldMap, deltaTime);

            pathTimer += deltaTime;
            if (pathTimer > 0.5) {
                enemy.updatePath(worldMap, (int) player.posX, (int) player.posY);
                pathTimer = 0;
            }

            enemy.move(deltaTime);

            if (worldMap[(int) player.posX][(int) player.posY] == 3) {
                currentState = GameState.VICTORY;
                System.out.println("🎉 YOU ESCAPED!");
            }

            double distanceToPlayer = Math.sqrt(Math.pow(player.posX - enemy.posX, 2) + Math.pow(player.posY - enemy.posY, 2));
            if (distanceToPlayer < 0.8) {
                currentState = GameState.GAME_OVER;
                System.out.println("💀 YOU DIED!");
            }
        }

        if (worldMap != null) {
            return raycaster.castRays(player, worldMap);
        }
        return null;
    }
}