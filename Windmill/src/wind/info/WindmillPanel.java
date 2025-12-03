package wind.info;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

class WindmillPanel extends JPanel implements ActionListener {

    private double angle = 0;  
    private int speed = 5;     

    private Timer timer;

    public WindmillPanel() {
        setPreferredSize(new Dimension(600, 600));
        setBackground(Color.BLACK);

        // Update every 16 ms (~60 FPS)
        timer = new Timer(16, this);
        timer.start();
    }

    public void setSpeed(int s) {
        this.speed = s;  // slider controls speed
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        angle += speed * 0.05; // adjust rotation speed smoothly
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawWindmill((Graphics2D) g);
    }

    private void drawWindmill(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        // Draw pole
        g2.setColor(Color.WHITE);
        g2.fillRect(cx - 10, cy, 20, 200);

        // Draw rotating blades
        g2.translate(cx, cy);  
        g2.rotate(angle);

        g2.setColor(Color.RED);
        drawBlade(g2);

        g2.rotate(Math.toRadians(120));
        g2.setColor(Color.GREEN);
        drawBlade(g2);

        g2.rotate(Math.toRadians(120));
        g2.setColor(Color.BLUE);
        drawBlade(g2);
    }

    private void drawBlade(Graphics2D g2) {
        Polygon blade = new Polygon();
        blade.addPoint(0, 0);
        blade.addPoint(150, -20);
        blade.addPoint(150, 20);

        g2.fillPolygon(blade);
    }
}

