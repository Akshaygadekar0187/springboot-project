package aerobomb.info;

import java.awt.Color;
import java.awt.Graphics2D;

public class Plane {
    public int x, y;
    public boolean left, right, up, down;

    public Plane(int x, int y) {
        this.x = x; this.y = y;
    }

    public void update() {
        if (left)  x -= 5;
        if (right) x += 5;
        if (up)    y -= 4;
        if (down)  y += 4;
    }

    public void draw(Graphics2D g) {
        g.setColor(new Color(180, 220, 255));
        g.fillRoundRect(x, y, 90, 20, 8, 8);

        g.setColor(new Color(130, 180, 255));
        g.fillOval(x + 10, y + 5, 30, 12);

        g.setColor(Color.WHITE);
        g.fillPolygon(
                new int[]{x + 60, x + 110, x + 60},
                new int[]{y + 5, y + 10, y + 15}, 3
        );

        g.setColor(new Color(255, 100, 20));
        g.fillOval(x - 12, y + 4, 10, 10);
    }
}
