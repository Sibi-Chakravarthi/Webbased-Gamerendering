package interfaces;

import core.GameEngine;

public interface IEquippable {
    void fire(GameEngine engine);
    int getAmmo();
    void addAmmo(int amount);
}