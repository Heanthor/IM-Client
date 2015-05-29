package tests;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GridBagProportions {
    static void buildAndShowWindow() {
        JTextField small = new JTextField("small (0.8)", 5);
        JTextField large = new JTextField("LARGE (0.2)", 30);

        small.setMinimumSize(small.getPreferredSize());
        large.setMinimumSize(large.getPreferredSize());

        JPanel panel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.left = 6;
        gbc.insets.top = 6;
        gbc.insets.bottom = 6;

        gbc.weightx = 0.8;
        panel.add(small, gbc);

        gbc.weightx = 0.2;
        gbc.insets.right = 6;
        panel.add(large, gbc);

        JFrame frame = new JFrame("GridBagLayout Proportions");
        frame.getContentPane().add(panel);
        frame.pack();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                buildAndShowWindow();
            }
        });
    }
}