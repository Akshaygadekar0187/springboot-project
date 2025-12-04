package windpunk;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.Random;

class Particle {
	double x, y, vx, vy;
	double life;
	float size;
	Color color;

	public Particle(double x, double y, double vx, double vy, double life, float size, Color color) {
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.life = life;
		this.size = size;
		this.color = color;
	}

	public static Particle createWindParticle(int width, int height, int speed) {
		Random rnd = new Random();
		double y = height * (0.6 + rnd.nextDouble() * 0.35); // near ground
		double x = -20; // spawn left
		double vx = 1 + rnd.nextDouble() * (0.5 + speed / 6.0);
		double vy = -0.15 + rnd.nextDouble() * 0.3;
		double life = 1.0;
		float size = 2f + rnd.nextFloat() * 3f;
		Color c = new Color(120, 200, 255, 120); // cyan-ish particles
		return new Particle(x, y, vx, vy, life, size, c);
	}

	// returns false when dead
	public boolean update() {
		x += vx;
		y += vy;
		life -= 0.01 + Math.random() * 0.01;
		return life > 0 && x < 2000;
	}

	public void draw(Graphics2D g2) {
		Composite old = g2.getComposite();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) Math.max(0.02, life * 0.6)));
		g2.setColor(color);
		g2.fill(new Ellipse2D.Double(x, y, size, size / 1.2));
		g2.setComposite(old);
	}
}