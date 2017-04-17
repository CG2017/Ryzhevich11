package by.bsu.fpmi.cg5;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Alexandra on 16.04.2017.
 */
public class SettingsPanel extends JPanel {

    private MainFrame parent;

    public SettingsPanel(MainFrame parent) {
        super();
        this.parent = parent;
        init();
    }

    private void init() {
        setPreferredSize(new Dimension(200, 650));
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Line", new LinePanel(parent));
        tabbedPane.addTab("Circle", new CirclePanel(parent));

        add(tabbedPane, BorderLayout.CENTER);
    }
}
