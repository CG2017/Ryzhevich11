import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Alexandra on 30.04.2017.
 */
public class MainFrame extends JFrame {

    private BufferedImage img, initImg;
    private JLabel imgLabel, initImgLabel;

    private JPanel initImgPanel, imgPanel, settingsPanel;
    private JButton reset, gray;
    private JSlider sFilterPower, sBernsenRadius, sBernsenConstant, sMidgreyRadius, sMidgreyConstant, sAdaptiveAlpha;

    private int filterPower = 1;

    public MainFrame() {
        super();
        init();
    }

    private void init() {
        this.setTitle("CG Lab6");
        this.setSize(1200, 500);
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

        initImgLabel = new JLabel();
        initImgPanel= new JPanel();
        initImgPanel.add(initImgLabel);
        add(initImgPanel);
        imgLabel = new JLabel();
        imgPanel= new JPanel();
        imgPanel.add(imgLabel);
        add(imgPanel);

        settingsPanel = new JPanel();
        settingsPanel.setLayout(new GridLayout(16, 1));

        sFilterPower = new JSlider(0, 5, 1);
        sFilterPower.setMajorTickSpacing(1);
        sFilterPower.setPaintTicks(true);
        settingsPanel.add(new JLabel("Filter power:"));
        settingsPanel.add(sFilterPower);

        gray = new JButton("Gray");
        settingsPanel.add(gray);

        sBernsenRadius = new JSlider(1, 10, 5);
        sBernsenRadius.setMajorTickSpacing(2);
        sBernsenRadius.setPaintTicks(true);
        settingsPanel.add(new JLabel("Bernsen radius:"));
        settingsPanel.add(sBernsenRadius);

        sBernsenConstant = new JSlider(0, 255, 15);
        sBernsenConstant.setMajorTickSpacing(20);
        sBernsenConstant.setPaintTicks(true);
        settingsPanel.add(new JLabel("Bernsen contrast threshold:"));
        settingsPanel.add(sBernsenConstant);

        sMidgreyRadius = new JSlider(1, 10, 5);
        sMidgreyRadius.setMajorTickSpacing(2);
        sMidgreyRadius.setPaintTicks(true);
        settingsPanel.add(new JLabel("Midgrey radius:"));
        settingsPanel.add(sMidgreyRadius);

        sMidgreyConstant = new JSlider(0, 255, 15);
        sMidgreyConstant.setMajorTickSpacing(20);
        sMidgreyConstant.setPaintTicks(true);
        settingsPanel.add(new JLabel("Midgrey constant:"));
        settingsPanel.add(sMidgreyConstant);

        sAdaptiveAlpha = new JSlider(0, 20, 8);
        sAdaptiveAlpha.setMajorTickSpacing(2);
        sAdaptiveAlpha.setPaintTicks(true);
        settingsPanel.add(new JLabel("Adaptive alpha:"));
        settingsPanel.add(sAdaptiveAlpha);

        reset = new JButton("Reset");
        settingsPanel.add(reset);

        add(settingsPanel);

        initListeners();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void initListeners() {
        gray.addActionListener(e -> grayPicture());
        sAdaptiveAlpha.addChangeListener(e -> applyAdaptive());
        reset.addActionListener(e -> resetPicture());
        sFilterPower.addChangeListener(e -> applyFilter());
        sBernsenRadius.addChangeListener(e -> applyBernsen());
        sBernsenConstant.addChangeListener(e -> applyBernsen());
        sMidgreyRadius.addChangeListener(e -> applyMidgrey());
        sMidgreyConstant.addChangeListener(e -> applyMidgrey());
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
                initImgLabel.setIcon(new ImageIcon(initImg));
                initImgLabel.setSize(initImg.getWidth(), initImg.getHeight());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void resetPicture() {
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                img.setRGB(x, y, initImg.getRGB(x, y));
            }
        }
        repaint();
    }

    private void grayPicture() {
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color color = new Color(initImg.getRGB(x, y), true);
                int newColor = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                img.setRGB(x, y, new Color(newColor, newColor, newColor).getRGB());
            }
        }
        repaint();
    }

    private int[][] getKernel() {
        int power = sFilterPower.getValue();
        int size = power * 2 + 1;
        int[][] res = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                res[i][j] = -1;
            }
        }
        res[power][power] = size * size;
        return res;
    }

    private void applyFilter() {
        if (initImg == null || img == null) return;
        if (sFilterPower.getValueIsAdjusting()) return;
        if (sFilterPower.getValue() == 0) {
            resetPicture();
            return;
        }
        int width = initImg.getWidth();
        int height = initImg.getHeight();

        int[][] kernel = getKernel();

        int kernelSize = kernel.length;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double rSum = 0, gSum = 0, bSum = 0, kSum = 0;
                for (int i = 0; i < kernelSize; i++) {
                    for (int j = 0; j < kernelSize; j++) {
                        int pixelPosX = x + (i - (kernelSize / 2));
                        int pixelPosY = y + (j - (kernelSize / 2));
                        if ((pixelPosX < 0) || (pixelPosX >= width) || (pixelPosY < 0) || (pixelPosY >= height)) continue;

                        Color color = new Color(initImg.getRGB(pixelPosX, pixelPosY), true);
                        int r = color.getRed();
                        int g = color.getGreen();
                        int b = color.getBlue();

                        int kernelVal = kernel[i][j];

                        rSum += (double)(r * kernelVal);
                        gSum += (double)(g * kernelVal);
                        bSum += (double)(b * kernelVal);

                        kSum += (double)kernelVal;
                    }
                }

                if (kSum <= 0) kSum = 1;

                rSum /= kSum;
                if (rSum < 0) rSum = 0;
                if (rSum > 255) rSum = 255;

                gSum /= kSum;
                if (gSum < 0) gSum = 0;
                if (gSum > 255) gSum = 255;

                bSum /= kSum;
                if (bSum < 0) bSum = 0;
                if (bSum > 255) bSum = 255;

                img.setRGB(x, y, new Color((int)rSum, (int)gSum, (int)bSum).getRGB());
            }
        }
        repaint();
    }

    private void applyBernsen() {
        if (initImg == null || img == null) return;
        if (sBernsenRadius.getValueIsAdjusting()) return;
        if (sBernsenConstant.getValueIsAdjusting()) return;

        int width = initImg.getWidth();
        int height = initImg.getHeight();

        int radius = sBernsenRadius.getValue();
        int constant = sBernsenConstant.getValue();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int max = 0, min = 255;
                for (int i = 0; i < radius * 2 + 1; i++) {
                    for (int j = 0; j < radius * 2 + 1; j++) {
                        int pixelPosX = x + (i - radius);
                        int pixelPosY = y + (j - radius);
                        if ((pixelPosX < 0) || (pixelPosX >= width) || (pixelPosY < 0) || (pixelPosY >= height)) continue;

                        Color color = new Color(initImg.getRGB(pixelPosX, pixelPosY), true);
                        int grayColor = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                        max = grayColor > max ? grayColor : max;
                        min = grayColor < max ? grayColor : min;
                    }
                }

                Color newColor;
                Color color = new Color(initImg.getRGB(x, y), true);
                int grayColor = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                if (max - min < constant) {
                    newColor = ((max + min) / 2 >= 128) ? Color.white : Color.black;
                }
                else {
                    newColor = (grayColor >= (max + min) / 2) ? Color.white : Color.black;
                }

                img.setRGB(x, y, newColor.getRGB());
            }
        }
        repaint();
    }

    private void applyAdaptive() {
        if (initImg == null || img == null) return;
        if (sAdaptiveAlpha.getValueIsAdjusting()) return;

        int width = initImg.getWidth();
        int height = initImg.getHeight();

        double alpha = ((double) sAdaptiveAlpha.getValue()) / 20.0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int radius = 1;
                while (true) {
                    int max = 0, min = 255, mid = 0;
                    for (int i = 0; i < radius * 2 + 1; i++) {
                        for (int j = 0; j < radius * 2 + 1; j++) {
                            int pixelPosX = x + (i - radius);
                            int pixelPosY = y + (j - radius);
                            if ((pixelPosX < 0) || (pixelPosX >= width) || (pixelPosY < 0) || (pixelPosY >= height))
                                continue;

                            Color color = new Color(initImg.getRGB(pixelPosX, pixelPosY), true);
                            int grayColor = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                            max = grayColor > max ? grayColor : max;
                            min = grayColor < min ? grayColor : min;
                            mid += grayColor;
                        }
                    }
                    mid /= (2 * radius + 1) * (2 * radius + 1);
                    int deltaMax = Math.abs(max - mid);
                    int deltaMin = Math.abs(min - mid);
                    int t = 0;

                    if (deltaMax > deltaMin) {
                        t = (int) (alpha * (((double) min) * 2. / 3. + ((double) max) / 3.));
                    } else if (deltaMax < deltaMin) {
                        t = (int) (alpha * (((double) min) / 3. + ((double) max) * 2. / 3.));
                    } else if (deltaMax == deltaMin && max == min) {
                        t = (int) (alpha * (double) mid);
                    } else {
                        radius += 1;
                        continue;
                    }

                    Color newColor;
                    Color color = new Color(initImg.getRGB(x, y), true);
                    int grayColor = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                    newColor = Math.abs(mid - grayColor) > t ? Color.black : Color.white;

                    img.setRGB(x, y, newColor.getRGB());
                    break;
                }
            }
        }
        repaint();
    }

    private void applyMidgrey() {
        if (initImg == null || img == null) return;
        if (sMidgreyRadius.getValueIsAdjusting()) return;
        if (sMidgreyConstant.getValueIsAdjusting()) return;

        int width = initImg.getWidth();
        int height = initImg.getHeight();

        int radius = sMidgreyRadius.getValue();
        int constant = sMidgreyConstant.getValue();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int max = 0, min = 255;
                for (int i = 0; i < radius * 2 + 1; i++) {
                    for (int j = 0; j < radius * 2 + 1; j++) {
                        int pixelPosX = x + (i - radius);
                        int pixelPosY = y + (j - radius);
                        if ((pixelPosX < 0) || (pixelPosX >= width) || (pixelPosY < 0) || (pixelPosY >= height)) continue;

                        Color color = new Color(initImg.getRGB(pixelPosX, pixelPosY), true);
                        int grayColor = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                        max = grayColor > max ? grayColor : max;
                        min = grayColor < max ? grayColor : min;
                    }
                }

                Color newColor;
                Color color = new Color(initImg.getRGB(x, y), true);
                int grayColor = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
                newColor = (grayColor > (max + min) / 2 - constant) ? Color.white : Color.black;

                img.setRGB(x, y, newColor.getRGB());
            }
        }
        repaint();
    }
}
