package windpunk;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

class Spark {
	double x, y;
	double life;
	double vx, vy;
	Color color;
	double angle;

	public Spark(double x, double y) {
		this.x = x;
		this.y = y;
		this.life = 1.0;
		this.vx = -1 + Math.random() * 2;
		this.vy = -1 + Math.random() * 2;
		this.color = new Color(180 + (int) (Math.random() * 70), 200, 255);
		this.angle = Math.random() * Math.PI * 2;
	}

	public boolean update() {
		x += vx * (1 + Math.random() * 0.8);
		y += vy * (1 + Math.random() * 0.8);
		life -= 0.05 + Math.random() * 0.02;
		return life > 0;
	}

	public void draw(Graphics2D g2) {
		Composite old = g2.getComposite();
		g2.setStroke(new BasicStroke(2f));
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) Math.max(0.02, life)));
		g2.setColor(new Color(200, 240, 255, (int) (life * 255)));
		// jagged short line
		Path2D path = new Path2D.Double();
		path.moveTo(x, y);
		double len = 8 + Math.random() * 12;
		double a = angle + (Math.random() - 0.5) * 0.8;
		path.lineTo(x + Math.cos(a) * len, y + Math.sin(a) * len);
		g2.draw(path);

		// small glow
		RadialGradientPaint rg = new RadialGradientPaint(new Point2D.Double(x, y), 6, new float[] { 0f, 1f },
				new Color[] { Color.WHITE, new Color(180, 230, 255, 0) });
		g2.setPaint(rg);
		g2.fill(new Ellipse2D.Double(x - 6, y - 6, 12, 12));

		g2.setComposite(old);
	}
}
