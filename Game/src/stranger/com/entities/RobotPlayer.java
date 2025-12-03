package stranger.com.entities;

import java.awt.*;

import stranger.com.util.Constants;

public class RobotPlayer {
	public int x, y;
	public int w = Constants.PLAYER_WIDTH;
	public int h = Constants.PLAYER_HEIGHT;

	private int vx = 0;
	private int speed = Constants.PLAYER_SPEED;

	private int hoverPhase = 0;
	private boolean damaged = false;
	private int damageTimer = 0;

	public RobotPlayer(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void moveLeft() {
		vx = -speed;
	}

	public void moveRight() {
		vx = speed;
	}

	public void stop() {
		vx = 0;
	}

	public void update(int screenW, int screenH) {
		x += vx;
		if (x < 6)
			x = 6;
		if (x + w > screenW - 6)
			x = screenW - w - 6;
		hoverPhase = (hoverPhase + 1) % 360;
		if (damaged) {
			damageTimer--;
			if (damageTimer <= 0)
				damaged = false;
		}
	}

	public void hit() {
		damaged = true;
		damageTimer = 30;
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, w, h);
	}

	public Bullet shoot() {
		return new Bullet(x + w / 2 - 3, y - 8);
	}

	public void draw(Graphics2D g) {
		// robot body
		int aura = 6 + (int) (Math.sin(hoverPhase * Math.PI / 90.0) * 3);
		// aura
		Composite old = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.22f));
		g.setColor(new Color(80, 200, 230));
		g.fillRoundRect(x - aura, y - aura, w + aura * 2, h + aura * 2, 12, 12);
		g.setComposite(old);

		// body plate
		GradientPaint gp = new GradientPaint(x, y, new Color(40, 40, 60), x + w, y + h, new Color(80, 90, 110));
		g.setPaint(gp);
		g.fillRoundRect(x, y, w, h, 8, 8);

		// face panel with glowing eye slit
		g.setColor(Color.BLACK);
		g.fillRect(x + 6, y + 6, w - 12, 12);
		// glowing eyes strips
		g.setColor(new Color(120, 220, 255));
		g.fillRect(x + 10, y + 8, w - 20, 6);

		// corrupted circuits & vines (thin purple lines)
		g.setColor(new Color(160, 80, 200));
		for (int i = 0; i < 3; i++) {
			g.drawLine(x + 6 + i * 8, y + 20 + i * 6, x + w - 6 - i * 6, y + 30 + i * 4);
		}

		// small status flash if damaged
		if (damaged) {
			g.setColor(new Color(255, 60, 60, 100));
			g.fillRoundRect(x, y, w, h, 8, 8);
		}

		// outline
		g.setColor(new Color(0, 0, 0, 120));
		g.setStroke(new BasicStroke(2f));
		g.drawRoundRect(x, y, w, h, 8, 8);
	}
}
