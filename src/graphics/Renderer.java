package graphics;

import core.GameEngine;
import core.GameState;
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
            for (entities.Item item : engine.floorItems) {
                if (!item.isCollected) {
                    drawSingleSprite(engine, item.posX, item.posY, 0x00FF00);
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