package pendulum_game.info;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

class ControlsPanel extends JPanel {
	private final PendulumPanel pendulum;
	private InfoPanel infoPanel;

	public ControlsPanel(PendulumPanel pendulum) {
		this.pendulum = pendulum;
		setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));
		setBackground(new Color(240, 242, 245));
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 230)));

		add(new JLabel("Initial Angle (degrees):"));
		pendulum.angleField.setColumns(6);
		add(pendulum.angleField);

		add(new JLabel("Length (meters):"));
		pendulum.lengthField.setColumns(6);
		add(pendulum.lengthField);

		add(new JLabel("Pixels per meter:"));
		pendulum.scaleSlider.setPreferredSize(new Dimension(200, 40));
		add(pendulum.scaleSlider);

		add(pendulum.startBtn);
		add(pendulum.pauseBtn);
		add(pendulum.resetBtn);

		// brief tip label
		JLabel tip = new JLabel("Use small angle (<= 20°) for best accuracy; formula uses small-angle approximation.");
		tip.setForeground(new Color(85, 85, 90));
		add(tip);
	}

	public void setInfoPanel(InfoPanel info) {
		this.infoPanel = info;
	}
}
