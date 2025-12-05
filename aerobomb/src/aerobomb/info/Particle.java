package aerobomb.info;
import java.awt.*;
import java.util.Random;

public class Particle {
    double x, y, vx, vy;
    int life = 25;
    Random rnd = new Random();

    public Particle(int x, int y) {
        this.x = x;
        this.y = y;
        this.vx = rnd.nextDouble()*4 - 2;
        this.vy = rnd.nextDouble()*4 - 2;
    }

    public void update() {
        x += vx;
        y += vy;
        vy += 0.1;
        life--;
    }

    public boolean dead() {
        return life <= 0;
    }

    public void draw(Graphics2D g) {
        g.setColor(new Color(255, 150, 80, Math.max(40, life * 10)));
        g.fillOval((int)x, (int)y, 6, 6);
    }
}
