package by.bsu.fpmi.cg5;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Alexandra on 16.04.2017.
 */
public class MainFrame extends JFrame {

    private PixelMatrix matrix;
    private SettingsPanel settingsPanel;
    private ImagePanel imagePanel;

    public MainFrame() {
        super();
        init();
    }

    private void init() {
        setTitle("CG Lab 5");
        setMinimumSize(new Dimension(900, 700));
        setResizable(false);
        setLayout(new FlowLayout(FlowLayout.LEADING));

        matrix = new PixelMatrix();

        settingsPanel = new SettingsPanel(this);
        add(settingsPanel);
        imagePanel = new ImagePanel(matrix);
        add(imagePanel);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void drawCircle(int size, int r, int x, int y) {
        matrix.setSize(size);
        matrix.calculateCircle(x, y, r);
        imagePanel.repaint();
    }

    public void drawLine(int algo, int size, int x1, int y1, int x2, int y2) {
        matrix.setSize(size);
        matrix.calculateLine(algo, x1, y1, x2, y2);
        imagePanel.repaint();
    }
}
