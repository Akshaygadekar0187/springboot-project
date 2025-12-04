package windpunk;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.util.Random;

class Building {
	private final int x, y, w, h;
	private final Color facade;
	private final Random rnd = new Random();

	public Building(int x, int y, int w, int h, Color facade) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.facade = facade;
	}

	public void draw(Graphics2D g2) {
		// building base
		GradientPaint gp = new GradientPaint(x, y, facade.darker().darker(), x + w, y + h, facade.darker());
		g2.setPaint(gp);
		g2.fillRect(x, y, w, h);

		// windows — grid of neon lit boxes
		int cols = Math.max(1, w / 12);
		int rows = Math.max(2, h / 18);
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				if (rnd.nextDouble() < 0.35) {
					int wx = x + 4 + i * (w / cols);
					int wy = y + 4 + j * (h / rows);
					int ww = Math.max(4, (w / cols) - 6);
					int hh = Math.max(6, (h / rows) - 6);
					// window color variations
					int r = Math.min(255, facade.getRed() + rnd.nextInt(80));
					int g = Math.min(255, facade.getGreen() + rnd.nextInt(80));
					int b = Math.min(255, facade.getBlue() + rnd.nextInt(80));
					Color wc = new Color(r, g, b, 180);
					g2.setColor(wc);
					g2.fillRect(wx, wy, ww, hh);
				}
			}
		}

		// neon sign sometimes
		if (rnd.nextDouble() < 0.02) {
			drawNeonSign(g2);
		}
	}

	private void drawNeonSign(Graphics2D g2) {
		Composite old = g2.getComposite();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.55f));
		g2.setColor(new Color(220, 80, 220));
		g2.fillRoundRect(x + w / 4, y + 10, w / 2, 18, 6, 6);
		g2.setComposite(old);
	}
}