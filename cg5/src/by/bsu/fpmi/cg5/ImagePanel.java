package by.bsu.fpmi.cg5;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Alexandra on 16.04.2017.
 */
public class ImagePanel extends JPanel {

    private class Point {
        public int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private PixelMatrix matrix;

    public ImagePanel(PixelMatrix matrix) {
        super();
        this.matrix = matrix;
        this.setPreferredSize(new Dimension(600, 600));
        //this.setBackground(Color.red);
    }

    private int getStep() {
        return this.getWidth() / matrix.getSize();
    }

    private Point getCenterPosition(int i, int j) {
        return new Point(i * getStep() + getStep() / 2, j * getStep() + getStep() / 2);
    }

    private Point getStartPosition(int i, int j) {
        return new Point(i * getStep(), j * getStep());
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        if (matrix.getSize() > 0) {
            for (int i = 0; i < matrix.getSize(); i++) {
                for (int j = 0; j < matrix.getSize(); j++) {
                    if (matrix.hasPixel(i, j)) {
                        Point p = getStartPosition(i, j);
                        g.setColor(Color.blue);
                        g.fillRect(p.x, p.y, getStep(), getStep());
                    } else {
                        Point p = getCenterPosition(i, j);
                        g.setColor(Color.black);
                        g.fillRect(p.x, p.y, 1, 1);
                    }
                }
            }
            if (matrix.isCircle()) {
                g.setColor(Color.red);
                Point p = getCenterPosition(matrix.x1 - matrix.r, matrix.y1 - matrix.r);
                g.drawOval(p.x, p.y, getStep() * matrix.r * 2, getStep() * matrix.r * 2);
            } else {
                g.setColor(Color.red);
                Point p1 = getCenterPosition(matrix.x1, matrix.y1);
                Point p2 = getCenterPosition(matrix.x2, matrix.y2);
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }
}
