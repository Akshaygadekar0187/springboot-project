package stranger.com;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;

class ThemePanel extends JPanel implements ActionListener, MouseMotionListener {

	private final int WIDTH;
	private final int HEIGHT;
	private final int DIVIDER_Y; // horizontal divider (normal / upside-down split)

	private javax.swing.Timer timer;
	private java.util.List<Particle> topParticles = new ArrayList<Particle>();
	private java.util.List<Particle> bottomParticles = new ArrayList<Particle>();
	private java.util.List<FogBlob> fogLayers = new ArrayList<FogBlob>();
	private java.util.List<Vine> vines = new ArrayList<Vine>();

	private Random rnd = new Random();

	// for subtle ripple animation
	private double ripplePhase = 0;

	// mouse for slight parallax
	private int mouseX = 0, mouseY = 0;

	public ThemePanel(int w, int h) {
		WIDTH = w;
		HEIGHT = h;
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.BLACK);
		setDoubleBuffered(true);

		DIVIDER_Y = HEIGHT / 2;

		// create particles and fog
		for (int i = 0; i < 70; i++)
			topParticles.add(new Particle(true, WIDTH, DIVIDER_Y, rnd));
		for (int i = 0; i < 90; i++)
			bottomParticles.add(new Particle(false, WIDTH, HEIGHT - DIVIDER_Y, rnd));
		for (int i = 0; i < 6; i++)
			fogLayers.add(new FogBlob(WIDTH, HEIGHT, rnd, i));
		// create vines hanging from divider into upside-down
		for (int i = 0; i < 7; i++) {
			int vx = 40 + i * (WIDTH - 80) / 6 + rnd.nextInt(40) - 20;
			vines.add(new Vine(vx, DIVIDER_Y - 6 + rnd.nextInt(10), DIVIDER_Y + 80 + rnd.nextInt(120), rnd));
		}

		addMouseMotionListener(this);
	}

	public void start() {
		timer = new javax.swing.Timer(16, this); // ~60 fps
		timer.start();
	}

	public Rectangle getNormalArea() {
		return new Rectangle(0, 0, WIDTH, DIVIDER_Y);
	}

	public Rectangle getUpsideDownArea() {
		return new Rectangle(0, DIVIDER_Y, WIDTH, HEIGHT - DIVIDER_Y);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// update
		for (Particle p : topParticles)
			p.update();
		for (Particle p : bottomParticles)
			p.update();
		for (FogBlob f : fogLayers)
			f.update();
		for (Vine v : vines)
			v.update();

		ripplePhase += 0.04;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g0) {
		super.paintComponent(g0);
		Graphics2D g = (Graphics2D) g0;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// slight parallax offsets based on mouse
		int px = (mouseX - WIDTH / 2) / 40;
		int py = (mouseY - DIVIDER_Y) / 80;

		// draw top (Normal world)
		paintNormalWorld(g, px, py);

		// divider (slightly glowing)
		paintDivider(g);

		// draw vines hanging and crossing divider
		for (Vine v : vines)
			v.draw(g);

		// draw bottom (Upside Down) using mirrored rendering
		paintUpsideDown(g, px, py);

		// overlay fog and particles (layers)
		for (FogBlob f : fogLayers)
			f.draw(g);
		for (Particle p : topParticles)
			p.draw(g);
		for (Particle p : bottomParticles)
			p.draw(g);

		// draw logo on top center
		drawGlowingTitle(g);

		// small caption / controls hint
		drawFooter(g);
	}

	private void paintNormalWorld(Graphics2D g, int px, int py) {
		// background gradient (red/orange)
		GradientPaint gp = new GradientPaint(0, 0, new Color(24, 6, 12), 0, DIVIDER_Y, new Color(120, 30, 20));
		g.setPaint(gp);
		g.fillRect(0, 0, WIDTH, DIVIDER_Y);

		// distant tree silhouettes (simple triangles and strokes)
		int rows = 9;
		for (int i = 0; i < rows; i++) {
			int tx = -50 + (i * (WIDTH + 100) / rows) + px * 2
					+ (int) (Math.sin(i * 0.7 + System.currentTimeMillis() / 1500.0) * 10);
			int baseY = DIVIDER_Y - 40 + py;
			paintTreeSilhouette(g, tx, baseY, 140 + (i % 2) * 30, 220 + (i % 3) * 10, new Color(20, 8, 8));
		}

		// subtle glow near horizon
		RadialGradientPaint rg = new RadialGradientPaint(new Point2D.Double(WIDTH / 2 + px * 3, DIVIDER_Y - 30 + py),
				220, new float[] { 0f, 1f }, new Color[] { new Color(220, 60, 30, 90), new Color(0, 0, 0, 0) });
		g.setPaint(rg);
		g.fillRect(0, 0, WIDTH, DIVIDER_Y);

		// add some fog-ish ellipses
		Composite old = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.12f));
		g.setColor(new Color(255, 220, 180));
		for (int i = 0; i < 8; i++) {
			int fx = (i * 137 + (int) (System.currentTimeMillis() / 50)) % (WIDTH + 200) - 80 + px * 4;
			int fy = DIVIDER_Y - 60 - (i % 3) * 20 + py;
			g.fillOval(fx - 120, fy - 40, 260, 120);
		}
		g.setComposite(old);
	}

	private void paintDivider(Graphics2D g) {
		// dark band with glow
		g.setColor(new Color(15, 15, 15, 200));
		g.fillRect(0, DIVIDER_Y - 6, WIDTH, 12);

		// subtle red glow above and teal glow below
		GradientPaint glowTop = new GradientPaint(0, DIVIDER_Y - 30, new Color(200, 30, 30, 90), 0, DIVIDER_Y,
				new Color(0, 0, 0, 0));
		g.setPaint(glowTop);
		g.fillRect(0, DIVIDER_Y - 60, WIDTH, 60);

		GradientPaint glowBot = new GradientPaint(0, DIVIDER_Y, new Color(0, 0, 0, 0), 0, DIVIDER_Y + 40,
				new Color(50, 120, 150, 70));
		g.setPaint(glowBot);
		g.fillRect(0, DIVIDER_Y, WIDTH, 60);
	}

	private void paintUpsideDown(Graphics2D g, int px, int py) {
		// We'll render a mirrored scene: create an offscreen image for the top and draw
		// it flipped with distortion
		BufferedImage topImg = new BufferedImage(WIDTH, DIVIDER_Y, BufferedImage.TYPE_INT_ARGB);
		Graphics2D tg = topImg.createGraphics();
		tg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// draw an inverted palette version of the top into topImg
		GradientPaint gp = new GradientPaint(0, 0, new Color(8, 18, 40), 0, DIVIDER_Y, new Color(20, 40, 70));
		tg.setPaint(gp);
		tg.fillRect(0, 0, WIDTH, DIVIDER_Y);

		// draw inverted tree silhouettes (but a little different)
		int rows = 9;
		for (int i = 0; i < rows; i++) {
			int tx = -50 + (i * (WIDTH + 100) / rows) - px * 2
					+ (int) (Math.cos(i * 0.9 + System.currentTimeMillis() / 1300.0) * 10);
			int baseY = DIVIDER_Y - 40;
			paintTreeSilhouette(tg, tx, baseY, 140 + (i % 2) * 30, 220 + (i % 3) * 10, new Color(6, 20, 30));
		}

		// add downward-floating particles (inverted)
		tg.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.18f));
		for (int i = 0; i < 30; i++) {
			int pxo = (int) (Math.abs(Math.sin(i + System.currentTimeMillis() / 900.0)) * WIDTH) % WIDTH;
			int pyc = 20 + (i * 37) % DIVIDER_Y;
			tg.setColor(new Color(80, 200, 200, 30));
			tg.fillOval(pxo, pyc, 8, 8);
		}
		tg.dispose();

		// flip vertically: draw mirrored topImg into the bottom area with wavy ripple
		BufferedImage rippled = rippledImage(topImg, ripplePhase);

		// draw the rippled image starting at DIVIDER_Y
		g.drawImage(rippled, 0, DIVIDER_Y, null);

		// darken and tint for Upside Down feel
		Composite old = g.getComposite();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.28f));
		g.setColor(new Color(10, 40, 50));
		g.fillRect(0, DIVIDER_Y, WIDTH, DIVIDER_Y);
		g.setComposite(old);

		// draw some hanging inverted roots/vines darker overlay
		for (Vine v : vines)
			v.drawUpsideDownOverlay(g);
	}

	// draw simple stylized tree silhouette
	private void paintTreeSilhouette(Graphics2D g, int baseX, int baseY, int w, int h, Color color) {
		Composite old = g.getComposite();
		g.setComposite(AlphaComposite.SrcOver);
		g.setColor(color);

		// trunk
		int trunkW = Math.max(6, w / 12);
		g.fillRect(baseX + w / 2 - trunkW / 2, baseY - trunkW / 2, trunkW, trunkW + 60);

		// layers of triangular branches
		Path2D tri = new Path2D.Double();
		tri.moveTo(baseX, baseY);
		tri.lineTo(baseX + w / 2, baseY - h);
		tri.lineTo(baseX + w, baseY);
		tri.closePath();
		g.fill(tri);

		// add some jagged thin branches
		g.setStroke(new BasicStroke(2));
		g.setColor(new Color(0, 0, 0, 60));
		for (int i = 0; i < 6; i++) {
			int bx = baseX + 10 + i * (w / 6);
			int by = baseY - 10 - (i % 3) * 12;
			g.drawLine(bx, by, bx - 20 + (i % 2) * 40, by - 12);
		}
		g.setComposite(old);
	}

	// create ripple distortion by shifting each horizontal scan line with sinus
	// wave
	private BufferedImage rippledImage(BufferedImage src, double phase) {
		int w = src.getWidth(), h = src.getHeight();
		BufferedImage dst = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		int amplitude = 6;
		double freq = 2.5; // waves across width

		for (int y = 0; y < h; y++) {
			int shift = (int) (Math.sin((y / (double) h) * Math.PI * freq + phase) * amplitude);
			// copy scanline
			for (int x = 0; x < w; x++) {
				int sx = x + shift;
				if (sx < 0 || sx >= w)
					continue;
				int rgb = src.getRGB(x, y);
				dst.setRGB(sx, y, rgb);
			}
		}

		// flip vertically
		AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
		tx.translate(0, -h);
		BufferedImage flipped = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = flipped.createGraphics();
		g2.drawRenderedImage(dst, tx);
		g2.dispose();

		// apply slight blur-like translucent overlay to simulate water blur
		Graphics2D g = flipped.createGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06f));
		g.setColor(Color.WHITE);
		for (int i = 0; i < 6; i++) {
			g.fillRoundRect(-10 + i * 3, i * 4, w + 40, 12, 30, 30);
		}
		g.dispose();
		return flipped;
	}

	private void drawGlowingTitle(Graphics2D g) {
		String title = "STRANGER THINGS";
		Font base = new Font("Serif", Font.BOLD, 46);
		g.setFont(base);

		FontMetrics fm = g.getFontMetrics(base);
		int sw = fm.stringWidth(title);
		int x = (WIDTH - sw) / 2;
		int y = 90;

		// glow effect: draw multiple layers
		for (int i = 12; i >= 1; i--) {
			int alpha = Math.max(8, 50 - i * 3);
			g.setColor(new Color(200, 30, 30, alpha));
			g.drawString(title, x - i / 2, y - i / 3);
		}

		// main text
		g.setColor(new Color(255, 160, 100));
		g.drawString(title, x, y);

		// small subtitle
		Font sub = new Font("SansSerif", Font.PLAIN, 14);
		g.setFont(sub);
		String subtext = "UPSIDE DOWN — PROTECT THE CHILDREN";
		g.setColor(new Color(220, 220, 220, 160));
		g.drawString(subtext, (WIDTH - g.getFontMetrics(sub).stringWidth(subtext)) / 2, y + 28);
	}

	private void drawFooter(Graphics2D g) {
		String s = "Use arrow keys to move. Space to interact. (UI preview)";
		Font f = new Font("SansSerif", Font.PLAIN, 12);
		g.setFont(f);
		g.setColor(new Color(200, 200, 200, 140));
		g.drawString(s, 14, HEIGHT - 12);
	}

	// small classes ----------------------------------------------------

	private static class Particle {
		double x, y;
		double vx, vy;
		float alpha;
		int size;
		boolean top;
		int boundW, boundH;
		Random rnd;

		void reset() {
			size = 4 + rnd.nextInt(8);
			x = rnd.nextInt(boundW);
			y = (top ? rnd.nextInt(boundH) : rnd.nextInt(boundH));
			vx = (rnd.nextDouble() - 0.5) * 0.6;
			vy = (top ? (0.2 + rnd.nextDouble() * 1.2) : (0.1 + rnd.nextDouble() * 0.6));
			alpha = 0.08f + rnd.nextFloat() * 0.7f;
		}

		Particle(boolean top, int panelW, int panelH, Random r) {
			this(top, panelW, panelH, r, null);
		}

		Particle(boolean top, int panelW, int panelH, Random r, Object ignored) {
			rnd = r;
			this.top = top;
			boundW = panelW;
			boundH = panelH;
			reset();
		}

		void update() {
			x += vx;
			y += (top ? -vy * 0.6 : vy * 0.6); // top floats upward slightly, bottom drifts downward/up
			// wrap
			if (x < -20)
				x = boundW + 20;
			if (x > boundW + 20)
				x = -20;
			if (y < -50 || y > boundH + 120) {
				// respawn
				x = rnd.nextInt(boundW);
				if (top)
					y = boundH + rnd.nextInt(60);
				else
					y = -rnd.nextInt(40);
			}
		}

		void draw(Graphics2D g) {
			Composite old = g.getComposite();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			if (top)
				g.setColor(new Color(255, 200, 120));
			else
				g.setColor(new Color(120, 220, 220));
			g.fillOval((int) x, (int) y + (top ? 0 : (int) (boundH)), size, size);
			g.setComposite(old);
		}
	}

	private static class FogBlob {
		double x, y, w, h;
		float alpha;
		double speed;
		int panelW, panelH;
		Random rnd;
		Color color;

		FogBlob(int panelW, int panelH, Random rnd, int seed) {
			this.panelW = panelW;
			this.panelH = panelH;
			this.rnd = rnd;
			this.x = rnd.nextInt(panelW);
			this.y = rnd.nextInt(panelH);
			this.w = 200 + rnd.nextInt(300);
			this.h = 80 + rnd.nextInt(140);
			this.alpha = 0.06f + rnd.nextFloat() * 0.14f;
			this.speed = 0.1 + rnd.nextDouble() * 0.3 + seed * 0.02;
			if (seed % 2 == 0)
				this.color = new Color(220, 180, 120, 200);
			else
				this.color = new Color(80, 160, 160, 200);
		}

		void update() {
			x += Math.sin(System.currentTimeMillis() / 4000.0 + x) * 0.2;
			y += Math.cos(System.currentTimeMillis() / 6000.0 + x) * 0.06;
		}

		void draw(Graphics2D g) {
			Composite old = g.getComposite();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			g.setColor(color);
			g.fillOval((int) (x - w / 2), (int) (y - h / 2), (int) w, (int) h);
			g.setComposite(old);
		}
	}

	private static class Vine {
		int startX, startY;
		int endY;
		double sway;
		double phase;
		Random rnd;

		Vine(int x, int startY, int endY, Random rnd) {
			this.startX = x;
			this.startY = startY;
			this.endY = endY;
			this.rnd = rnd;
			this.sway = 20 + rnd.nextInt(30);
			this.phase = rnd.nextDouble() * Math.PI * 2;
		}

		void update() {
			phase += 0.01 + (rnd.nextDouble() * 0.02);
			sway = 14 + Math.sin(phase * 0.7) * 10;
		}

		void draw(Graphics2D g) {
			// hanging roots from divider into normal/upside
			Path2D path = new Path2D.Double();
			path.moveTo(startX, startY);
			double midX = startX + Math.sin(phase) * sway;
			double midY = startY + (endY - startY) * 0.4;
			double ctlX = startX + Math.cos(phase * 1.3) * (sway * 0.8);
			path.curveTo((startX + ctlX) / 2, startY + 10, midX, midY, startX, endY - 8);
			g.setStroke(new BasicStroke(3f));
			g.setColor(new Color(40, 20, 30, 200));
			g.draw(path);

			// small tendrils
			for (int t = 0; t < 3; t++) {
				double tphase = phase + t;
				int tx = (int) (startX + Math.sin(tphase + t) * (10 + t * 6));
				int ty = (int) (startY + 30 + t * 20 + Math.cos(tphase) * 10);
				g.setStroke(new BasicStroke(1.2f));
				g.drawLine(tx, ty, tx - 10 - t * 6, ty + 8 + t * 4);
			}
		}

		void drawUpsideDownOverlay(Graphics2D g) {
			// draw darker inverted roots / reflection
			Path2D path = new Path2D.Double();
			path.moveTo(startX, startY + 6);
			double midX = startX - Math.sin(phase) * sway * 0.6;
			double midY = startY + (endY - startY) * 0.6;
			path.curveTo(startX - 10, startY + 20, midX, midY, startX, endY + 30);
			g.setStroke(new BasicStroke(2f));
			g.setColor(new Color(10, 40, 50, 160));
			g.draw(path);
		}
	}

	// mouse for tiny parallax
	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	
}