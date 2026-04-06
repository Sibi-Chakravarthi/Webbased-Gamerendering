package items;

import core.GameEngine;
import entities.Item;
import interfaces.IEquippable;

public class Blaster extends Item implements IEquippable {

    public Blaster(double x, double y) {
        super(x, y);
    }

    @Override
    public void fire(GameEngine engine) {
        System.out.println("🔫 PEW! Fired the Blaster!");

        if (engine.enemy != null) {

            double dx = engine.enemy.posX - engine.player.posX;
            double dy = engine.enemy.posY - engine.player.posY;
            double distance = Math.sqrt((dx * dx) + (dy * dy));

            double nx = dx / distance;
            double ny = dy / distance;

            double dotProduct = (nx * engine.player.dirX) + (ny * engine.player.dirY);

            if (distance < 8.0 && dotProduct > 0.95) {
                System.out.println("🎯 HIT! The enemy was struck!");

                engine.enemy.posX = 0.5; 
                engine.enemy.posY = 0.5;
            } else {
                System.out.println("💨 Missed!");
            }
        }
    }
}