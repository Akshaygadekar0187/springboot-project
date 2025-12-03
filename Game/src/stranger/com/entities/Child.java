package stranger.com.entities;

import java.awt.Color;

import java.awt.*;

public class Child {

	public int x, y;
	public final int WIDTH = 25, HEIGHT = 30;

	public boolean safe = true;

	public Child(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void update() {
		// simple movement (feel free to improve)
		x += (Math.random() > 0.5) ? 1 : -1;

		x = Math.max(0, Math.min(775, x));
	}

	public void draw(Graphics g) {
		g.setColor(Color.yellow);
		g.fillRect(x, y, WIDTH, HEIGHT);
	}
}
