package graphics;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Texture {
    public int[] pixels;
    public int width, height;

    public Texture(String filepath) {
        try {
            BufferedImage image = ImageIO.read(new File(filepath));
            this.width = image.getWidth();
            this.height = image.getHeight();
            this.pixels = new int[width * height];

            image.getRGB(0, 0, width, height, pixels, 0, width);
            System.out.println("🖼️ Successfully loaded texture: " + filepath);

        } catch (IOException e) {
            System.err.println("❌ CRITICAL: Could not load texture at " + filepath);
            e.printStackTrace();
            this.width = 64;
            this.height = 64;
            this.pixels = new int[64 * 64];
            for (int i = 0; i < pixels.length; i++) pixels[i] = 0xFF00FF; 
        }
    }
}