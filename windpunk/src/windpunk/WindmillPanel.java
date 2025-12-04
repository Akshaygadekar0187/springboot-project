package windpunk;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

public class WindmillPanel extends JPanel implements ActionListener {

	private final int width;
	private final int height;
	private double angle = 0;
	private int speed = 8; // slider controlled
	private final Timer timer;

	private final Windmill windmill;
	private final List<Particle> particles = new ArrayList<>();
	private final List<Spark> sparks = new ArrayList<>();
	private final Random rnd = new Random();

	private final BackgroundScene bg;

	private boolean sparksEnabled = true;
	private int frameCount = 0;

	public WindmillPanel(int w, int h) {
		this.width = w;
		this.height = h;
		setPreferredSize(new Dimension(w, h));
		setBackground(new Color(12, 6, 25)); // deep night

		windmill = new Windmill(w / 2, (int) (h * 0.45));
		bg = new BackgroundScene(w, h);

		timer = new Timer(16, this); // ~60 FPS
		timer.start();
	}

	public void setSpeed(int s) {
		this.speed = s;
	}

	public void startAnimation() {
		if (!timer.isRunning())
			timer.start();
	}

	public void stopAnimation() {
		if (timer.isRunning())
			timer.stop();
	}

	public void toggleSparks() {
		sparksEnabled = !sparksEnabled;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		frameCount++;
		// update rotation angle
		angle += speed * 0.04; // base rotation
		windmill.setAngle(angle);
		windmill.setSpinSpeed(speed);

		// occasionally create sparks at blade tips when speed high
		if (sparksEnabled && rnd.nextDouble() < Math.min(0.12, speed / 60.0)) {
			Point2D tip = windmill.getRandomBladeTip();
			sparks.add(new Spark(tip.getX(), tip.getY()));
		}

		// particle spawn (wind) based on speed
		for (int i = 0; i < Math.max(1, speed / 6); i++) {
			particles.add(Particle.createWindParticle(width, height, speed));
		}

		// update particles
		particles.removeIf(p -> !p.update());

		// update sparks
		sparks.removeIf(s -> !s.update());

		// periodic lightning / neon pulses
		if (rnd.nextDouble() < 0.005) {
			bg.flashPulse();
		}

		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		// high quality
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		// draw background city
		bg.draw(g2);

		// draw ground shadow (soft ellipse)
		drawGroundShadow(g2);

		// motion blur technique: draw translucent trail by painting blades multiple
		// times with reduced alpha
		windmill.drawWithMotionBlur(g2);

		// draw neon rim/lighting on hub
		windmill.drawHub(g2);

		// draw sparks
		for (Spark s : sparks)
			s.draw(g2);

		// draw particles
		for (Particle p : particles)
			p.draw(g2);

		// draw neon HUD info (speed)
		drawHUD(g2);

		g2.dispose();
	}

	private void drawGroundShadow(Graphics2D g2) {
		int cx = width / 2;
		int cy = (int) (height * 0.65);
		int w = 280;
		int h = 60;
		RadialGradientPaint rg = new RadialGradientPaint(new Point2D.Double(cx, cy), Math.max(w, h) / 2f,
				new float[] { 0f, 1f }, new Color[] { new Color(0, 0, 0, 120), new Color(0, 0, 0, 0) });
		Composite old = g2.getComposite();
		g2.setPaint(rg);
		g2.fillOval(cx - w / 2, cy - h / 2, w, h);
		g2.setComposite(old);
	}

	private void drawHUD(Graphics2D g2) {
		String s = "Speed: " + speed;
		g2.setFont(new Font("Consolas", Font.PLAIN, 14));
		g2.setColor(new Color(180, 160, 255, 220));
		g2.drawString(s, 12, 20);

		// neon label
		g2.setColor(new Color(140, 40, 220, 150));
		g2.drawString("CYBERPUNK WINDMILL", 12, 38);
	}
}