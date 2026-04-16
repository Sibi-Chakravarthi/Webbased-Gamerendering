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
import javax.imageio.ImageIO;
import java.io.File;

public class Renderer {
    private int width, height;
    private BufferedImage image;
    private int[] pixels;
    private BufferedImage gunSprite;
    private BufferedImage shotgunSprite;
    private BufferedImage rifleSprite;
    private Texture enemySprite;
    private Texture wallTexture;
    private Texture startTexture;
    private Texture exitTexture;
    private Texture skyTexture;
    private Texture floorTexture;
    private Texture gunPickupTexture;
    private Texture shotgunPickupTexture;
    private Texture riflePickupTexture;
    private Texture healthPickupTexture;

    public Renderer(int width, int height) {
        this.width = width;
        this.height = height;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        try {
            gunSprite = ImageIO.read(new File("res/textures/gun.png"));
        } catch (Exception e) {
            System.out.println("⚠️ Could not find res/textures/gun.png!");
        }
        try {
            shotgunSprite = ImageIO.read(new File("res/textures/shotgun.png"));
        } catch (Exception e) {
            System.out.println("⚠️ Could not find res/textures/shotgun.png!");
        }
        try {
            rifleSprite = ImageIO.read(new File("res/textures/rifle.png"));
        } catch (Exception e) {
            System.out.println("⚠️ Could not find res/textures/rifle.png!");
        }
        try {
            enemySprite = new Texture("res/textures/enemy.png");
        } catch (Exception e) {
            System.out.println("⚠️ Could not find res/textures/enemy.png!");
        }
        try {
            wallTexture = new Texture("res/textures/wall.png");
        } catch (Exception e) {
            System.out.println("⚠️ Could not find res/textures/wall.png!");
        }
        try {
            startTexture = new Texture("res/textures/start.png");
        } catch (Exception e) {
            System.out.println("⚠️ Could not find res/textures/start.png!");
        }
        try {
            exitTexture = new Texture("res/textures/exit.png");
        } catch (Exception e) {
            System.out.println("⚠️ Could not find res/textures/exit.png!");
        }
        try {
            skyTexture = new Texture("res/textures/sky.png");
        } catch (Exception e) {
            System.out.println("⚠️ Could not find res/textures/sky.png!");
        }
        try {
            floorTexture = new Texture("res/textures/floor.png");
        } catch (Exception e) {
            System.out.println("⚠️ Could not find res/textures/floor.png!");
        }
        try {
            gunPickupTexture = new Texture("res/textures/gun_pickup.png");
        } catch (Exception e) {
            System.out.println("⚠️ Could not find res/textures/gun_pickup.png!");
        }
        try {
            shotgunPickupTexture = new Texture("res/textures/shotgun_pickup.png");
        } catch (Exception e) {
            System.out.println("⚠️ Could not find res/textures/shotgun_pickup.png!");
        }
        try {
            riflePickupTexture = new Texture("res/textures/rifle_pickup.png");
        } catch (Exception e) {
            System.out.println("⚠️ Could not find res/textures/rifle_pickup.png!");
        }
        try {
            healthPickupTexture = new Texture("res/textures/health_pickup.png");
        } catch (Exception e) {
            System.out.println("⚠️ Could not find res/textures/health_pickup.png!");
        }
    }

    public void draw(Graphics g, GameEngine engine, int[][] currentFrame) {
        
        if (engine.currentState == GameState.LOADING) {
            fillScreenBlack();
        } else if (engine.currentState == GameState.MENU) {
            fillScreenMenu();
            g.drawImage(image, 0, 0, null);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.BOLD, 72));
            g.drawString("RAYFORGE ENGINE", width / 2 - 300, height / 2 - 50);
            g.setFont(new Font("Monospaced", Font.BOLD, 36));
            g.drawString("Press ENTER to Start", width / 2 - 220, height / 2 + 50);
            return;
        } else {

            drawFloorAndSky(engine);
            draw3DWorld(currentFrame, engine.raycaster.wallXBuffer, engine.raycaster.zBuffer);
            drawSprites(engine);

            if (engine.currentState == GameState.GAME_OVER || engine.currentState == GameState.VICTORY) {
                applyStateOverlay(engine.currentState);
            }
        }

        g.drawImage(image, 0, 0, null);

        if (engine.currentState == GameState.GAME_OVER) {
            g.setColor(Color.RED);
            g.setFont(new Font("Monospaced", Font.BOLD, 72));
            g.drawString("YOU DIED", width / 2 - 150, height / 2);
            g.setFont(new Font("Monospaced", Font.BOLD, 36));
            g.drawString("Press ENTER to Restart", width / 2 - 230, height / 2 + 50);
        } else if (engine.currentState == GameState.VICTORY) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Monospaced", Font.BOLD, 72));
            g.drawString("LEVEL COMPLETE", width / 2 - 300, height / 2);
            g.setFont(new Font("Monospaced", Font.BOLD, 36));
            g.drawString("Press ENTER to Continue", width / 2 - 230, height / 2 + 50);
        }

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

        g.setColor(Color.WHITE);
        g.drawString("WAVE " + engine.currentLevel, width / 2 - 60, 50);
        g.drawString("KILLS: " + engine.player.killCount, width - 350, 50);

        g.setColor(Color.GREEN);
        g.drawString("HP: " + engine.player.health, 30, height - 80);

        IEquippable currentWeapon = engine.player.inventory[engine.player.activeSlot];
        String weaponName = (currentWeapon != null) ? currentWeapon.getClass().getSimpleName() : "UNARMED";
        String ammoText = (currentWeapon != null) ? String.valueOf(currentWeapon.getAmmo()) : "0";
        
        g.setColor(Color.ORANGE);
        g.drawString("WPN: " + weaponName + " | AMMO: " + ammoText, 30, height - 50); 

        BufferedImage currentHUD = null;
        if (currentWeapon instanceof items.Shotgun) currentHUD = shotgunSprite;
        else if (currentWeapon instanceof items.Rifle) currentHUD = rifleSprite;
        else if (currentWeapon instanceof items.Blaster) currentHUD = gunSprite;

        if (currentWeapon != null && currentHUD != null) {

            double maxCooldown = 0.6;
            if (currentWeapon instanceof items.Shotgun) maxCooldown = 1.0;
            else if (currentWeapon instanceof items.Rifle) maxCooldown = 0.15;

            int recoilOffset = (engine.player.weaponCooldown > 0) ? (int)((engine.player.weaponCooldown / maxCooldown) * 80) : 0;
            
            int desiredHeight = height / 2;
            double scaleRatio = (double) desiredHeight / currentHUD.getHeight();
            
            int scaledWidth = (int) (currentHUD.getWidth() * scaleRatio);
            int scaledHeight = (int) (currentHUD.getHeight() * scaleRatio);
            
            int drawX = (width / 2) - (scaledWidth / 2) + 250; 

            int drawY = height - scaledHeight - recoilOffset + 40; 
            
            g.drawImage(currentHUD, drawX, drawY, scaledWidth, scaledHeight, null);

            if (engine.player.weaponCooldown > maxCooldown * 0.6) { 
                g.setColor(new Color(255, 255, 150, 100)); 
                g.fillRect(0, 0, width, height);
            }
        }
    }
    
    private void fillScreenBlack() {
        for (int i = 0; i < pixels.length; i++) { pixels[i] = 0x000000; }
    }
    
    private void fillScreenMenu() {
        for (int i = 0; i < pixels.length; i++) { pixels[i] = 0x002244; }
    }

    private void draw3DWorld(int[][] currentFrame, double[] wallXBuffer, double[] zBuffer) {

        if (currentFrame != null) {
            for (int x = 0; x < currentFrame.length; x++) {
                int drawStart = currentFrame[x][0];
                int drawEnd = currentFrame[x][1];
                int side = currentFrame[x][2];
                int wallType = currentFrame[x][3];

                if (wallType > 0) {
                    Texture tex = null;
                    if (wallType == 2 && startTexture != null) tex = startTexture;
                    else if (wallType == 3 && exitTexture != null) tex = exitTexture;
                    else if (wallType == 1 && wallTexture != null) tex = wallTexture;

                    if (tex != null) {

                    double wallX = wallXBuffer[x];
                    int texX = (int)(wallX * tex.width);
                    if (texX >= tex.width) texX = tex.width - 1;

                    double lineHeight = (double) height / zBuffer[x];

                    double step = (double) tex.height / lineHeight;

                    double texPos = (drawStart - (-lineHeight / 2.0 + height / 2.0)) * step;

                    for (int y = drawStart; y < drawEnd; y++) {
                        int texY = (int) texPos;
                        if (texY < 0) texY = 0;
                        if (texY >= tex.height) texY = tex.height - 1;
                        texPos += step;

                        int color = tex.pixels[texX + texY * tex.width];

                        if (side == 1) {
                            int r = ((color >> 16) & 0xFF) >> 1;
                            int g = ((color >> 8) & 0xFF) >> 1;
                            int b = (color & 0xFF) >> 1;
                            color = (r << 16) | (g << 8) | b;
                        }

                        pixels[x + y * width] = color;
                    }
                } else {

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
        }
    }

    private void drawSprites(GameEngine engine) {
        if (engine.player == null) return;

        if (engine.enemies != null) {
            for (entities.Enemy e : engine.enemies) {

                drawSingleSprite(engine, e.posX, e.posY, 0, enemySprite, e.currentFrame, 4, 1.0); 
            }
        }

        if (engine.floorItems != null) {
            for (entities.Item item : engine.floorItems) {
                if (!item.isCollected) {
                    Texture tex = null;
                    if (item instanceof items.Blaster) {
                        tex = gunPickupTexture;
                    } else if (item instanceof items.Shotgun) {
                        tex = shotgunPickupTexture;
                    } else if (item instanceof items.Rifle) {
                        tex = riflePickupTexture;
                    } else if (item instanceof items.HealthPack) {
                        tex = healthPickupTexture;
                    }
                    
                    int color = 0;
                    if (tex == null) {
                        color = (item instanceof interfaces.IEquippable) ? 0x00FFFF : 0x00FF00;
                    }

                    drawSingleSprite(engine, item.posX, item.posY, color, tex, 0, 1, 0.4); 
                }
            }
        }
    }

    private void drawSingleSprite(GameEngine engine, double entityX, double entityY, int colorHex, Texture tex, int frame, int totalFrames, double scale) {
        double spriteX = entityX - engine.player.posX;
        double spriteY = entityY - engine.player.posY;

        double invDet = 1.0 / (engine.player.planeX * engine.player.dirY - engine.player.dirX * engine.player.planeY);
        double transformX = invDet * (engine.player.dirY * spriteX - engine.player.dirX * spriteY);
        double transformY = invDet * (-engine.player.planeY * spriteX + engine.player.planeX * spriteY);

        if (transformY > 0) {
            int spriteScreenX = (int) ((width / 2) * (1 + transformX / transformY));
            int vMove = (int)(height * (1.0 - scale) / 2.0 + 128);
            int vMoveScreen = (int)(vMove / transformY); 
            
            int spriteHeight = Math.abs((int) ((height / transformY) * scale)); 
            int drawStartY = Math.max(0, -spriteHeight / 2 + height / 2 + vMoveScreen);
            int drawEndY = Math.min(height - 1, spriteHeight / 2 + height / 2 + vMoveScreen);

            int spriteWidth = Math.abs((int) ((height / transformY) * scale));
            int drawStartX = Math.max(0, -spriteWidth / 2 + spriteScreenX);
            int drawEndX = Math.min(width - 1, spriteWidth / 2 + spriteScreenX);

            int spriteLeft = -spriteWidth / 2 + spriteScreenX;

            for (int stripe = drawStartX; stripe < drawEndX; stripe++) {
                if (transformY < engine.raycaster.zBuffer[stripe]) {
                    
                    int texX = 0;
                    int frameWidth = 0;
                    
                    if (tex != null) {
                        frameWidth = tex.width / totalFrames;

                        int localTexX = (stripe - spriteLeft) * frameWidth / spriteWidth;

                        if (localTexX < 0) localTexX = 0;
                        if (localTexX >= frameWidth) localTexX = frameWidth - 1;
                        
                        texX = localTexX + (frame * frameWidth); 
                    }

                    for (int y = drawStartY; y < drawEndY; y++) {
                        if (tex != null) {
                            int d = (y - vMoveScreen) * 256 - height * 128 + spriteHeight * 128;
                            int texY = ((d * tex.height) / spriteHeight) / 256;

                            if (texY < 0 || texY >= tex.height) continue;
                            
                            int pixelColor = tex.pixels[texX + texY * tex.width];
                            int alpha = (pixelColor >> 24) & 0xFF;
                            
                            if (alpha > 50 && (pixelColor & 0x00FFFFFF) != 0xFF00FF) {
                                pixels[stripe + y * width] = pixelColor;
                            }
                        } else {
                            pixels[stripe + y * width] = colorHex; 
                        }
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

    private void drawFloorAndSky(GameEngine engine) {
        if (skyTexture != null) {
            for (int x = 0; x < width; x++) {
                double cameraX = 2 * x / (double) width - 1;
                double rayDirX = engine.player.dirX + engine.player.planeX * cameraX;
                double rayDirY = engine.player.dirY + engine.player.planeY * cameraX;

                double rayAngle = Math.atan2(rayDirY, rayDirX);
                if (rayAngle < 0) rayAngle += 2 * Math.PI;

                int texX = (int) ((rayAngle / (2 * Math.PI)) * skyTexture.width);
                if (texX < 0) texX = 0;
                if (texX >= skyTexture.width) texX = skyTexture.width - 1;

                for (int y = 0; y < height / 2; y++) {
                    int texY = (y * skyTexture.height) / (height / 2);
                    if (texY < 0) texY = 0;
                    if (texY >= skyTexture.height) texY = skyTexture.height - 1;

                    pixels[x + y * width] = skyTexture.pixels[texX + texY * skyTexture.width];
                }
            }
        } else {
            for (int i = 0; i < pixels.length / 2; i++) {
                pixels[i] = 0x87CEEB;
            }
        }

        if (floorTexture != null) {
            for (int y = height / 2; y < height; y++) {
                double rayDirX0 = engine.player.dirX - engine.player.planeX;
                double rayDirY0 = engine.player.dirY - engine.player.planeY;
                double rayDirX1 = engine.player.dirX + engine.player.planeX;
                double rayDirY1 = engine.player.dirY + engine.player.planeY;

                int p = y - height / 2; 
                if (p == 0) p = 1; 
                double posZ = 0.5 * height; 
                double rowDistance = posZ / p;

                double floorStepX = rowDistance * (rayDirX1 - rayDirX0) / width;
                double floorStepY = rowDistance * (rayDirY1 - rayDirY0) / width;

                double floorX = engine.player.posX + rowDistance * rayDirX0;
                double floorY = engine.player.posY + rowDistance * rayDirY0;

                for (int x = 0; x < width; ++x) {
                    int cellX = (int)(floorX);
                    int cellY = (int)(floorY);

                    int tx = (int)(floorTexture.width * (floorX - cellX)) & (floorTexture.width - 1);
                    int ty = (int)(floorTexture.height * (floorY - cellY)) & (floorTexture.height - 1);

                    floorX += floorStepX;
                    floorY += floorStepY;

                    int color = floorTexture.pixels[tx + ty * floorTexture.width];
                    int r = ((color >> 16) & 0xFF) >> 1;
                    int g = ((color >> 8) & 0xFF) >> 1;
                    int b = (color & 0xFF) >> 1;
                    color = (r << 16) | (g << 8) | b;

                    pixels[x + y * width] = color;
                }
            }
        } else {
            for (int i = pixels.length / 2; i < pixels.length; i++) {
                pixels[i] = 0x555555;
            }
        }
    }
}