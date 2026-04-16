package items;

import entities.Item;
import entities.Player;
import interfaces.IConsumable;
import interfaces.IEquippable;

public class HealthPack extends Item implements IConsumable {
    private int healAmount = 50;

    public HealthPack(double x, double y) {
        super(x, y);
    }

    @Override
    public void consume(Player player) {
        player.health += healAmount;
        if (player.health > 100) player.health = 100; 
        
        for (IEquippable weapon : player.inventory) {
            if (weapon != null) {
                weapon.addAmmo(50);
            }
        }
        
        this.isCollected = true;
        System.out.println("🩹 Picked up Health Pack & Ammo! Health is now: " + player.health);
    }
}