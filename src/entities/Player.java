package entities;

import interfaces.IEquippable;

public class Player extends Entity {

    public double dirX;
    public double dirY;
    public double planeX;
    public double planeY;

    public boolean isGhostMode = false; 

    public int health = 100;
    public IEquippable[] inventory = new IEquippable[3]; 
    public int activeSlot = 0;

    public Player(double startX, double startY, double dirX, double dirY, double planeX, double planeY) {
        super(startX, startY);
        this.dirX = dirX;
        this.dirY = dirY;
        this.planeX = planeX;
        this.planeY = planeY;
    }

    public void move(boolean w, boolean a, boolean s, boolean d, int[][] worldMap, double deltaTime) {
        double moveSpeed = 5.0 * deltaTime;
        double rotSpeed = 3.0 * deltaTime;

        if (w) {
            if (isWalkable(worldMap, posX + dirX * moveSpeed, posY)) posX += dirX * moveSpeed;
            if (isWalkable(worldMap, posX, posY + dirY * moveSpeed)) posY += dirY * moveSpeed;
        }
        if (s) {
            if (isWalkable(worldMap, posX - dirX * moveSpeed, posY)) posX -= dirX * moveSpeed;
            if (isWalkable(worldMap, posX, posY - dirY * moveSpeed)) posY -= dirY * moveSpeed;
        }
        if (d) {
            double oldDirX = dirX;
            dirX = dirX * Math.cos(-rotSpeed) - dirY * Math.sin(-rotSpeed);
            dirY = oldDirX * Math.sin(-rotSpeed) + dirY * Math.cos(-rotSpeed);
            double oldPlaneX = planeX;
            planeX = planeX * Math.cos(-rotSpeed) - planeY * Math.sin(-rotSpeed);
            planeY = oldPlaneX * Math.sin(-rotSpeed) + planeY * Math.cos(-rotSpeed);
        }
        if (a) {
            double oldDirX = dirX;
            dirX = dirX * Math.cos(rotSpeed) - dirY * Math.sin(rotSpeed);
            dirY = oldDirX * Math.sin(rotSpeed) + dirY * Math.cos(rotSpeed);
            double oldPlaneX = planeX;
            planeX = planeX * Math.cos(rotSpeed) - planeY * Math.sin(rotSpeed);
            planeY = oldPlaneX * Math.sin(rotSpeed) + planeY * Math.cos(rotSpeed);
        }
    }

    public boolean pickupWeapon(IEquippable weapon) {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) { 
                inventory[i] = weapon;
                System.out.println("🎒 Picked up weapon into Slot " + (i + 1));
                return true; 
            }
        }
        System.out.println("⚠️ Inventory Full! Left weapon on the floor.");
        return false; 
    }

    @Override
    protected boolean isWalkable(int[][] worldMap, double targetX, double targetY) {
        int gridX = (int) targetX;
        int gridY = (int) targetY;
        
        if (gridX < 0 || gridX >= worldMap.length || gridY < 0 || gridY >= worldMap[0].length) {
            return false;
        }
        
        if (isGhostMode) {
            return true; 
        }
        
        return worldMap[gridX][gridY] == 0 || worldMap[gridX][gridY] == 3;
    }
}