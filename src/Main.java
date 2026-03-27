import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Main extends JPanel implements Runnable , KeyListener {

    private JFrame frame;
    private Thread gameThread;
    private boolean isRunning = false;

    private GameEngine engine;
    private int[][] currentFrame;

    private final int WIDTH = 1920;
    private final int HEIGHT = 1080;
    private BufferedImage image;
    private int[] pixels;

    private boolean w , a , s , d;

    public Main() {
        engine = new GameEngine();

        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        frame = new JFrame("RayForge Engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        this.setPreferredSize(new java.awt.Dimension(WIDTH, HEIGHT));
        frame.add(this);
        frame.pack(); 
        
        frame.setLocationRelativeTo(null);
        frame.addKeyListener(this);
        frame.setVisible(true);

        startThread();
    }

    private synchronized void startThread() {
        isRunning = true;
        gameThread = new Thread(this, "GameLoop");
        gameThread.start();
    }

    @Override
    public void run() {
        while (isRunning) {
            currentFrame = engine.tick(w, a, s, d);

            renderToBuffer(); 
            repaint(); 

            try {
                Thread.sleep(16); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void renderToBuffer() {
        
        for (int i = 0; i < pixels.length; i++) {
            if (i < pixels.length / 2) {
                pixels[i] = 0x87CEEB;
            } else {
                pixels[i] = 0x555555;
            }
        }

        if (currentFrame != null) {
            for (int x = 0; x < currentFrame.length; x++) {
                int drawStart = currentFrame[x][0];
                int drawEnd = currentFrame[x][1];
                int side = currentFrame[x][2];
                int wallType = currentFrame[x][3];

                int color = 0;
                if (wallType == 2) {
                    color = (side == 0) ? 0x00FF00 : 0x00AA00;
                } else if (wallType == 3) {
                    color = (side == 0) ? 0x0000FF : 0x0000AA;
                } else {
                    color = (side == 0) ? 0xCC0000 : 0x770000;
                }

                for (int y = drawStart; y < drawEnd; y++) {
                    pixels[x + y * WIDTH] = color;
                }
            }
        }

        if (engine.enemy != null) {

            double spriteX = engine.enemy.posX - engine.player.posX;
            double spriteY = engine.enemy.posY - engine.player.posY;

            double invDet = 1.0 / (engine.player.planeX * engine.player.dirY - engine.player.dirX * engine.player.planeY);

            double transformX = invDet * (engine.player.dirY * spriteX - engine.player.dirX * spriteY);
            double transformY = invDet * (-engine.player.planeY * spriteX + engine.player.planeX * spriteY);

            if (transformY > 0) {

                int spriteScreenX = (int) ((WIDTH / 2) * (1 + transformX / transformY));

                int spriteHeight = Math.abs((int) (HEIGHT / transformY));
                int drawStartY = -spriteHeight / 2 + HEIGHT / 2;
                if (drawStartY < 0) drawStartY = 0;
                int drawEndY = spriteHeight / 2 + HEIGHT / 2;
                if (drawEndY >= HEIGHT) drawEndY = HEIGHT - 1;

                int spriteWidth = Math.abs((int) (HEIGHT / transformY));
                int drawStartX = -spriteWidth / 2 + spriteScreenX;
                if (drawStartX < 0) drawStartX = 0;
                int drawEndX = spriteWidth / 2 + spriteScreenX;
                if (drawEndX >= WIDTH) drawEndX = WIDTH - 1;

                for (int stripe = drawStartX; stripe < drawEndX; stripe++) {

                    if (stripe >= 0 && stripe < WIDTH && transformY < engine.raycaster.zBuffer[stripe]) {
                        for (int y = drawStartY; y < drawEndY; y++) {
                            pixels[stripe + y * WIDTH] = 0xFF00FF;
                        }
                    }
                }
            }
        }

        if (engine.currentState != GameState.PLAYING) {
            
            boolean isGameOver = (engine.currentState == GameState.GAME_OVER);
            
            // Loop through every pixel on the screen (O(N) operation)
            for (int i = 0; i < pixels.length; i++) {
                int oldPixel = pixels[i];

                // 1. Bit Masking: Extract RGB using Right Shift (>>) and Bitwise AND (&)
                int r = (oldPixel >> 16) & 0xFF;
                int g = (oldPixel >> 8) & 0xFF;
                int b = oldPixel & 0xFF;
                // 2. Fast 50% Alpha Blend using Right Shift (>> 1 is identical to / 2)
                if (isGameOver) {
                    // Tint Red: Maximize Red, halve Green and Blue
                    r = (r + 255) >> 1; 
                    g = g >> 1;
                    b = b >> 1;
                } else {
                    // Tint Green: Maximize Green, halve Red and Blue
                    r = r >> 1;
                    g = (g + 255) >> 1; 
                    b = b >> 1;
                }

                // 3. Bit Packing: Recombine back into a 32-bit integer using Left Shift (<<) and Bitwise OR (|)
                pixels[i] = (r << 16) | (g << 8) | b;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }

    @Override 
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) w = true;
        if (e.getKeyCode() == KeyEvent.VK_A) a = true;
        if (e.getKeyCode() == KeyEvent.VK_S) s = true;
        if (e.getKeyCode() == KeyEvent.VK_D) d = true;
    }

    @Override 
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) w = false;
        if (e.getKeyCode() == KeyEvent.VK_A) a = false;
        if (e.getKeyCode() == KeyEvent.VK_S) s = false;
        if (e.getKeyCode() == KeyEvent.VK_D) d = false;
    }

    @Override 
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new Main();
    }
}