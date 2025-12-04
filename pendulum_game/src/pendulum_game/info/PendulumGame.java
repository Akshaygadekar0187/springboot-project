package pendulum_game.info;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PendulumGame extends JFrame {
	public PendulumGame() {
		setTitle("Educational Pendulum — Formulas & Simulation");
		setSize(900, 700);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		setLayout(new BorderLayout());
		PendulumPanel pendulumPanel = new PendulumPanel();
		add(pendulumPanel, BorderLayout.CENTER);

		ControlsPanel controls = new ControlsPanel(pendulumPanel);
		add(controls, BorderLayout.NORTH);

		InfoPanel info = new InfoPanel(pendulumPanel);
		add(info, BorderLayout.SOUTH);

		// Let panels know about each other
		pendulumPanel.setInfoPanel(info);
		controls.setInfoPanel(info);

		setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(PendulumGame::new);
	}
}