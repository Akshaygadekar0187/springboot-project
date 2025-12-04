package windpunk;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;

public class NeonBlade {

    private Color color;

    public NeonBlade(Color c) {
        this.color = c;
    }

    public void draw(Graphics2D g2) {

        GradientPaint gp = new GradientPaint(
                0, 0, color.brighter(),
                150, 0, color.darker()
        );
        g2.setPaint(gp);

        Polygon blade = new Polygon();
        blade.addPoint(0, 0);
        blade.addPoint(150, -20);
        blade.addPoint(150, 20);

        g2.fillPolygon(blade);

        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 180));
        g2.fillOval(130, -10, 20, 20);
    }
}
