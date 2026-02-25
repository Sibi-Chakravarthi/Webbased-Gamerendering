public class Raycaster{

    int screenwidth = 640;

    double posX = 22.0;
    double posY = 11.5;
    
    double dirX = 0.0;
    double dirY = -1.0;

    double planeX = 0.66;
    double planeY = 0.0;

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
        }
    }

    public static void main(String args[]){
        
        Raycaster engine = new Raycaster();
        engine.castRays();
    
    }
}