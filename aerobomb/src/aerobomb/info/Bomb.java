package aerobomb.info;

import java.awt.Color;
import java.awt.Graphics2D;


public class Bomb {
    public int x, y;
    public int size = 12;

    public boolean exploded = false;
    public int explosionSize = 1;
    double vy = 0;

    public Bomb(int x, int y) {
        this.x = x; this.y = y;
    }

    public void update() {
        if (!exploded) {
            vy += 0.4;
            y += vy;

            if (y >= 420) exploded = true;
        } else {
            explosionSize += 8;
        }
    }

    public void draw(Graphics2D g) {
        if (!exploded) {
            g.setColor(Color.YELLOW);
            g.fillOval(x, y, size, size);
        } else {
            g.setColor(new Color(255, 160, 50, 130));
            g.fillOval(x - explosionSize/2, y - explosionSize/2,
                    explosionSize, explosionSize);
        }
    }
}
