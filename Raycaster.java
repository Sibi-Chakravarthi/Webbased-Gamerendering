public class Raycaster{

    int screenwidth = 640;
    
    double dirX = 0.0;
    double dirY = -1.0;

    double planeX = 0.66;
    double planeY = 0.0;

    public void castRays(){
        for (int x = 0; x < screenwidth ; x++){
            double cameraX = (2.0 * x /screenwidth) - 1;
            
            double rayDirX = dirX + planeX * cameraX;
            double rayDirY = dirY + planeY * cameraX;
            System.out.println("Ray " + x + ": [" + rayDirX + ", " + rayDirY + "]");
        }
    }

    public static void main(String args[]){
        Raycaster engine = new Raycaster();
        engine.castRays();
    }
}