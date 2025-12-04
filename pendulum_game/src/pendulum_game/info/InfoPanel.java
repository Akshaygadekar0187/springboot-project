package pendulum_game.info;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import java.text.DecimalFormat;
import java.awt.geom.RoundRectangle2D;


class InfoPanel extends JPanel {
    private final PendulumPanel pendulum;

    private final Font titleFont = new Font("Serif", Font.BOLD, 20);
    private final Font labelFont = new Font("SansSerif", Font.PLAIN, 16);
    private final Font formulaFont = new Font("SansSerif", Font.PLAIN, 16);
    private final Font numericFont = new Font("Monospaced", Font.BOLD, 16);

    private final DecimalFormat df4 = new DecimalFormat("0.0000");
    private final DecimalFormat df2 = new DecimalFormat("0.00");

    public InfoPanel(PendulumPanel pendulum) {
        this.pendulum = pendulum;
        setPreferredSize(new Dimension(900, 150));
        setBackground(new Color(245, 246, 250));
    }

    public void updateValues() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int margin = 15;

        // Background box
        RoundRectangle2D box = new RoundRectangle2D.Double(
                margin, margin, w - margin * 2, h - margin * 2, 20, 20);

        g.setColor(new Color(255, 255, 255));
        g.fill(box);
        g.setColor(new Color(210, 210, 220));
        g.setStroke(new BasicStroke(2f));
        g.draw(box);

        // Extract values
        double L = pendulum.getLengthMeters();
        double omega = pendulum.getOmega();
        double f = pendulum.getFrequency();
        double T = pendulum.getPeriod();
        double theta0 = pendulum.getInitialTheta();
        double theta = pendulum.getCurrentTheta();
        double t = pendulum.getTimeSeconds();

        // Columnes
        int col1 = margin + 30;          // label column
        int col2 = margin + 330;         // symbolic formula
        int col3 = margin + 650;         // numeric

        // Title
        g.setFont(titleFont);
        g.setColor(new Color(40, 40, 40));
        int y = margin + 35;
        g.drawString("Pendulum Formulas (Small-Angle Approximation)", col1, y);

        y += 25;

        //  Row spacing
        int rowGap = 30;

        // Draw rows
        drawRow(g, col1, col2, col3, y, "Angular frequency:", 
                "ω = √(g / l)", 
                "ω = " + df4.format(omega) + " rad/s");
        y += rowGap;

        drawRow(g, col1, col2, col3, y, "Frequency:", 
                "f = (1 / 2π) √(g / l)", 
                "f = " + df4.format(f) + " Hz");
        y += rowGap;

        drawRow(g, col1, col2, col3, y, "Time period:", 
                "T = 2π √(l / g)", 
                "T = " + df4.format(T) + " s");
        y += rowGap + 10;

        // Motion block header
        g.setFont(titleFont.deriveFont(18f));
        g.setColor(new Color(30, 30, 30));
        g.drawString("Motion:", col1, y);
        y += 22;

        // Motion formulas
        g.setFont(formulaFont);
        g.setColor(new Color(60, 60, 60));
        g.drawString("θ(t) = θ₀ cos(ωt)", col1, y);
        y += rowGap;

        g.setFont(labelFont);
        g.drawString("Initial angle θ₀: " + df2.format(Math.toDegrees(theta0)) + "°", col1, y);
        y += rowGap;

        g.drawString("Current θ(t): " + df2.format(Math.toDegrees(theta)) + "°   (t = " + df2.format(t) + "s)", col1, y);

        g.dispose();
    }

    private void drawRow(Graphics2D g, int col1, int col2, int col3, int y,
                         String label, String formula, String numeric) {

        g.setFont(labelFont);
        g.setColor(new Color(50, 50, 50));
        g.drawString(label, col1, y);

        g.setFont(formulaFont);
        g.setColor(new Color(30, 30, 30));
        g.drawString(formula, col2, y);

        g.setFont(numericFont);
        g.setColor(new Color(20, 20, 20));
        g.drawString(numeric, col3, y);
    }
}
