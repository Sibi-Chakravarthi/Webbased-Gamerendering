public class GameEngine {

    public int[][] worldMap;
    public Player player;
    public Enemy enemy;
    public Raycaster raycaster;
    public double deltaTime;
    private long lastTime;

    private double pathTimer = 0;

    public GameEngine() {
        
        generateNewMap();

        this.worldMap = MapLoader.loadMap("map.json");

        if (this.worldMap == null) {
            System.out.println("⚠️ WARNING: map.json not found! Loading emergency fallback room...");
            this.worldMap = new int[][] {
                {1, 1, 1, 1, 1},
                {1, 2, 0, 0, 1},
                {1, 0, 0, 0, 1},
                {1, 0, 0, 3, 1},
                {1, 1, 1, 1, 1}
            };
        }

        double spawnX = 2.5; 
        double spawnY = 2.5;
        double enemyX = 2.5;
        double enemyY = 2.5;

        for (int x = 0; x < worldMap.length; x++) {
            for (int y = 0; y < worldMap[x].length; y++) {
                if (worldMap[x][y] == 2) {
                    spawnX = x + 1.5; 
                    spawnY = y + 0.5;
                }
                if (worldMap[x][y] == 3) {
                    enemyX = x + 0.5;
                    enemyY = y + 0.5;
                }
            }
        }

        this.player = new Player(spawnX, spawnY, 1, 0, 0, -0.66);
        this.enemy = new Enemy(enemyX, enemyY);
        
        this.raycaster = new Raycaster();
        this.lastTime = System.nanoTime();
    }

    private void generateNewMap() {
        System.out.println("🐍 Firing up the Python Map Generator...");
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "scripts/map-generator.py");
            
            pb.inheritIO(); 
            
            Process process = pb.start();

            process.waitFor(); 
            
            System.out.println("✅ Python script finished execution.");

        } catch (Exception e) {
            System.out.println("❌ Failed to execute the Python script!");
            e.printStackTrace();
        }
    }

    public int[][] tick(boolean w, boolean a, boolean s, boolean d) {

        long currentTime = System.nanoTime();
        double deltaTime = (currentTime - lastTime) / 1000000000.0;
        this.lastTime = currentTime;

        player.move(w, a, s, d, worldMap, deltaTime);

        pathTimer += deltaTime;
        if (pathTimer > 0.5) {
            enemy.updatePath(worldMap, (int) player.posX, (int) player.posY);
            pathTimer = 0;
        }

        enemy.move(deltaTime);

        double distanceToPlayer = Math.sqrt(Math.pow(player.posX - enemy.posX, 2) + Math.pow(player.posY - enemy.posY, 2));
        System.out.println("Enemy Distance: " + String.format("%.2f", distanceToPlayer) + " blocks");
        
        if (distanceToPlayer < 0.8) {
            System.out.println("💀 YOU DIED! The enemy caught you!");
        }

        return raycaster.castRays(player, worldMap);
    }
}