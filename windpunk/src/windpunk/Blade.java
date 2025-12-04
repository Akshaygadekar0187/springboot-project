package windpunk;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;

class Blade {
	public final int length;
	private Color color;

	public Blade(int length, Color color) {
		this.length = length;
		this.color = color;
	}

	public void draw3D(Graphics2D g2) {
		// build shape in local coords pointing right from origin
		Polygon blade = new Polygon();
		blade.addPoint(0, -18);
		blade.addPoint(length, -8);
		blade.addPoint(length, 8);
		blade.addPoint(0, 18);

		// shading gradient: light top → dark bottom along length
		GradientPaint gp = new GradientPaint(0f, 0f, color.brighter(), length, 0f, color.darker());
		Composite oldComp = g2.getComposite();
		Paint oldPaint = g2.getPaint();

		g2.setPaint(gp);
		g2.fillPolygon(blade);

		// edge stroke
		g2.setStroke(new BasicStroke(1.5f));
		g2.setColor(new Color(10, 10, 20, 140));
		g2.drawPolygon(blade);

		// neon tip: radial glow
		int tipX = length;
		RadialGradientPaint rg = new RadialGradientPaint(new Point2D.Double(tipX, 0), 18, new float[] { 0f, 1f },
				new Color[] { Color.WHITE, new Color(color.getRed(), color.getGreen(), color.getBlue(), 0) });
		g2.setPaint(rg);
		g2.fillOval(tipX - 18, -18, 36, 36);

		// neon rim on tip (solid)
		g2.setPaint(oldPaint);
		g2.setComposite(oldComp);
	}
}