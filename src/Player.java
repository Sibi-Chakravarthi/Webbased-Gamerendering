public class Player extends Entity{
    
    public double dirX;
    public double dirY;
    public double planeX;
    public double planeY;

    private double baseMoveSpeed = 5.0; 
    private double baseRotSpeed = 3.0;

    public Player(double startX, double startY, double startDirX, double startDirY, double startPlaneX, double startPlaneY) {
        super(startX, startY); // This handles the coordinates now!
        this.dirX = startDirX;
        this.dirY = startDirY;
        this.planeX = startPlaneX;
        this.planeY = startPlaneY;
    }

    public void move(boolean w,boolean a,boolean s,boolean d,int[][] worldMap , double deltaTime){

        double moveSpeed = baseMoveSpeed * deltaTime;
        double rotSpeed = baseRotSpeed * deltaTime;
        
        double buffer = 0.3;

        if (w) {
            // We check slightly ahead of where we want to step
            if (isWalkable(worldMap, posX + dirX * (moveSpeed + buffer), posY)) {
                posX += dirX * moveSpeed;
            }
            if (isWalkable(worldMap, posX, posY + dirY * (moveSpeed + buffer))) {
                posY += dirY * moveSpeed;
            }
        }

        if (s) {
            if (isWalkable(worldMap, posX - dirX * (moveSpeed + buffer), posY)) {
                posX -= dirX * moveSpeed;
            }
            if (isWalkable(worldMap, posX, posY - dirY * (moveSpeed + buffer))) {
                posY -= dirY * moveSpeed;
            }
        }

        // ... (Keep your exact same 'a' and 'd' rotation logic here!) ...
        if (a) {
            double oldDirX = dirX;
            dirX = oldDirX * Math.cos(rotSpeed) - dirY * Math.sin(rotSpeed);
            dirY = oldDirX * Math.sin(rotSpeed) + dirY * Math.cos(rotSpeed);
            double oldPlaneX = planeX;
            planeX = oldPlaneX * Math.cos(rotSpeed) - planeY * Math.sin(rotSpeed);
            planeY = oldPlaneX * Math.sin(rotSpeed) + planeY * Math.cos(rotSpeed);
        }

        if (d) {
            double oldDirX = dirX;
            dirX = oldDirX * Math.cos(-rotSpeed) - dirY * Math.sin(-rotSpeed);
            dirY = oldDirX * Math.sin(-rotSpeed) + dirY * Math.cos(-rotSpeed);
            double oldPlaneX = planeX;
            planeX = oldPlaneX * Math.cos(-rotSpeed) - planeY * Math.sin(-rotSpeed);
            planeY = oldPlaneX * Math.sin(-rotSpeed) + planeY * Math.cos(-rotSpeed);
        }
    }
}