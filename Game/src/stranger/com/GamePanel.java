package stranger.com;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import stranger.com.entities.RobotPlayer;
import stranger.com.entities.RobotVecna;

import stranger.com.util.Constants;

import stranger.com.entities.Bullet;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

	private int WIDTH;
	private int HEIGHT;
	private ThemePanel theme;

	private javax.swing.Timer timer;
	private RobotPlayer player;
	private RobotVecna vecna;
	private java.util.List<Bullet> bullets = new ArrayList<Bullet>();

	private int score = 0;
	private int lives = 3;
	private boolean gameOver = false;

	// input
	private Set<Integer> keys = new HashSet<Integer>();

	public GamePanel(int w, int h, ThemePanel themePanel) {
		this.WIDTH = w;
		this.HEIGHT = h;
		this.theme = themePanel;
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setOpaque(false);
		setFocusable(true);
		addKeyListener(this);

		initEntities();
	}

	private void initEntities() {
		player = new RobotPlayer(WIDTH / 2 - Constants.PLAYER_WIDTH / 2, Constants.UPSIDE_DIVIDER_Y + 60);
		vecna = new RobotVecna(WIDTH / 2 - Constants.VECNA_WIDTH / 2, -120); // will spawn downward
	}

	public void start() {
		timer = new javax.swing.Timer(16, this);
		timer.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (gameOver) {
			repaint();
			return;
		}

		handleInput();
		player.update(WIDTH, HEIGHT);
		vecna.updateTowards(player, Constants.UPSIDE_DIVIDER_Y, WIDTH);

		// update bullets
		Iterator<Bullet> it = bullets.iterator();
		while (it.hasNext()) {
			Bullet b = it.next();
			b.update();
			if (!b.active) {
				it.remove();
				continue;
			}
			// only hit vecna if vecna visible in normal area
			if (vecna.isActive && b.getBounds().intersects(vecna.getBounds())) {
				vecna.takeDamage(b.damage);
				b.active = false;
				if (!vecna.isActive) {
					score += 150;
				}
			}
		}

		// vecna hunts children area simplified: if vecna overlaps player's vertical
		// area, it can hit player
		if (vecna.isActive && vecna.getBounds().intersects(player.getBounds())) {
			// damage player then knockback
			player.hit();
			vecna.knockBack();
			lives--;
			if (lives <= 0)
				gameOver = true;
		}

		repaint();
	}

	private void handleInput() {
		if (keys.contains(KeyEvent.VK_LEFT))
			player.moveLeft();
		if (keys.contains(KeyEvent.VK_RIGHT))
			player.moveRight();
		if (!keys.contains(KeyEvent.VK_LEFT) && !keys.contains(KeyEvent.VK_RIGHT))
			player.stop();
		// shoot handled on keyPressed to avoid auto-fire
	}

	@Override
	protected void paintComponent(Graphics g0) {
		// transparent overlay — draw HUD and entities
		super.paintComponent(g0);
		Graphics2D g = (Graphics2D) g0;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// draw player and enemy (player in normal area, vecna possibly straddling
		// divider)
		player.draw(g);
		vecna.draw(g, Constants.UPSIDE_DIVIDER_Y);

		// bullets
		for (Bullet b : bullets)
			b.draw(g);

		// HUD
		g.setFont(new Font("SansSerif", Font.BOLD, 14));
		g.setColor(Color.WHITE);
		g.drawString("Score: " + score, WIDTH - 150, 24);
		g.drawString("Lives: " + lives, WIDTH - 150, 44);

		if (gameOver) {
			g.setFont(new Font("SansSerif", Font.BOLD, 48));
			g.setColor(new Color(200, 30, 30, 200));
			String s = "GAME OVER";
			int sw = g.getFontMetrics().stringWidth(s);
			g.drawString(s, (WIDTH - sw) / 2, HEIGHT / 2 - 10);
		}
	}

	// input handlers
	@Override
	public void keyPressed(KeyEvent e) {
		int k = e.getKeyCode();
		keys.add(k);
		if (k == KeyEvent.VK_SPACE) {
			// shoot
			if (bullets.size() < Constants.MAX_BULLETS) {
				bullets.add(player.shoot());
			}
		}
		if (k == KeyEvent.VK_R) {
			if (gameOver) {
				gameOver = false;
				lives = 3;
				score = 0;
				initEntities();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys.remove(e.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
}
