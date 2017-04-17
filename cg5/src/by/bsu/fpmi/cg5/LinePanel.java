package by.bsu.fpmi.cg5;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Alexandra on 16.04.2017.
 */
public class LinePanel extends JPanel {

    private MainFrame parent;

    private JSpinner txX1, txY1, txX2, txY2;
    private JSlider sSize;
    private JComboBox<String> algo;

    public LinePanel(MainFrame parent){
        super();
        this.parent = parent;
        init();
    }

    private void init() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        algo = new JComboBox<>(new String[] {"Step", "DDA", "Bresenham"});
        algo.setSelectedIndex(0);

        sSize = new JSlider(0, PixelMatrix.MAX_SIZE, 10);
        sSize.setMajorTickSpacing(20);
        sSize.setMinorTickSpacing(5);
        sSize.setPaintTicks(true);
        sSize.setPaintLabels(true);

        SpinnerNumberModel numberModel = new SpinnerNumberModel(new Integer(0), new Integer(0), new Integer(PixelMatrix.MAX_SIZE - 1), new Integer(1));
        txX1 = new JSpinner(numberModel);
        numberModel = new SpinnerNumberModel(new Integer(0), new Integer(0), new Integer(PixelMatrix.MAX_SIZE - 1), new Integer(1));
        txY1 = new JSpinner(numberModel);
        numberModel = new SpinnerNumberModel(new Integer(0), new Integer(0), new Integer(PixelMatrix.MAX_SIZE - 1), new Integer(1));
        txX2 = new JSpinner(numberModel);
        numberModel = new SpinnerNumberModel(new Integer(0), new Integer(0), new Integer(PixelMatrix.MAX_SIZE - 1), new Integer(1));
        txY2 = new JSpinner(numberModel);

        JButton btGo = new JButton("Go");
        btGo.addActionListener(e -> go());

        this.add(new JLabel("Algorithm:"));
        this.add(algo);
        this.add(new JLabel("Size:"));
        this.add(sSize);
        this.add(new JLabel("X1:"));
        this.add(txX1);
        this.add(new JLabel("Y1:"));
        this.add(txY1);
        this.add(new JLabel("X2:"));
        this.add(txX2);
        this.add(new JLabel("Y2:"));
        this.add(txY2);
        this.add(btGo);

        JPanel fillPanel = new JPanel();
        fillPanel.setPreferredSize(new Dimension(200, 300));
        this.add(fillPanel);
    }

    private void go() {
        parent.drawLine(algo.getSelectedIndex(), sSize.getValue(), (Integer)txX1.getValue(), (Integer)txY1.getValue(), (Integer)txX2.getValue(), (Integer)txY2.getValue());
    }
}
