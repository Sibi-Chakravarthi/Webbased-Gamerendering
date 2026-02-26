public class Raycaster{

    int[][] worldMap = {
        {1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,1},
        {1,0,0,1,1,0,0,0,0,1},
        {1,0,0,1,0,0,0,0,0,1},
        {1,0,0,0,0,0,1,0,0,1},
        {1,0,0,0,0,0,1,0,0,1},
        {1,0,0,0,0,0,0,0,0,1},
        {1,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1}
    };

    int screenHeight = 480;
    int screenwidth = 640;

    double posX = 4.5;
    double posY = 4.5;
    
    double dirX = -1.0;
    double dirY = 0.0;

    double planeX = 0.0;
    double planeY = 0.66;

    double sideDistX;
    double sideDistY;
    
    int stepX;
    int stepY;

    public void castRays(){

        for (int x = 0; x < screenwidth ; x++){
        
            double cameraX = (2.0 * x /screenwidth) - 1;
            
            double rayDirX = dirX + planeX * cameraX;
            double rayDirY = dirY + planeY * cameraX;
            
            int mapX = (int) posX;
            int mapY = (int) posY;

            double deltaDistX = Math.abs(1/rayDirX);
            double deltaDistY = Math.abs(1/rayDirY);

            if (rayDirX < 0){
                stepX = -1;
                sideDistX = (posX - mapX) * deltaDistX;
            } else {
                stepX = 1;
                sideDistX = (mapX + 1 - posX) * deltaDistX;
            }

            if (rayDirY < 0){
                stepY = -1;
                sideDistY = (posY - mapY) * deltaDistY;
            } else {
                stepY = 1;
                sideDistY = (mapY + 1 - posY) * deltaDistY;
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

                if (worldMap[mapX][mapY] > 0) {
                    hit = 1;
                }
            }
                        
            double perpWallDist;

            if (side == 0){

                perpWallDist = (mapX - posX + (1 - stepX) / 2.0) / rayDirX;

            } else {

                perpWallDist = (mapY - posY + (1 - stepY) / 2.0) / rayDirY;

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

            if (x == 320) {
                System.out.println("Ray 320 | Dist: " + perpWallDist + " | Draw Wall from Y: " + drawStart + " to Y: " + drawEnd);
            }
        }
    }

    public static void main(String args[]){
        
        Raycaster engine = new Raycaster();
        engine.castRays();
    
    }

}