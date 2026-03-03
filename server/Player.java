public class Player{
    
    public double posX;
    public double posY;
    public double dirX;
    public double dirY;
    public double planeX;
    public double planeY;

    private double moveSpeed = 0.08; 
    private double rotSpeed = 0.05;

    public Player(double startX ,double startY, double startDirX,double startDirY,double startPlaneX,double startPlaneY){

        this.posX = startX;
        this.posY = startY;
        this.dirX = startDirX;
        this.dirY = startDirY;
        this.planeX = startPlaneX;
        this.planeY = startPlaneY;

    }

    public void move(boolean w,boolean a,boolean s,boolean d,int[][] worldMap){

        double radius = 0.25;

        if (w) {

            if (worldMap[(int)(posX + dirX * moveSpeed + Math.signum(dirX) * radius)][(int)posY] == 0) {
                posX += dirX * moveSpeed;
            }

            if (worldMap[(int)posX][(int)(posY + dirY * moveSpeed + Math.signum(dirY) * radius)] == 0) {
                posY += dirY * moveSpeed;
            }
        }

        if (s) {

            if (worldMap[(int)(posX - dirX * moveSpeed - Math.signum(dirX) * radius)][(int)posY] == 0) {
                posX -= dirX * moveSpeed;
            }

            if (worldMap[(int)posX][(int)(posY - dirY * moveSpeed - Math.signum(dirY) * radius)] == 0) {
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
