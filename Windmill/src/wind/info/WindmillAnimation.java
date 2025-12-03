package wind.info;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;

public class WindmillAnimation {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Windmill Animation");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            WindmillPanel panel = new WindmillPanel();

            // Slider to control speed (0 to 20)
            JSlider speedSlider = new JSlider(0, 20, 5);
            speedSlider.setMajorTickSpacing(5);
            speedSlider.setMinorTickSpacing(1);
            speedSlider.setPaintLabels(true);
            speedSlider.setPaintTicks(true);

            speedSlider.addChangeListener(e -> panel.setSpeed(speedSlider.getValue()));

            frame.add(panel, BorderLayout.CENTER);
            frame.add(speedSlider, BorderLayout.SOUTH);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}