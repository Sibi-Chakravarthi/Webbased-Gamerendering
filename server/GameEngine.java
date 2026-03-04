public class GameEngine {

    public int[][] worldMap;
    public Player player;
    public Raycaster raycaster;

    public GameEngine() {
        
        generateNewMap();

        this.worldMap = MapLoader.loadMap("map.json");

        if (this.worldMap == null) {
            System.out.println("⚠️ WARNING: map.json not found! Loading emergency fallback room...");
            this.worldMap = new int[][] {
                {1, 1, 1, 1, 1},
                {1, 0, 0, 0, 1},
                {1, 0, 0, 0, 1},
                {1, 0, 0, 0, 1},
                {1, 1, 1, 1, 1}
            };
        }

        double spawnX = 2.5; 
        double spawnY = 2.5;
        boolean foundSpawn = false;

        for (int x = 1; x < worldMap.length - 1; x++) {
            for (int y = 1; y < worldMap[x].length - 1; y++) {
                if (worldMap[x][y] == 0) {
                    spawnX = x + 0.5; 
                    spawnY = y + 0.5;
                    foundSpawn = true;
                    break;
                }
            }
            if (foundSpawn) break;
        }

        this.player = new Player(spawnX, spawnY, -1, 0, 0, 0.66);
        this.raycaster = new Raycaster();
    }

    private void generateNewMap() {
        System.out.println("🐍 Firing up the Python Map Generator...");
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "generator/map-generator.py");
            
            pb.inheritIO(); 
            
            Process process = pb.start();

            process.waitFor(); 
            
            System.out.println("✅ Python script finished execution.");

        } catch (Exception e) {
            System.out.println("❌ Failed to execute the Python script!");
            e.printStackTrace();
        }
    }

    public String tick(boolean w, boolean a, boolean s, boolean d) {

        player.move(w, a, s, d, worldMap);

        int[][] frameData = raycaster.castRays(player, worldMap);

        return raycaster.toJSON(frameData);
    }
}