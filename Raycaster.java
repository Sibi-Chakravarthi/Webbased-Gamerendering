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

    double sideDistX;
    double sideDistY;
    
    int stepX;
    int stepY;

    public Player player;

    public Raycaster() {
        this.player = new Player(2.5, 2.5, -1, 0, 0, 0.66);
    }

    public int[][] castRays(){

        int[][] rayData = new int[screenwidth][3]; 

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

                if (worldMap[mapX][mapY] > 0) {
                    hit = 1;
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
        }
        return rayData;
    }

    public String toJSON(int[][] data){

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (int i = 0;i < data.length;i++){

            sb.append("[")
              .append(data[i][0]).append(",")
              .append(data[i][1]).append(",")
              .append(data[i][2])
              .append("]");

            if (i < data.length - 1){
                sb.append(",");
            }
        }
        
        sb.append(']');

        return sb.toString();
    }
}