package by.bsu.fpmi.cg5;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Alexandra on 16.04.2017.
 */
public class CirclePanel extends JPanel {

    private MainFrame parent;

    private JSpinner txR, txX, txY;
    private JSlider sSize;

    public CirclePanel(MainFrame parent){
        super();
        this.parent = parent;
        init();
    }

    private void init() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        sSize = new JSlider(0, PixelMatrix.MAX_SIZE, 10);
        sSize.setMajorTickSpacing(20);
        sSize.setMinorTickSpacing(5);
        sSize.setPaintTicks(true);
        sSize.setPaintLabels(true);

        SpinnerNumberModel numberModel = new SpinnerNumberModel(new Integer(0), new Integer(0), new Integer(PixelMatrix.MAX_SIZE - 1), new Integer(1));
        txR = new JSpinner(numberModel);
        numberModel = new SpinnerNumberModel(new Integer(0), new Integer(0), new Integer(PixelMatrix.MAX_SIZE - 1), new Integer(1));
        txX = new JSpinner(numberModel);
        numberModel = new SpinnerNumberModel(new Integer(0), new Integer(0), new Integer(PixelMatrix.MAX_SIZE - 1), new Integer(1));
        txY = new JSpinner(numberModel);

        JButton btGo = new JButton("Go");
        btGo.addActionListener(e -> go());

        this.add(new JLabel("Size:"));
        this.add(sSize);
        this.add(new JLabel("Radius:"));
        this.add(txR);
        this.add(new JLabel("Center X:"));
        this.add(txX);
        this.add(new JLabel("Center Y:"));
        this.add(txY);
        this.add(btGo);

        JPanel fillPanel = new JPanel();
        fillPanel.setPreferredSize(new Dimension(200, 400));
        this.add(fillPanel);
    }

    private void go() {
        parent.drawCircle(sSize.getValue(), (Integer)txR.getValue(), (Integer)txX.getValue(), (Integer)txY.getValue());
    }
}
