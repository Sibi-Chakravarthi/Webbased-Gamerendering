public class Raycaster{

    int[][] worldMap;

    public int screenHeight = 1080;
    public int screenwidth = 1920;

    double sideDistX;
    double sideDistY;
    
    int stepX;
    int stepY;

    public Player player;

    public Raycaster() {

        this.worldMap = MapLoader.loadMap("map.json");

        this.player = new Player(50.5, 50.5, -1, 0, 0, 0.66);

    }

    public int[][] castRays(Player player, int[][] worldMap){

        int[][] rayData = new int[screenwidth][4]; 

        for (int x = 0; x < screenwidth ; x++){
        
            double cameraX = (2.0 * x /screenwidth) - 1;
            
            double rayDirX = player.dirX + player.planeX * cameraX;
            double rayDirY = player.dirY + player.planeY * cameraX;
            
            int mapX = (int) player.posX;
            int mapY = (int) player.posY;

            double deltaDistX = Math.abs(1/rayDirX);
            double deltaDistY = Math.abs(1/rayDirY);

            if (rayDirX < 0){
                stepX = -1;
                sideDistX = (player.posX - mapX) * deltaDistX;
            } else {
                stepX = 1;
                sideDistX = (mapX + 1 - player.posX) * deltaDistX;
            }

            if (rayDirY < 0){
                stepY = -1;
                sideDistY = (player.posY - mapY) * deltaDistY;
            } else {
                stepY = 1;
                sideDistY = (mapY + 1 - player.posY) * deltaDistY;
            }

            int hit = 0;
            int side = 0;

            while (hit == 0){

                if (sideDistX < sideDistY){

                    sideDistX = sideDistX + deltaDistX;
                    mapX += stepX;
                    side = 0;

                } else {
                    sideDistY = sideDistY + deltaDistY;
                    mapY += stepY;
                    side = 1;
                }

                if (mapX < 0 || mapX >= worldMap.length || mapY < 0 || mapY >= worldMap[0].length) {
                    hit = 1;
                    break;
                }

                if (worldMap[mapX][mapY] > 0) {
                    hit = worldMap[mapX][mapY];
                }
            }
                        
            double perpWallDist;

            if (side == 0){

                perpWallDist = (mapX - player.posX + (1 - stepX) / 2.0) / rayDirX;

            } else {

                perpWallDist = (mapY - player.posY + (1 - stepY) / 2.0) / rayDirY;

            }

            double lineHeight = screenHeight / perpWallDist;

            int drawStart = (int) -lineHeight / 2 + screenHeight / 2;
            
            if (drawStart < 0) {
                drawStart = 0;
            }

            int drawEnd = (int) lineHeight / 2 + screenHeight / 2;
            
            if (drawEnd >= screenHeight) {
                drawEnd = screenHeight - 1;
            }
            rayData[x][0] = drawStart;
            rayData[x][1] = drawEnd;
            rayData[x][2] = side;
            rayData[x][3] = hit;
        }
        return rayData;
    }

}