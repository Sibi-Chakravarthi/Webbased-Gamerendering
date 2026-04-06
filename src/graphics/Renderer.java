package graphics;

import core.GameEngine;
import core.GameState;
import entities.Item;
import interfaces.IEquippable;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Renderer {
    private int width, height;
    private BufferedImage image;
    private int[] pixels;

    public Renderer(int width, int height) {
        this.width = width;
        this.height = height;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    }

    public void draw(Graphics g, GameEngine engine, int[][] currentFrame) {
        
        if (engine.currentState == GameState.LOADING) {
            fillScreenBlack();
        } else if (engine.currentState == GameState.MENU) {
            fillScreenMenu();
        } else {

            draw3DWorld(currentFrame);
            drawSprites(engine);

            if (engine.currentState == GameState.GAME_OVER || engine.currentState == GameState.VICTORY) {
                applyStateOverlay(engine.currentState);
            }
        }

        g.drawImage(image, 0, 0, null);

        if (engine.currentState == GameState.PLAYING) {
            drawMinimap(g, engine);
            drawHUD(g, engine);
        }
    }

    private void drawMinimap(Graphics g, GameEngine engine) {
        int scale = 3; 
        int[][] map = engine.worldMap;
        if (map == null) return;

        int mapWidth = map.length;
        int mapHeight = map[0].length;

        g.setColor(new Color(0, 0, 0, 200)); 
        g.fillRect(0, 0, mapHeight * scale, mapWidth * scale);

        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                if (map[x][y] > 0) {

                    if (map[x][y] == 2) g.setColor(Color.BLUE); 
                    else if (map[x][y] == 3) g.setColor(Color.GREEN); 
                    else g.setColor(Color.DARK_GRAY); 

                    int drawX = (mapHeight - 1 - y) * scale;
                    int drawY = (mapWidth - 1 - x) * scale;
                    g.fillRect(drawX, drawY, scale, scale);
                }
            }
        }

        g.setColor(new Color(255, 255, 0, 100)); 

        int px = (mapHeight - 1 - (int) engine.player.posY) * scale;
        int py = (mapWidth - 1 - (int) engine.player.posX) * scale;

        for (int i = 0; i < width; i += 30) { 
            double cameraX = 2.0 * i / width - 1;
            double rayDirX = engine.player.dirX + engine.player.planeX * cameraX;
            double rayDirY = engine.player.dirY + engine.player.planeY * cameraX;

            double rx = engine.player.posX;
            double ry = engine.player.posY;

            while (true) {
                rx += rayDirX * 0.1;
                ry += rayDirY * 0.1;
                
                if (rx < 0 || ry < 0 || rx >= mapWidth || ry >= mapHeight) break;
                if (map[(int)rx][(int)ry] > 0) break;
            }

            int drawRx = (mapHeight - 1 - (int) ry) * scale;
            int drawRy = (mapWidth - 1 - (int) rx) * scale;
            g.drawLine(px, py, drawRx, drawRy);
        }

        g.setColor(Color.RED);
        g.fillRect(px - 1, py - 1, 3, 3); 
    }

    private void drawHUD(Graphics g, GameEngine engine) {
        g.setFont(new Font("Monospaced", Font.BOLD, 36));

        g.setColor(Color.GREEN);
        g.drawString("HP: " + engine.player.health, 30, height - 80);

        IEquippable currentWeapon = engine.player.inventory[engine.player.activeSlot];
        String weaponName = (currentWeapon != null) ? currentWeapon.getClass().getSimpleName() : "UNARMED";
        
        g.setColor(Color.ORANGE);
        g.drawString("WPN: " + weaponName, 30, height - 50); 
    }

    private void fillScreenBlack() {
        for (int i = 0; i < pixels.length; i++) { pixels[i] = 0x000000; }
    }
    
    private void fillScreenMenu() {
        for (int i = 0; i < pixels.length; i++) { pixels[i] = 0x002244; }
    }

    private void draw3DWorld(int[][] currentFrame) {
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = (i < pixels.length / 2) ? 0x87CEEB : 0x555555;
        }

        if (currentFrame != null) {
            for (int x = 0; x < currentFrame.length; x++) {
                int drawStart = currentFrame[x][0];
                int drawEnd = currentFrame[x][1];
                int side = currentFrame[x][2];
                int wallType = currentFrame[x][3];

                int color = 0;
                if (wallType == 2) color = (side == 0) ? 0x0000FF : 0x00AA00;
                else if (wallType == 3) color = (side == 0) ? 0x00FF00 : 0x0000AA;
                else color = (side == 0) ? 0xCC0000 : 0x770000;

                for (int y = drawStart; y < drawEnd; y++) {
                    pixels[x + y * width] = color;
                }
            }
        }
    }

    private void drawSprites(GameEngine engine) {
        if (engine.player == null) return;

        if (engine.enemy != null) {
            drawSingleSprite(engine, engine.enemy.posX, engine.enemy.posY, 0xFF0000); 
        }

        if (engine.floorItems != null) {
            for (Item item : engine.floorItems) {
                if (!item.isCollected) {
                    int color = (item instanceof IEquippable) ? 0x00FFFF : 0x00FF00;
                    drawSingleSprite(engine, item.posX, item.posY, color); 
                }
            }
        }
    }

    private void drawSingleSprite(GameEngine engine, double entityX, double entityY, int colorHex) {
        double spriteX = entityX - engine.player.posX;
        double spriteY = entityY - engine.player.posY;

        double invDet = 1.0 / (engine.player.planeX * engine.player.dirY - engine.player.dirX * engine.player.planeY);
        double transformX = invDet * (engine.player.dirY * spriteX - engine.player.dirX * spriteY);
        double transformY = invDet * (-engine.player.planeY * spriteX + engine.player.planeX * spriteY);

        if (transformY > 0) {
            int spriteScreenX = (int) ((width / 2) * (1 + transformX / transformY));
            
            int vMoveScreen = (int)(128 / transformY); 
            int spriteHeight = Math.abs((int) (height / transformY)) / 2; 
            
            int drawStartY = Math.max(0, -spriteHeight / 2 + height / 2 + vMoveScreen);
            int drawEndY = Math.min(height - 1, spriteHeight / 2 + height / 2 + vMoveScreen);

            int spriteWidth = Math.abs((int) (height / transformY)) / 2;
            int drawStartX = Math.max(0, -spriteWidth / 2 + spriteScreenX);
            int drawEndX = Math.min(width - 1, spriteWidth / 2 + spriteScreenX);

            for (int stripe = drawStartX; stripe < drawEndX; stripe++) {
                if (transformY < engine.raycaster.zBuffer[stripe]) {
                    for (int y = drawStartY; y < drawEndY; y++) {
                        pixels[stripe + y * width] = colorHex; 
                    }
                }
            }
        }
    }

    private void applyStateOverlay(GameState state) {
        boolean isGameOver = (state == GameState.GAME_OVER);
        for (int i = 0; i < pixels.length; i++) {
            int oldPixel = pixels[i];
            int r = (oldPixel >> 16) & 0xFF;
            int g = (oldPixel >> 8) & 0xFF;
            int b = oldPixel & 0xFF;

            if (isGameOver) {
                r = (r + 255) >> 1; g >>= 1; b >>= 1; 
            } else {
                r >>= 1; g = (g + 255) >> 1; b >>= 1; 
            }
            pixels[i] = (r << 16) | (g << 8) | b;
        }
    }
}