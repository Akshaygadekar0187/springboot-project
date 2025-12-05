package aerobomb.info;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.*;
import java.util.Random;

public class Building {
    public int x, y, w, h;
    public boolean destroyed = false;

    private final Color facade;
    private final Random rnd = new Random();

    public Building(int x, int y, int w, int h, Color facade) {
        this.x = x; this.y = y;
        this.w = w; this.h = h;
        this.facade = facade;
    }

    public void draw(Graphics2D g2) {
        if (destroyed) return;

        GradientPaint gp = new GradientPaint(
                x, y, facade.darker(),
                x + w, y + h, facade.darker().darker()
        );

        g2.setPaint(gp);
        g2.fillRect(x, y, w, h);

        int cols = Math.max(1, w / 12);
        int rows = Math.max(2, h / 18);

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                if (rnd.nextDouble() < 0.32) {

                    int wx = x + 4 + i * (w / cols);
                    int wy = y + 4 + j * (h / rows);
                    int ww = Math.max(4, (w / cols) - 6);
                    int hh = Math.max(6, (h / rows) - 6);

                    int r = Math.min(255, facade.getRed() + rnd.nextInt(60));
                    int g = Math.min(255, facade.getGreen() + rnd.nextInt(60));
                    int b = Math.min(255, facade.getBlue() + rnd.nextInt(60));

                    g2.setColor(new Color(r, g, b, 190));
                    g2.fillRect(wx, wy, ww, hh);
                }
            }
        }
    }

    public boolean isHit(Bomb bomb) {
        int cx = x + w / 2;
        int cy = y + h / 2;
        double d = Math.hypot(cx - bomb.x, cy - bomb.y);
        return d < bomb.explosionSize;
    }
}
