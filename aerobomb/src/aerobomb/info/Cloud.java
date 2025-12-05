package aerobomb.info;

import java.awt.Color;
import java.awt.Graphics2D;


public class Cloud {

    int x, y;
    int speed = 1;

    public Cloud(int x, int y) {
        this.x = x; this.y = y;
    }

    public void update() {
        x -= speed;
        if (x < -120) x = 1400;
    }

    public void draw(Graphics2D g) {
        g.setColor(new Color(255, 200, 255, 90));
        g.fillOval(x, y, 90, 40);
        g.fillOval(x + 30, y - 10, 100, 50);
        g.fillOval(x + 60, y, 80, 35);
    }
}
