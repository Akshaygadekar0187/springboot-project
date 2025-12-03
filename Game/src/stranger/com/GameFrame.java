package stranger.com;

import javax.swing.*;
import stranger.com.util.Constants;
import java.awt.*;

public class GameFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Stranger Things — Corrupted Robot Demo");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setResizable(false);

                // layered pane: background (ThemePanel) + transparent game overlay (GamePanel)
                JLayeredPane layers = new JLayeredPane();
                layers.setPreferredSize(new Dimension(Constants.WIDTH, Constants.HEIGHT));

                ThemePanel theme = new ThemePanel(Constants.WIDTH, Constants.HEIGHT);
                theme.setBounds(0, 0, Constants.WIDTH, Constants.HEIGHT);

                GamePanel game = new GamePanel(Constants.WIDTH, Constants.HEIGHT, theme);
                game.setOpaque(false);
                game.setBounds(0, 0, Constants.WIDTH, Constants.HEIGHT);

                layers.add(theme, Integer.valueOf(0));
                layers.add(game, Integer.valueOf(1));

                frame.getContentPane().add(layers);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                theme.start();
                game.start();
                game.requestFocusInWindow();
            }
        });
    }
}

