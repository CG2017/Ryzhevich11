package by.bsu.fpmi.cg;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Panel that displays table of tariffs.
 * @author Alexandra Ryzhevich
 */
public class InfoPanel extends JPanel {

    private MainFrame parent;
    private JScrollPane tablePanel;
    private JTable table;

    public InfoPanel(MainFrame parent) {
        super();
        this.parent = parent;
        this.init();
    }

    private void init() {
        this.setLayout(new BorderLayout());
        tablePanel = new JScrollPane();
    }

    public void updateInfo(List<InfoRecord> records) {
        InfoTableModel model = new InfoTableModel(records);
        this.remove(tablePanel);
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setVisible(true);
        tablePanel = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.add(tablePanel, BorderLayout.CENTER);
        parent.repaint();
        tablePanel.repaint();
        table.repaint();
        parent.setSize(new Dimension(1001, 501));
        parent.setSize(new Dimension(1000, 500));
    }

}
