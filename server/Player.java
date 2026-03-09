public class Player{
    
    public double posX;
    public double posY;
    public double dirX;
    public double dirY;
    public double planeX;
    public double planeY;

    private double baseMoveSpeed = 5.0; 
    private double baseRotSpeed = 3.0;

    public Player(double startX ,double startY, double startDirX,double startDirY,double startPlaneX,double startPlaneY){

        this.posX = startX;
        this.posY = startY;
        this.dirX = startDirX;
        this.dirY = startDirY;
        this.planeX = startPlaneX;
        this.planeY = startPlaneY;

    }

    private boolean isWalkable(int[][] worldMap, double targetX, double targetY) {
        int gridX = (int) targetX;
        int gridY = (int) targetY;
        
        if (gridX < 0 || gridX >= worldMap.length || gridY < 0 || gridY >= worldMap[0].length) {
            return false;
        }
        
        return worldMap[gridX][gridY] == 0;
    }

    public void move(boolean w,boolean a,boolean s,boolean d,int[][] worldMap , double deltaTime){

        double moveSpeed = baseMoveSpeed * deltaTime;
        double rotSpeed = baseRotSpeed * deltaTime;
        
        double radius = 0.25;

        if (w) {
            if (isWalkable(worldMap, posX + dirX * moveSpeed + Math.signum(dirX) * radius, posY)) {
                posX += dirX * moveSpeed;
            }
            if (isWalkable(worldMap, posX, posY + dirY * moveSpeed + Math.signum(dirY) * radius)) {
                posY += dirY * moveSpeed;
            }
        }

        if (s) {
            if (isWalkable(worldMap, posX - dirX * moveSpeed - Math.signum(dirX) * radius, posY)) {
                posX -= dirX * moveSpeed;
            }
            if (isWalkable(worldMap, posX, posY - dirY * moveSpeed - Math.signum(dirY) * radius)) {
                posY -= dirY * moveSpeed;
            }
        }

        if (a){

            double oldDirX = dirX;
            dirX = oldDirX * Math.cos(rotSpeed) - dirY * Math.sin(rotSpeed);
            dirY = oldDirX * Math.sin(rotSpeed) + dirY * Math.cos(rotSpeed);
            
            double oldPlaneX = planeX;
            planeX = oldPlaneX * Math.cos(rotSpeed) - planeY * Math.sin(rotSpeed);
            planeY = oldPlaneX * Math.sin(rotSpeed) + planeY * Math.cos(rotSpeed);

        }

        if (d){

            double oldDirX = dirX;
            dirX = oldDirX * Math.cos(-rotSpeed) - dirY * Math.sin(-rotSpeed);
            dirY = oldDirX * Math.sin(-rotSpeed) + dirY * Math.cos(-rotSpeed);
            
            double oldPlaneX = planeX;
            planeX = oldPlaneX * Math.cos(-rotSpeed) - planeY * Math.sin(-rotSpeed);
            planeY = oldPlaneX * Math.sin(-rotSpeed) + planeY * Math.cos(-rotSpeed);
        }

        }
    }
