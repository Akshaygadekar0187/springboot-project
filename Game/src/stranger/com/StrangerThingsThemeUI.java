package stranger.com;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class StrangerThingsThemeUI {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame f = new JFrame("Stranger Things — Themed UI");
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				ThemePanel panel = new ThemePanel(900, 600);
				f.add(panel);
				f.pack();
				f.setLocationRelativeTo(null);
				f.setVisible(true);
				panel.start();
			}
		});
	}
}
