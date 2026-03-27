package core;

import graphics.Renderer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main extends JPanel implements Runnable, KeyListener {

    private JFrame frame;
    private Thread gameThread;
    private boolean isRunning = false;

    private GameEngine engine;
    private Renderer renderer;
    private int[][] currentFrame;

    private final int WIDTH = 1920;
    private final int HEIGHT = 1080;
    private boolean w, a, s, d;

    public Main() {
        engine = new GameEngine();
        renderer = new Renderer(WIDTH, HEIGHT);

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
            repaint(); 

            try { Thread.sleep(16); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.draw(g, engine, currentFrame); 
    }

    @Override 
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) w = true;
        if (e.getKeyCode() == KeyEvent.VK_A) a = true;
        if (e.getKeyCode() == KeyEvent.VK_S) s = true;
        if (e.getKeyCode() == KeyEvent.VK_D) d = true;

        // FSM Transitions on ENTER key!
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (engine.currentState == GameState.MENU || 
                engine.currentState == GameState.GAME_OVER || 
                engine.currentState == GameState.VICTORY) {
                engine.reset();
            }
        }
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