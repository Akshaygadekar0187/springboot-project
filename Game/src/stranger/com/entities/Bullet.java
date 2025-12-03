package stranger.com.entities;

import java.awt.Color;
import java.awt.*;

public class Bullet {
	public int x, y;
	public int WIDTH = 6, HEIGHT = 12;
	public int speed = 10;
	public boolean active = true;
	public int damage = 12;

	public Bullet(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void update() {
		y -= speed;
		if (y < -20)
			active = false;
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}

	public void draw(Graphics2D g) {
		Composite old = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
		GradientPaint gp = new GradientPaint(x, y, Color.WHITE, x, y + HEIGHT, new Color(120, 220, 255));
		g.setPaint(gp);
		g.fillRoundRect(x, y, WIDTH, HEIGHT, 4, 4);
		g.setComposite(old);
	}
}
