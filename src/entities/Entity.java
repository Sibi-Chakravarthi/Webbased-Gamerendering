package entities;

public abstract class Entity {

    public double posX;
    public double posY;

    public Entity(double startX, double startY) {
        this.posX = startX;
        this.posY = startY;
    }

    protected boolean isWalkable(int[][] worldMap, double targetX, double targetY) {
        int gridX = (int) targetX;
        int gridY = (int) targetY;
        
        if (gridX < 0 || gridX >= worldMap.length || gridY < 0 || gridY >= worldMap[0].length) {
            return false;
        }
        
        return worldMap[gridX][gridY] == 0;
    }
}