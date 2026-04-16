package items;

import core.GameEngine;
import entities.Enemy;
import entities.Item;
import interfaces.IEquippable;

public class Blaster extends Item implements IEquippable {

    public int ammo = 50;

    public Blaster(double x, double y) {
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
        System.out.println("🔫 PEW! Fired the Blaster! Ammo left: " + ammo);

        Enemy closestEnemy = null;
        double minDistance = Double.MAX_VALUE;

        for (Enemy e : engine.enemies) {
            double dx = e.posX - engine.player.posX;
            double dy = e.posY - engine.player.posY;
            double distance = Math.sqrt((dx * dx) + (dy * dy));

            double nx = dx / distance;
            double ny = dy / distance;
            double dotProduct = (nx * engine.player.dirX) + (ny * engine.player.dirY);

            if (distance < 8.0 && dotProduct > 0.95) {
                if (distance < minDistance) {
                    minDistance = distance;
                    closestEnemy = e;
                }
            }
        }

        if (closestEnemy != null) {
            closestEnemy.health -= 50;
            System.out.println("🎯 HIT! Enemy health drops to " + closestEnemy.health);
            
            if (closestEnemy.health <= 0) {
                System.out.println("💀 Enemy Defeated!");
                engine.enemies.remove(closestEnemy);
                engine.player.killCount++;
            }
        } else {
            System.out.println("💨 Missed!");
        }
    }
}