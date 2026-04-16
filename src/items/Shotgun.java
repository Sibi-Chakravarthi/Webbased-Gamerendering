package items;

import core.GameEngine;
import entities.Enemy;
import entities.Item;
import interfaces.IEquippable;

public class Shotgun extends Item implements IEquippable {

    public int ammo = 15;

    public Shotgun(double x, double y) {
        super(x, y);
    }

    @Override
    public int getAmmo() {
        return ammo;
    }

    @Override
    public void addAmmo(int amount) {
        this.ammo += amount;
    }

    @Override
    public void fire(GameEngine engine) {
        if (ammo <= 0) return;
        ammo--;
        System.out.println("💥 BOOM! Fired the Shotgun! Ammo left: " + ammo);

        boolean hitSomething = false;

        // Shotgun hits all enemies within a wide short cone (scatter shot)
        for (int i = engine.enemies.size() - 1; i >= 0; i--) {
            Enemy e = engine.enemies.get(i);
            
            double dx = e.posX - engine.player.posX;
            double dy = e.posY - engine.player.posY;
            double distance = Math.sqrt((dx * dx) + (dy * dy));

            double nx = dx / distance;
            double ny = dy / distance;
            double dotProduct = (nx * engine.player.dirX) + (ny * engine.player.dirY);

            // Shorter range but much wider cone
            if (distance < 5.0 && dotProduct > 0.85) {
                e.health -= 100;
                hitSomething = true;
                System.out.println("🎯 BLAST! Enemy health drops to " + e.health);
                
                if (e.health <= 0) {
                    System.out.println("💀 Enemy Obliterated!");
                    engine.enemies.remove(i);
                    engine.player.killCount++;
                }
            }
        }

        if (!hitSomething) {
            System.out.println("💨 Missed!");
        }
    }
}
