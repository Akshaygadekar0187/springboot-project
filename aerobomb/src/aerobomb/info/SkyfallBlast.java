package aerobomb.info;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SkyfallBlast extends JPanel implements ActionListener, KeyListener {

    private Timer timer;
    private Plane plane;
    private Bomb bomb;
    private ArrayList<Building> buildings = new ArrayList<>();
    private ArrayList<Cloud> clouds = new ArrayList<>();
    private ArrayList<Particle> particles = new ArrayList<>();

    private int cameraShake = 0;
    private Random rnd = new Random();

    public SkyfallBlast() {
        setFocusable(true);
        addKeyListener(this);

        plane = new Plane(120, 120);

        // clouds
        for (int i = 0; i < 6; i++)
            clouds.add(new Cloud(200 + i * 200, 40 + rnd.nextInt(80)));

        // buildings
        buildCity();

        timer = new Timer(16, this);
        timer.start();
    }

    private void buildCity() {
        buildings.clear();
        for (int i = 0; i < 18; i++) {
            int w = 70 + rnd.nextInt(40);
            int h = 150 + rnd.nextInt(200);
            int x = i * 120;
            int y = 500 - h;

            Color neon = new Color(
                    150 + rnd.nextInt(100),
                    20 + rnd.nextInt(30),
                    200 + rnd.nextInt(55)
            );

            buildings.add(new Building(x, y, w, h, neon));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.translate(
                cameraShake == 0 ? 0 : rnd.nextInt(cameraShake) - cameraShake / 2,
                cameraShake == 0 ? 0 : rnd.nextInt(cameraShake) - cameraShake / 2
        );

        drawSky(g2);

        for (Cloud c : clouds) c.draw(g2);

        for (Building b : buildings) b.draw(g2);

        plane.draw(g2);

        if (bomb != null) bomb.draw(g2);

        for (Particle p : particles) p.draw(g2);

        g2.translate(0, 0);
    }

    private void drawSky(Graphics2D g2) {
        GradientPaint sky = new GradientPaint(
                0, 0, new Color(30, 0, 50),
                0, getHeight(), new Color(240, 50, 130)
        );
        g2.setPaint(sky);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        plane.update();

        for (Cloud c : clouds) c.update();

        if (bomb != null) {
            bomb.update();

            if (bomb.exploded) {
                // spawn particles
                for (int i = 0; i < 8; i++)
                    particles.add(new Particle(bomb.x, bomb.y));

                // building destruction
                for (Building b : buildings) {
                    if (!b.destroyed && b.isHit(bomb)) b.destroyed = true;
                }

                // camera shake
                cameraShake = Math.min(40, bomb.explosionSize / 4);

                if (bomb.explosionSize > 250) bomb = null;
            }
        }

        particles.removeIf(Particle::dead);
        particles.forEach(Particle::update);

        if (cameraShake > 0) cameraShake -= 1;

        repaint();
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("CYBERPUNK NUCLEAR STRIKE");
        SkyfallBlast s = new SkyfallBlast();
        f.add(s);
        f.setSize(1300, 600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int c = e.getKeyCode();
        switch (c) {
            case KeyEvent.VK_LEFT: plane.left = true; break;
            case KeyEvent.VK_RIGHT: plane.right = true; break;
            case KeyEvent.VK_UP: plane.up = true; break;
            case KeyEvent.VK_DOWN: plane.down = true; break;

            case KeyEvent.VK_SPACE:
                if (bomb == null)
                    bomb = new Bomb(plane.x + 80, plane.y + 20);
                break;

            case KeyEvent.VK_R:
                bomb = null;
                particles.clear();
                buildCity();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int c = e.getKeyCode();
        switch (c) {
            case KeyEvent.VK_LEFT: plane.left = false; break;
            case KeyEvent.VK_RIGHT: plane.right = false; break;
            case KeyEvent.VK_UP: plane.up = false; break;
            case KeyEvent.VK_DOWN: plane.down = false; break;
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
}
