package by.bsu.fpmi.cg;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.io.File;
import java.util.LinkedList;

/**
 * Created by Alexandra on 03.04.2017.
 */
public class Navigation extends JPanel {

    private MainFrame parent;
    private JButton fileChooser;

    public Navigation(MainFrame parent) {
        super();
        this.parent = parent;
        init();
    }

    private void init() {
        fileChooser = new JButton("Choose new");
        fileChooser.addActionListener(e -> this.chooseNewFiles());

        this.setLayout(new BorderLayout());
        this.add(fileChooser, BorderLayout.CENTER);
    }

    private void chooseNewFiles() {
        JFileChooser chooser = new JFileChooser();;
        chooser.setDialogTitle("Choose directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File folder = chooser.getSelectedFile();
            File[] dirFiles = folder.listFiles();
            LinkedList<String> res = new LinkedList<>();
            if (dirFiles != null) {
                for (File file : dirFiles) {
                    if (file.isFile() && !file.isDirectory()) {
                        res.add(file.getAbsolutePath());
                    }
                }
            }
            parent.setFiles(res);
        }
    }
}
