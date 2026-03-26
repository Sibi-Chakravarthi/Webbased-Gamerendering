import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main extends JPanel implements Runnable , KeyListener {

    private JFrame frame;
    private Thread gameThread;
    private boolean isRunning = false;

    private GameEngine engine;
    private int[][] currentFrame;

    private boolean w , a , s , d;

    public Main(){

        engine = new GameEngine();

        frame = new JFrame("Raycaster Game Engine");
        frame.setSize(1920, 1080);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        frame.add(this);
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
            repaint(); 

            try {
                Thread.sleep(16); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.setColor(new Color(135, 206, 235));
        g.fillRect(0, 0, 1920, 540);
        
        g.setColor(new Color(85, 85, 85));
        g.fillRect(0, 540, 1920, 540);

        if (currentFrame != null) {
            for (int x = 0; x < currentFrame.length; x++) {
                int drawStart = currentFrame[x][0];
                int drawEnd = currentFrame[x][1];
                int side = currentFrame[x][2];
                int wallType = currentFrame[x][3];
                int lineHeight = drawEnd - drawStart;

                if (wallType == 2) {
                    g.setColor(side == 0 ? new Color(0, 255, 0) : new Color(0, 170, 0));
                } else if (wallType == 3) {
                    g.setColor(side == 0 ? new Color(0, 0, 255) : new Color(0, 0, 170));
                } else {
                    g.setColor(side == 0 ? new Color(204, 0, 0) : new Color(119, 0, 0));
                }

                g.fillRect(x, drawStart, 1, lineHeight);
            }
        }
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

    // THE STARTING POINT
    public static void main(String[] args) {
        new Main();
    }

}