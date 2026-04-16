package core;

import graphics.Renderer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import interfaces.IEquippable;

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
    private boolean isShooting;

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

            if (isShooting && engine.currentState == GameState.PLAYING) {
                IEquippable currentWeapon = engine.player.inventory[engine.player.activeSlot];
                if (currentWeapon != null && engine.player.weaponCooldown <= 0 && currentWeapon.getAmmo() > 0) {
                    currentWeapon.fire(engine);
                    if (currentWeapon instanceof items.Shotgun) engine.player.weaponCooldown = 1.0;
                    else if (currentWeapon instanceof items.Rifle) engine.player.weaponCooldown = 0.15;
                    else engine.player.weaponCooldown = 0.6;
                } else if (currentWeapon != null && engine.player.weaponCooldown <= 0 && currentWeapon.getAmmo() <= 0) {
                    System.out.println("❌ *Click* Out of ammo!");
                    engine.player.weaponCooldown = 0.25;
                } else if (currentWeapon == null && engine.player.weaponCooldown <= 0) {
                    System.out.println("❌ *Click* Your hands are empty!");
                    engine.player.weaponCooldown = 0.6;
                }
            }

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

        if (e.getKeyCode() == KeyEvent.VK_1) engine.player.activeSlot = 0;
        if (e.getKeyCode() == KeyEvent.VK_2) engine.player.activeSlot = 1;
        if (e.getKeyCode() == KeyEvent.VK_3) engine.player.activeSlot = 2;

        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (engine.currentState == GameState.MENU || 
                engine.currentState == GameState.GAME_OVER || 
                engine.currentState == GameState.VICTORY) {
                engine.reset();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            isShooting = true;
        }
    }

    @Override 
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) w = false;
        if (e.getKeyCode() == KeyEvent.VK_A) a = false;
        if (e.getKeyCode() == KeyEvent.VK_S) s = false;
        if (e.getKeyCode() == KeyEvent.VK_D) d = false;
        if (e.getKeyCode() == KeyEvent.VK_SPACE) isShooting = false;
    }

    @Override 
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new Main();
    }
}