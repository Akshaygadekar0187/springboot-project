package windpunk;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

public class WindmillCyberpunk {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Windmill — Cyberpunk Neon City");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setResizable(false);


			WindmillPanel panel = new WindmillPanel(900, 600);


			// Controls
			JButton startBtn = new JButton("Start");
			startBtn.addActionListener(e -> panel.startAnimation());


			JButton stopBtn = new JButton("Stop");
			stopBtn.addActionListener(e -> panel.stopAnimation());


			JButton sparksBtn = new JButton("Toggle Sparks");
			sparksBtn.addActionListener(e -> panel.toggleSparks());


			JSlider speedSlider = new JSlider(0, 30, 8);
			speedSlider.setMajorTickSpacing(10);
			speedSlider.setMinorTickSpacing(1);
			speedSlider.setPaintTicks(true);
			speedSlider.setPaintLabels(true);
			speedSlider.addChangeListener(e -> panel.setSpeed(speedSlider.getValue()));


			JPanel controls = new JPanel();
			controls.add(startBtn);
			controls.add(stopBtn);
			controls.add(sparksBtn);
			controls.add(new JLabel("Speed:"));
			controls.add(speedSlider);


			frame.add(panel, BorderLayout.CENTER);
			frame.add(controls, BorderLayout.SOUTH);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);


			panel.requestFocusInWindow();
			});
			}
			}