package entities;

public abstract class Item extends Entity {
    public boolean isCollected = false; 

    public Item(double x, double y) {
        super(x, y);
    }
}