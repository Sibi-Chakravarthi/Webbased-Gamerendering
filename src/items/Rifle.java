package items;

import core.GameEngine;
import entities.Enemy;
import entities.Item;
import interfaces.IEquippable;

public class Rifle extends Item implements IEquippable {

    public Rifle(double x, double y) {
        super(x, y);
    }

    @Override
    public void fire(GameEngine engine) {
        System.out.println("🔫 RAT-TAT-TAT! Fired the Rifle!");

        Enemy closestEnemy = null;
        double minDistance = Double.MAX_VALUE;

        for (Enemy e : engine.enemies) {
            double dx = e.posX - engine.player.posX;
            double dy = e.posY - engine.player.posY;
            double distance = Math.sqrt((dx * dx) + (dy * dy));

            double nx = dx / distance;
            double ny = dy / distance;
            double dotProduct = (nx * engine.player.dirX) + (ny * engine.player.dirY);

            // Long range, narrow cone
            if (distance < 20.0 && dotProduct > 0.98) {
                if (distance < minDistance) {
                    minDistance = distance;
                    closestEnemy = e;
                }
            }
        }

        if (closestEnemy != null) {
            closestEnemy.health -= 25; // Lower damage per shot, but high fire rate
            System.out.println("🎯 HIT! Enemy health drops to " + closestEnemy.health);
            
            if (closestEnemy.health <= 0) {
                System.out.println("💀 Enemy Defeated!");
                engine.enemies.remove(closestEnemy);
            }
        } else {
            System.out.println("💨 Missed!");
        }
    }
}
