package by.bsu.fpmi.cg;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alexandra on 03.04.2017.
 */
public class InfoRecord {

    private Map<String, String> info;

    public InfoRecord(String filename) {
        this.info = new HashMap<>();
        info.put("Filename", filename);
    }

    public void addInfo(String name, String value) {
        info.put(name, value);
    }

    public String getValue(String name) {
        return info.getOrDefault(name, "");
    }
}
