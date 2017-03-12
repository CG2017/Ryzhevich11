package by.bsu.fpmi.cg;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Alexandra on 04.03.2017.
 */
public class Frame extends JFrame {

    private BufferedImage img, initImg;
    private JLabel imgLabel;

    private JPanel fromPanel, toPanel, imgPanel, settingsPanel;
    private JSpinner txSensitivity, txFromRed, txFromGreen, txFromBlue, txToRed, txToGreen, txToBlue, txLWeight, txAWeight, txBWeight;
    private JSlider sSensitivity, sFromRed, sFromGreen, sFromBlue, sToRed, sToGreen, sToBlue, sLWeight, sAWeight, sBWeight;

    private Color fromColor = new Color(0, 0, 0), toColor = new Color(0, 0, 0);
    private double[] weight = {1, 1, 1};
    private double sensitivity = 30;

    public Frame() {
        super();
        init();
    }

    private void init() {
        this.setTitle("CG Lab2");
        this.setSize(1000, 500);
        this.setLayout(new FlowLayout(FlowLayout.LEADING));

        JMenuBar jbar = new JMenuBar();
        JMenu jmenu = new JMenu("File");
        JMenuItem jmi = new JMenuItem("Open");
        jmi.addActionListener(ae -> chooseNewImage());
        JMenuItem jexit = new JMenuItem("Exit");
        jexit.addActionListener(ae -> System.exit(0));
        jmenu.add(jmi);
        jmenu.add(jexit);
        jbar.add(jmenu);
        this.setJMenuBar(jbar);

        imgLabel = new JLabel();
        imgPanel= new JPanel();
        imgPanel.add(imgLabel);
        add(imgPanel);

        settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridLayout(4, 1));

        fromPanel = new JPanel(new GridLayout(4, 1));
        fromPanel.add(new JLabel("From color:"));
        JPanel panel = new JPanel();
        sFromRed = new JSlider(0, 255, 0);
        sFromGreen = new JSlider(0, 255, 0);
        sFromBlue = new JSlider(0, 255, 0);
        SpinnerNumberModel numberModel = new SpinnerNumberModel(new Integer(0), new Integer(0), new Integer(255), new Integer(1));
        txFromRed = new JSpinner(numberModel);
        numberModel = new SpinnerNumberModel(new Integer(0), new Integer(0), new Integer(255), new Integer(1));
        txFromGreen = new JSpinner(numberModel);
        numberModel = new SpinnerNumberModel(new Integer(0), new Integer(0), new Integer(255), new Integer(1));
        txFromBlue = new JSpinner(numberModel);
        panel.add(new JLabel("R:"));
        panel.add(sFromRed);
        panel.add(txFromRed);
        fromPanel.add(panel);
        panel = new JPanel();
        panel.add(new JLabel("G:"));
        panel.add(sFromGreen);
        panel.add(txFromGreen);
        fromPanel.add(panel);
        panel = new JPanel();
        panel.add(new JLabel("B:"));
        panel.add(sFromBlue);
        panel.add(txFromBlue);
        fromPanel.add(panel);
        settingsPanel.add(fromPanel);

        toPanel = new JPanel(new GridLayout(4, 1));
        toPanel.add(new JLabel("To color:"));
        panel = new JPanel();
        sToRed = new JSlider(0, 255, 0);
        sToGreen = new JSlider(0, 255, 0);
        sToBlue = new JSlider(0, 255, 0);
        numberModel = new SpinnerNumberModel(new Integer(0), new Integer(0), new Integer(255), new Integer(1));
        txToRed = new JSpinner(numberModel);
        numberModel = new SpinnerNumberModel(new Integer(0), new Integer(0), new Integer(255), new Integer(1));
        txToGreen = new JSpinner(numberModel);
        numberModel = new SpinnerNumberModel(new Integer(0), new Integer(0), new Integer(255), new Integer(1));
        txToBlue = new JSpinner(numberModel);
        panel.add(new JLabel("R:"));
        panel.add(sToRed);
        panel.add(txToRed);
        toPanel.add(panel);
        panel = new JPanel();
        panel.add(new JLabel("G:"));
        panel.add(sToGreen);
        panel.add(txToGreen);
        toPanel.add(panel);
        panel = new JPanel();
        panel.add(new JLabel("B:"));
        panel.add(sToBlue);
        panel.add(txToBlue);
        toPanel.add(panel);
        settingsPanel.add(toPanel);

        JPanel sPanel = new JPanel(new GridLayout(4, 1));
        sPanel.add(new JLabel("Weights:"));
        numberModel = new SpinnerNumberModel(new Integer(1), new Integer(0), new Integer(100), new Integer(1));
        panel = new JPanel();
        sLWeight = new JSlider(0, 100, 1);
        sAWeight = new JSlider(0, 100, 1);
        sBWeight = new JSlider(0, 100, 1);
        txLWeight = new JSpinner(numberModel);
        numberModel = new SpinnerNumberModel(new Integer(1), new Integer(0), new Integer(100), new Integer(1));
        txAWeight = new JSpinner(numberModel);
        numberModel = new SpinnerNumberModel(new Integer(1), new Integer(0), new Integer(100), new Integer(1));
        txBWeight = new JSpinner(numberModel);
        panel.add(new JLabel("L:"));
        panel.add(sLWeight);
        panel.add(txLWeight);
        sPanel.add(panel);
        panel = new JPanel();
        panel.add(new JLabel("a:"));
        panel.add(sAWeight);
        panel.add(txAWeight);
        sPanel.add(panel);
        panel = new JPanel();
        panel.add(new JLabel("b:"));
        panel.add(sBWeight);
        panel.add(txBWeight);
        sPanel.add(panel);
        settingsPanel.add(sPanel);

        panel = new JPanel();
        panel.add(new JLabel("Sensitivity:"));
        sSensitivity = new JSlider(0, 100, 30);
        numberModel = new SpinnerNumberModel(new Integer(30), new Integer(0), new Integer(100), new Integer(1));
        txSensitivity = new JSpinner(numberModel);
        panel.add(sSensitivity);
        panel.add(txSensitivity);
        settingsPanel.add(panel);

        add(settingsPanel);

        imgLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setFromColorFromPicture(e.getX(), e.getY());
            }
        });

        initListeners();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void initListeners() {
        sToRed.addChangeListener(e -> setToColor(new Color(sToRed.getValue(), toColor.getGreen(), toColor.getBlue())));
        sToGreen.addChangeListener(e -> setToColor(new Color(toColor.getRed(), sToGreen.getValue(), toColor.getBlue())));
        sToBlue.addChangeListener(e -> setToColor(new Color(toColor.getRed(), toColor.getGreen(), sToBlue.getValue())));

        sFromRed.addChangeListener(e -> setFromColor(new Color(sFromRed.getValue(), fromColor.getGreen(), fromColor.getBlue())));
        sFromGreen.addChangeListener(e -> setFromColor(new Color(fromColor.getRed(), sFromGreen.getValue(), fromColor.getBlue())));
        sFromBlue.addChangeListener(e -> setFromColor(new Color(fromColor.getRed(), fromColor.getGreen(), sFromBlue.getValue())));

        sLWeight.addChangeListener(e -> setWeights(0, sLWeight.getValue()));
        sAWeight.addChangeListener(e -> setWeights(1, sAWeight.getValue()));
        sBWeight.addChangeListener(e -> setWeights(2, sBWeight.getValue()));

        sSensitivity.addChangeListener(e -> setSensitivity(sSensitivity.getValue()));

        txToRed.addChangeListener(e -> setToColor(new Color((Integer)txToRed.getValue(), toColor.getGreen(), toColor.getBlue())));
        txToGreen.addChangeListener(e -> setToColor(new Color(toColor.getRed(), (Integer)txToGreen.getValue(), toColor.getBlue())));
        txToBlue.addChangeListener(e -> setToColor(new Color(toColor.getRed(), toColor.getGreen(), (Integer)txToBlue.getValue())));

        txFromRed.addChangeListener(e -> setFromColor(new Color((Integer)txFromRed.getValue(), fromColor.getGreen(), fromColor.getBlue())));
        txFromGreen.addChangeListener(e -> setFromColor(new Color(fromColor.getRed(), (Integer)txFromGreen.getValue(), fromColor.getBlue())));
        txFromBlue.addChangeListener(e -> setFromColor(new Color(fromColor.getRed(), fromColor.getGreen(), (Integer)txFromBlue.getValue())));

        txLWeight.addChangeListener(e -> setWeights(0, (Integer)txLWeight.getValue()));
        txAWeight.addChangeListener(e -> setWeights(1, (Integer)txAWeight.getValue()));
        txBWeight.addChangeListener(e -> setWeights(2, (Integer)txBWeight.getValue()));

        txSensitivity.addChangeListener(e -> setSensitivity((Integer)txSensitivity.getValue()));
    }

    private void setFromColorFromPicture(int x, int y) {
        if (img == null) return;
        int packedInt = img.getRGB(x, y);
        setFromColor(new Color(packedInt, true));
    }

    private void setToColor(Color color) {
        toColor = color;
        sToRed.setValue(toColor.getRed());
        txToRed.setValue(toColor.getRed());
        sToGreen.setValue(toColor.getGreen());
        txToGreen.setValue(toColor.getGreen());
        sToBlue.setValue(toColor.getBlue());
        txToBlue.setValue(toColor.getBlue());
        toPanel.setBackground(toColor);
    }

    private void setFromColor(Color color) {
        fromColor = color;
        sFromRed.setValue(fromColor.getRed());
        txFromRed.setValue(fromColor.getRed());
        sFromGreen.setValue(fromColor.getGreen());
        txFromGreen.setValue(fromColor.getGreen());
        sFromBlue.setValue(fromColor.getBlue());
        txFromBlue.setValue(fromColor.getBlue());
        fromPanel.setBackground(fromColor);
    }

    private void setWeights(int idx, Integer value) {
        weight[idx] = (double) value;
        sLWeight.setValue((int) weight[0]);
        txLWeight.setValue((int) weight[0]);
        sAWeight.setValue((int) weight[1]);
        txAWeight.setValue((int) weight[1]);
        sBWeight.setValue((int) weight[2]);
        txBWeight.setValue((int) weight[2]);
        updateColor();
    }

    private void setSensitivity(Integer value) {
        sensitivity = (double) value;
        sSensitivity.setValue((int) sensitivity);
        txSensitivity.setValue((int) sensitivity);
        updateColor();
    }

    private void chooseNewImage() {
        JFileChooser fc = new JFileChooser();
        int result = fc.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                img = ImageIO.read(file);
                initImg = ImageIO.read(file);
                imgLabel.setIcon(new ImageIcon(img));
                imgLabel.setSize(img.getWidth(), img.getHeight());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateColor() {
        double[] difference = getDifference();
        double[] fromLab = convertRGBtoLAB(fromColor);
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                img.setRGB(x, y, getUpdatedColor(initImg.getRGB(x, y), difference, fromLab));
            }
        }
        repaint();
    }

    private int getUpdatedColor(int color, double[] difference, double[] fromLab) {
        double[] lab = convertRGBtoLAB(new Color(color, true));
        double diff = Math.pow(Math.pow(fromLab[0] - lab[0], 2) / weight[0] + Math.pow(fromLab[1] - lab[1], 2) / weight[1] + Math.pow(fromLab[2] - lab[2], 2) / weight[2], 0.5);
        if (diff > sensitivity) return color;
        lab[0] += difference[0];
        lab[1] += difference[1];
        lab[2] += difference[2];
        Color newColor = convertLABtoRGB(lab);
        return newColor.getRGB();
    }

    private void reset() {
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                img.setRGB(x, y, initImg.getRGB(x, y));
            }
        }
        repaint();
    }

    private double[] getDifference() {
        double[] from = convertRGBtoLAB(fromColor);
        double[] to = convertRGBtoLAB(toColor);
        return new double[] {to[0] - from[0], to[1] - from[1], to[2] - from[2]};
    }

    private double[] convertRGBtoLAB(Color color) {
        double[] xyz = new double[3];
        double[][] M = {
                {0.4887180, 0.3106803, 0.2006017},
                {0.1762044, 0.8129847, 0.0108109},
                {0.0000000, 0.0102048, 0.9897952}
        };
        for (int i = 0; i < 3; i++) {
            xyz[i] = M[i][0] * (double) color.getRed() / 255.
                    + M[i][1] * (double) color.getGreen() / 255.
                    + M[i][2] * (double) color.getBlue() / 255.;
        }
        double[] lab = new double[3];
        lab[0] = 116. * helpFunc(xyz[1] / 0.54) - 16.;
        lab[1] = 500. * (helpFunc(xyz[0] / 0.54) - helpFunc(xyz[1] / 0.54));
        lab[2] = 200. * (helpFunc(xyz[1] / 0.54) - helpFunc(xyz[2] / 0.54));
        return lab;
    }

    private Color convertLABtoRGB(double[] lab) {
        double[] xyz = new double[3];
        xyz[0] = 0.54 * helpReverseFunc((lab[0] + 16.) / 116. + lab[1] / 500.);
        xyz[1] = 0.54 * helpReverseFunc((lab[0] + 16.) / 116.);
        xyz[2] = 0.54 * helpReverseFunc((lab[0] + 16.) / 116. - lab[2] / 200.);

        double[][] M = {
                {2.3706743, -0.9000405, -0.4706338},
                {-0.5138850, 1.4253036, 0.0885814},
                {0.0052982, -0.0146949, 1.0093968}
        };
        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            rgb[i] = (int)Math.round((M[i][0] * xyz[0] + M[i][1] * xyz[1] + M[i][2] * xyz[2]) * 255);
            if (rgb[i] > 255) rgb[i] = 255;
            if (rgb[i] < 0) rgb[i] = 0;
        }
        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    private double helpFunc(double x) {
        double s = 6. / 29.;
        if (x > s * s * s) {
            return Math.pow(x, 1. / 3.);
        }
        return x / 3. / (s * s) + 4. / 29.;
    }

    private double helpReverseFunc(double x) {
        double s = 6. / 29.;
        if (x > s) {
            return Math.pow(x, 3);
        }
        return 3. * s * s * (x - 4. / 29.);
    }
}
