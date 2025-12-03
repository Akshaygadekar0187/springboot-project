package stranger.com.entities;

import java.awt.*;
import java.util.Random;

import stranger.com.util.Constants;

public class RobotVecna {
	public int x, y;
	public int WIDTH = Constants.VECNA_WIDTH;
	public int HEIGHT = Constants.VECNA_HEIGHT;

	public int hp = Constants.VECNA_MAX_HP;
	public int maxHp = Constants.VECNA_MAX_HP;
	public boolean isActive = false;

	private int vx = 0;
	private int vy = 1;
	private Random rnd = new Random();
	private int roamTimer = 0;

	public RobotVecna(int x, int y) {
		this.x = x;
		this.y = y;
		this.isActive = true;
	}

	public void spawnAt(int nx, int ny) {
		x = nx;
		y = ny;
		hp = maxHp;
		isActive = true;
	}

	public void updateTowards(RobotPlayer p, int dividerY, int screenW) {
		if (!isActive) {
			// slowly respawn from Upside Down
			y += 0;
			return;
		}
		// if above divider, descend
		if (y < dividerY - 20)
			y += vy;
		else {
			// simple hunting AI: move horizontally toward player
			if (roamTimer-- <= 0) {
				roamTimer = 40 + rnd.nextInt(90);
				vx = (rnd.nextBoolean() ? 1 : -1) * (1 + rnd.nextInt(2));
			}
			// follow player sometimes
			if (Math.abs((p.x + p.w / 2) - (x + WIDTH / 2)) > 6) {
				if ((p.x + p.w / 2) < (x + WIDTH / 2))
					x -= 1 + rnd.nextInt(2);
				else
					x += 1 + rnd.nextInt(2);
			} else {
				x += vx;
			}
			// limit
			if (x < 8)
				x = 8;
			if (x + WIDTH > screenW - 8)
				x = screenW - WIDTH - 8;
		}
	}

	public void takeDamage(int d) {
		hp -= d;
		if (hp <= 0) {
			hp = 0;
			isActive = false;
			// retreat
			y = -HEIGHT - 100;
		}
	}

	public void knockBack() {
		y -= 40;
		if (y < -HEIGHT)
			y = -HEIGHT;
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}

	public void draw(Graphics2D g, int dividerY) {
		if (!isActive) {
			// subtle sleeping silhouette near top edge
			g.setColor(new Color(60, 10, 80, 80));
			g.fillOval(x, Math.max(8, dividerY - 120), WIDTH, HEIGHT / 2);
			return;
		}

		// draw main corrupted robot: metallic torso
		GradientPaint gp = new GradientPaint(x, y, new Color(20, 10, 30), x + WIDTH, y + HEIGHT, new Color(40, 20, 60));
		g.setPaint(gp);
		g.fillRoundRect(x, y, WIDTH, HEIGHT, 16, 20);

		// corrupted purple circuits
		g.setColor(new Color(180, 60, 200));
		g.setStroke(new BasicStroke(3f));
		for (int i = 0; i < 5; i++) {
			int sx = x + 8 + i * 10;
			int sy = y + 16 + (i % 2) * 8;
			g.drawLine(sx, sy, sx + (i % 2 == 0 ? 18 : -18), sy + 14);
		}

		// head spikes / inverted crown
		g.setColor(new Color(30, 8, 40));
		Polygon p = new Polygon();
		p.addPoint(x + 8, y + 12);
		p.addPoint(x + WIDTH / 2, y - 26);
		p.addPoint(x + WIDTH - 8, y + 12);
		g.fillPolygon(p);

		// glowing eyes (two red orbs)
		g.setColor(new Color(220, 40, 60));
		g.fillOval(x + 14, y + 30, 12, 12);
		g.fillOval(x + WIDTH - 26, y + 30, 12, 12);

		// health bar
		g.setColor(Color.DARK_GRAY);
		g.fillRect(x, y - 12, WIDTH, 8);
		g.setColor(new Color(200, 50, 70));
		int hw = (int) ((hp / (double) maxHp) * WIDTH);
		g.fillRect(x, y - 12, Math.max(0, hw), 8);

		// outline
		g.setColor(new Color(0, 0, 0, 120));
		g.setStroke(new BasicStroke(2f));
		g.drawRoundRect(x, y, WIDTH, HEIGHT, 16, 20);

		// hanging corrupted roots (thin lines) to tie into Upside Down aesthetic
		g.setColor(new Color(30, 10, 30, 180));
		for (int i = 0; i < 4; i++) {
			int rx = x + 6 + i * (WIDTH / 4);
			g.drawLine(rx, y + HEIGHT - 6, rx - 6 + (i % 2 == 0 ? -6 : 6), y + HEIGHT + 20 + i * 6);
		}
	}
}
