package windpunk;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class BackgroundScene {

	private final int width, height;
	private final List<Building> buildings = new ArrayList<>();
	private final Random rnd = new Random();
	private float flash = 0f; // lightning/neon pulse

	public BackgroundScene(int w, int h) {
		this.width = w;
		this.height = h;
		generateBuildings();
	}

	private void generateBuildings() {
		int x = 0;
		while (x < width + 200) {
			int bw = 40 + rnd.nextInt(120);
			int bh = 80 + rnd.nextInt((int) (height * 0.6));
			int base = height - (int) (height * 0.15);
			buildings.add(new Building(x, base - bh, bw, bh, randomFacade()));
			x += bw + 10;
		}
	}

	private Color randomFacade() {
		// tinted neon windows
		int r = 30 + rnd.nextInt(80);
		int g = 10 + rnd.nextInt(120);
		int b = 80 + rnd.nextInt(160);
		return new Color(r, g, b);
	}

	public void flashPulse() {
		flash = 1f; // will decay
	}

	public void draw(Graphics2D g2) {
		// sky gradient with subtle neon haze
		GradientPaint sky = new GradientPaint(0, 0, new Color(10, 6, 30), 0, height, new Color(12, 6, 50));
		g2.setPaint(sky);
		g2.fillRect(0, 0, width, height);

		// moon (neon disc)
		drawNeonMoon(g2);

		// buildings
		for (Building b : buildings)
			b.draw(g2);

		// faint horizontal fog layer
		for (int i = 0; i < 3; i++) {
			Composite old = g2.getComposite();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.04f));
			g2.setColor(new Color(140, 60, 220));
			g2.fillRect((int) (i * 20), (int) (height * 0.45f + i * 8), width, 60);
			g2.setComposite(old);
		}

		// neon pulse overlay when flash > 0
		if (flash > 0) {
			Composite old = g2.getComposite();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(0.6f, flash)));
			g2.setColor(new Color(120, 90, 255));
			g2.fillRect(0, 0, width, height);
			g2.setComposite(old);
			flash -= 0.08f;
			if (flash < 0)
				flash = 0;
		}

		// subtle distant lightning (thin lines)
		if (Math.random() < 0.003)
			drawLightning(g2);
	}

	private void drawNeonMoon(Graphics2D g2) {
		int mx = (int) (width * 0.85);
		int my = (int) (height * 0.18);
		int r = 36;
		for (int i = 8; i >= 0; i--) {
			g2.setColor(new Color(160, 120, 255, (int) (40 * (1.0 - i / 10.0))));
			g2.fillOval(mx - r - i * 4, my - r - i * 4, (r + i * 4) * 2, (r + i * 4) * 2);
		}
		g2.setColor(new Color(220, 200, 255, 230));
		g2.fillOval(mx - r / 2, my - r / 2, r, r);
	}

	private void drawLightning(Graphics2D g2) {
		int x = (int) (Math.random() * width);
		int y = 0;
		Path2D path = new Path2D.Double();
		path.moveTo(x, y);
		int segments = 10 + rnd.nextInt(8);
		for (int i = 0; i < segments; i++) {
			x += -30 + rnd.nextInt(60);
			y += height / segments + rnd.nextInt(40);
			path.lineTo(x, y);
		}
		Stroke oldStroke = g2.getStroke();
		Composite oldComp = g2.getComposite();
		g2.setStroke(new BasicStroke(2.5f));
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
		g2.setColor(new Color(200, 220, 255));
		g2.draw(path);
		g2.setStroke(oldStroke);
		g2.setComposite(oldComp);
	}
}
