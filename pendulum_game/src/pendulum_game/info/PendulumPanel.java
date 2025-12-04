package pendulum_game.info;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class PendulumPanel extends JPanel implements ActionListener {

	    private final double g = 9.8;            // gravitational acceleration (m/s^2)
	    private double lengthMeters = 1.0;       // length in meters (user input)
	    private double theta0 = Math.toRadians(30); // initial angle (radians)
	    private double time = 0.0;               // simulation time (s)
	    private boolean running = false;

	    // Rendering scale
	    private int pixelsPerMeter = 220;        // slider controlled

	    // Timer for animation
	    private final Timer timer;
	
	    // Damping settings
	    private double dampingFactor = 0.9993;   // how fast amplitude decreases
	    private double minAmplitude = 0.0005;    // threshold to stop
	    private boolean stopped = false;
	    // Controls reference for inputs (kept simple)
	    JTextField angleField;
	    JTextField lengthField;
	    JSlider scaleSlider;

	    // Buttons
	    JButton startBtn;
	    JButton pauseBtn;
	    JButton resetBtn;

	    // Info panel reference to update computed values
	    private InfoPanel infoPanel;
	    
	    public PendulumPanel() {
	        setBackground(new Color(245, 245, 250));
	        setLayout(null);

	        // Input fields (placed off the canvas visually, but accessible in this panel)
	        angleField = new JTextField("30");
	        lengthField = new JTextField("1.0");

	        // Buttons
	        startBtn = new JButton("Start");
	        pauseBtn = new JButton("Pause");
	        resetBtn = new JButton("Reset");

	        // Place controls in top-left corner (they will be visually subtle)
	        angleField.setBounds(16, 12, 70, 26);
	        lengthField.setBounds(96, 12, 80, 26);
	        startBtn.setBounds(190, 12, 80, 26);
	        pauseBtn.setBounds(280, 12, 80, 26);
	        resetBtn.setBounds(370, 12, 80, 26);

	        // Add to panel so they respond to events (but we won't show labels here)
	        add(angleField);
	        add(lengthField);
	        add(startBtn);
	        add(pauseBtn);
	        add(resetBtn);

	        // Slider for pixels-per-meter (visual scale)
	        scaleSlider = new JSlider(50, 400, pixelsPerMeter);
	        scaleSlider.setBounds(16, 44, 300, 40);
	        scaleSlider.setMajorTickSpacing(50);
	        scaleSlider.setPaintTicks(true);
	        scaleSlider.setPaintLabels(false);
	        add(scaleSlider);

	        // Action listeners
	        startBtn.addActionListener(e -> startSimulation());
	        pauseBtn.addActionListener(e -> pauseSimulation());
	        resetBtn.addActionListener(e -> resetSimulation());

	        scaleSlider.addChangeListener((ChangeEvent e) -> {
	            pixelsPerMeter = scaleSlider.getValue();
	            repaint();
	            if (infoPanel != null) infoPanel.updateValues();
	        });

	        // Timer at ~60 FPS, step corresponds to dt seconds
	        timer = new Timer(16, this);
	    }

	    public void setInfoPanel(InfoPanel info) {
	        this.infoPanel = info;
	    }

	    // Call when Start is clicked (parses inputs)
	    private void startSimulation() {
	        try {
	            double angleDeg = Double.parseDouble(angleField.getText().trim());
	            double lMeters = Double.parseDouble(lengthField.getText().trim());

	            if (lMeters <= 0) {
	                JOptionPane.showMessageDialog(this, "Length must be > 0 (meters).");
	                return;
	            }

	            theta0 = Math.toRadians(angleDeg);
	            lengthMeters = lMeters;
	            // reset time so motion uses fresh initial condition
	            time = 0.0;
	            running = true;
	            timer.start();
	            if (infoPanel != null) infoPanel.updateValues();
	        } catch (NumberFormatException ex) {
	            JOptionPane.showMessageDialog(this, "Please enter numeric values for angle and length.");
	        }
	    }

	    private void pauseSimulation() {
	        running = false;
	        timer.stop();
	    }

	    private void resetSimulation() {
	        running = false;
	        timer.stop();
	        time = 0.0;
	        theta0 = Math.toRadians(30);
	        lengthMeters = 1.0;
	        angleField.setText("30");
	        lengthField.setText("1.0");
	        scaleSlider.setValue(220);
	        pixelsPerMeter = 220;
	        if (infoPanel != null) infoPanel.updateValues();
	        repaint();
	    }

	    @Override
	    public void actionPerformed(ActionEvent e) {
	        // increment time by dt (seconds) — choose dt consistent with timer (approx 0.016s)
	        time += 0.016;
	        repaint();
	        if (infoPanel != null) infoPanel.updateValues();
	    }

	    // Computes omega, frequency, period, and current angle
	    public double getOmega() {
	        return Math.sqrt(g / lengthMeters);
	    }

	    public double getFrequency() {
	        return getOmega() / (2.0 * Math.PI);
	    }

	    public double getPeriod() {
	        return 2.0 * Math.PI * Math.sqrt(lengthMeters / g);
	    }

	    public double getCurrentTheta() {
	        double omega = getOmega();
	        return theta0 * Math.cos(omega * time); // radians
	    }

	    public double getInitialTheta() {
	        return theta0;
	    }

	    public double getLengthMeters() {
	        return lengthMeters;
	    }

	    public int getPixelsPerMeter() {
	        return pixelsPerMeter;
	    }

	    public double getTimeSeconds() {
	        return time;
	    }
	    @Override
	    protected void paintComponent(Graphics g0) {
	        super.paintComponent(g0);
	        Graphics2D g = (Graphics2D) g0.create();
	        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	        int w = getWidth();
	        int h = getHeight();

	        // Pivot position (top center-ish)
	        int pivotX = w / 2;
	        int pivotY = 110;

	        // Draw decorative background subtle gradient
	        Paint old = g.getPaint();
	        GradientPaint bg = new GradientPaint(0, 0, new Color(250, 252, 255),
	                0, h, new Color(235, 238, 245));
	        g.setPaint(bg);
	        g.fillRect(0, 0, w, h);
	        g.setPaint(old);

	        // Draw top bar
	        int barWidth = 320;
	        int barHeight = 18;
	        int bleft = pivotX - barWidth / 2;
	        int btop = pivotY - 28;

	        // Wood bar base
	        g.setColor(new Color(120, 70, 25));
	        g.fillRoundRect(bleft, btop, barWidth, barHeight, 12, 12);
	        // glossy strip
	        g.setColor(new Color(180, 120, 70));
	        g.fillRoundRect(bleft, btop, barWidth, 6, 8, 8);

	        // Compute pendulum rendering length in pixels
	        double renderLengthPx = lengthMeters * pixelsPerMeter;

	        // Compute physics values
	        double omega = getOmega();
	        double theta = getCurrentTheta(); // radians

	        // Bob coordinates
	        int bobX = pivotX + (int) (renderLengthPx * Math.sin(theta));
	        int bobY = pivotY + (int) (renderLengthPx * Math.cos(theta));

	        // Draw rope
	        g.setStroke(new BasicStroke(3f));
	        g.setColor(new Color(70, 40, 20));
	        g.drawLine(pivotX, pivotY, bobX, bobY);

	        // Slight curved rope shadow near bar (to show attachment)
	        g.setStroke(new BasicStroke(1.2f));
	        g.setColor(new Color(100, 70, 40, 160));
	        g.drawLine(pivotX - 8, pivotY - 6, pivotX + 8, pivotY - 6);

	        // Draw shadow on ground below bob
	        int shadowY = getHeight() - 90;
	        int shadowW = 80;
	        int shadowH = 22;
	        g.setColor(new Color(0, 0, 0, 60));
	        g.fillOval(bobX - shadowW/2, shadowY, shadowW, shadowH);

	        // Draw bob with radial-like gradient (approx with two ovals)
	        int bobR = 36; // radius of bob
	        // outer glossy circle
	        g.setPaint(new GradientPaint(bobX - bobR, bobY - bobR, new Color(220, 55, 55),
	                bobX + bobR, bobY + bobR, new Color(140, 10, 10)));
	        g.fillOval(bobX - bobR, bobY - bobR, bobR * 2, bobR * 2);

	        // highlight
	        g.setColor(new Color(255, 180, 180, 120));
	        g.fillOval(bobX - bobR/2, bobY - bobR/2 - 8, bobR, bobR/2);

	        // bob border
	        g.setStroke(new BasicStroke(2f));
	        g.setColor(new Color(60, 20, 20));
	        g.drawOval(bobX - bobR, bobY - bobR, bobR * 2, bobR * 2);

	        // Draw pivot knob
	        g.setColor(new Color(60, 60, 60));
	        g.fillOval(pivotX - 6, pivotY - 6, 12, 12);

	        // Draw connecting ring (for style)
	        g.setColor(new Color(160, 160, 160));
	        g.drawOval(bobX - 8, bobY - bobR - 6, 16, 10);

	        // Text overlay for inputs (top-left)
	        g.setColor(new Color(30, 30, 30));
	        g.setFont(new Font("SansSerif", Font.BOLD, 14));
	        g.drawString("Initial Angle (θ₀): " + String.format("%.1f", Math.toDegrees(theta0)) + "°", 16, 22);
	        g.drawString("Length (l): " + String.format("%.3f", lengthMeters) + " m", 16, 42);
	        g.drawString("Scale: " + pixelsPerMeter + " px/m", 16, 62);
	        g.drawString("Render length: " + String.format("%.0f", renderLengthPx) + " px", 16, 82);

	        // Draw small live readouts near bob
	        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
	        String bobLabel = String.format("θ(t)=%.2f°", Math.toDegrees(theta));
	        g.setColor(new Color(20, 20, 20));
	        g.drawString(bobLabel, bobX + 10, bobY - 10);

	        // time
	        g.drawString(String.format("t = %.2f s", time), 16, 102);

	        g.dispose();
	    }
	}
