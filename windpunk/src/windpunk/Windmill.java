package windpunk;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Random;

class Windmill {
	private final int cx, cy; // center position
	private double angle = 0;
	private int spinSpeed = 5;
	private final Blade[] blades = new Blade[3];
	private final Random rnd = new Random();

	public Windmill(int cx, int cy) {
		this.cx = cx;
		this.cy = cy;

		blades[0] = new Blade(150, new Color(255, 80, 180));
		blades[1] = new Blade(150, new Color(80, 200, 255));
		blades[2] = new Blade(150, new Color(150, 100, 255));
	}

	public void setAngle(double a) {
		this.angle = a;
	}

	public void setSpinSpeed(int s) {
		this.spinSpeed = s;
	}

	public void drawWithMotionBlur(Graphics2D g2) {
		// Draw hub shadowed base
		AffineTransform old = g2.getTransform();
		g2.translate(cx, cy);

		// Draw multiple layered blades with decreasing alpha to simulate motion blur
		int layers = Math.max(4, Math.min(18, 4 + spinSpeed / 2));
		double step = -spinSpeed * 0.01; // trail length dependent on speed
		for (int layer = 0; layer < layers; layer++) {
			float alpha = (float) (1.0 - (layer / (double) layers));
			Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f * alpha + 0.03f);
			Composite oldComp = g2.getComposite();
			g2.setComposite(comp);

			double localAngle = angle + layer * step;
			g2.rotate(localAngle - g2.getTransform().getTranslateX()); // rotate relative - we'll reset per blade
			// draw blades at this rotation
			drawBladesAtAngle(g2, localAngle);

			g2.setComposite(oldComp);
		}

		// solid final blades (top-most)
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		drawBladesAtAngle(g2, angle);

		// restore
		g2.setTransform(old);

		// draw hub outline and neon glow (separate so it overlays)
		drawHub(g2);
	}

	private void drawBladesAtAngle(Graphics2D g2, double a) {
		AffineTransform t = g2.getTransform();
		g2.rotate(a);
		// draw 3 blades separated by 120 degrees
		for (int i = 0; i < 3; i++) {
			blades[i].draw3D(g2);
			g2.rotate(Math.toRadians(120));
		}
		g2.setTransform(t);
	}

	void drawHub(Graphics2D g2) {
		// small hub with neon ring
		int hubR = 18;
		Point2D hubCenter = new Point2D.Double(cx, cy);

		// glow using concentric circles
		for (int i = 6; i >= 1; i--) {
			float alpha = 0.06f * i;
			g2.setColor(new Color(160, 80, 255, Math.min(255, (int) (alpha * 255))));
			g2.fillOval(cx - hubR - i * 2, cy - hubR - i * 2, (hubR + i * 2) * 2, (hubR + i * 2) * 2);
		}

		// hub solid center
		g2.setColor(new Color(20, 10, 30));
		g2.fillOval(cx - hubR / 2, cy - hubR / 2, hubR, hubR);

		// neon ring
		g2.setStroke(new BasicStroke(3f));
		g2.setColor(new Color(180, 120, 255, 200));
		g2.drawOval(cx - hubR - 2, cy - hubR - 2, (hubR + 2) * 2, (hubR + 2) * 2);
	}

	public Point2D getRandomBladeTip() {
		// choose a blade and compute tip pos using current angle
		int idx = rnd.nextInt(3);
		double a = angle + Math.toRadians(120 * idx);
		double tipX = cx + Math.cos(a) * blades[idx].length;
		double tipY = cy + Math.sin(a) * blades[idx].length;
		return new Point2D.Double(tipX, tipY);
	}
}