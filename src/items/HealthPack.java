package items;

import entities.Item;
import entities.Player;
import interfaces.IConsumable;

public class HealthPack extends Item implements IConsumable {
    private int healAmount = 50;

    public HealthPack(double x, double y) {
        super(x, y);
    }

    @Override
    public void consume(Player player) {
        player.health += healAmount;
        if (player.health > 100) player.health = 100; // Cap at 100 max HP
        
        this.isCollected = true; // Flag it so the engine deletes it
        System.out.println("🩹 Picked up Health Pack! Health is now: " + player.health);
    }
}