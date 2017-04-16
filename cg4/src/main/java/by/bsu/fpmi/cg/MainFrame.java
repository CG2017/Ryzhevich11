package by.bsu.fpmi.cg;

import net.didion.jwnl.data.Exc;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.List;

/**
 * Created by Alexandra on 03.04.2017.
 */
public class MainFrame extends JFrame {

    private Navigation navigation;
    private InfoPanel infoPanel;
    public static SortedSet<String> keys = new TreeSet<>();
    public static List<String> keyList;
    public MainFrame() {
        super();
        init();
    }

    private void init() {
        this.setSize(new Dimension(1000, 500));
        this.setLayout(new BorderLayout());

        navigation = new Navigation(this);
        this.add(navigation, BorderLayout.NORTH);

        infoPanel = new InfoPanel(this);
        this.add(infoPanel, BorderLayout.CENTER);

        this.setResizable(false);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    void setFiles(LinkedList<String> files) {
        infoPanel.updateInfo(getInfoList(files));
    }

    private LinkedList<InfoRecord> getInfoList(LinkedList<String> files) {
        keys = new TreeSet<>();
        LinkedList<InfoRecord> res = new LinkedList<>();
        for (String file : files) {
            res.add(getInfo(file));
        }
        keyList = new ArrayList<>(keys.size() + 1);
        keyList.add("Filename");
        keyList.addAll(keys);
        return res;
    }

    private InfoRecord getInfo(String fileName) {
        try {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            FileInputStream inputstream = new FileInputStream(new File(fileName));
            ParseContext pcontext = new ParseContext();
            AutoDetectParser parser = new AutoDetectParser();
            parser.parse(inputstream, handler, metadata, pcontext);
            String[] metadataNames = metadata.names();
            InfoRecord record = new InfoRecord(fileName);
            for (String name : metadataNames) {
                record.addInfo(name, metadata.get(name));
                if (!keys.contains(name)) {
                    keys.add(name);
                }
            }
            return record;
        } catch (Exception e) {
            return new InfoRecord(fileName);
        }
    }
}
