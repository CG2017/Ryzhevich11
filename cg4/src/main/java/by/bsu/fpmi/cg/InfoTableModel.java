package by.bsu.fpmi.cg;

import javax.swing.event.*;
import javax.swing.table.*;
import java.util.*;

public class InfoTableModel implements TableModel {

    private Set<TableModelListener> listeners = new HashSet<TableModelListener>();
    private List<InfoRecord> infoModel;

    public InfoTableModel(List<InfoRecord> records) {
        this.infoModel = records;
    }

    public void addTableModelListener(TableModelListener listener) {
        listeners.add(listener);
    }

    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    public int getColumnCount() {
        return MainFrame.keyList.size();
    }

    public String getColumnName(int columnIndex) {
        if (columnIndex < MainFrame.keyList.size()) {
            return MainFrame.keyList.get(columnIndex);
        }
        return "";
    }

    public int getRowCount() {
        return infoModel.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        InfoRecord bean = infoModel.get(rowIndex);
        if (columnIndex < MainFrame.keyList.size()) {
            return bean.getValue(MainFrame.keyList.get(columnIndex));
        }
        return "";
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void removeTableModelListener(TableModelListener listener) {
        listeners.remove(listener);
    }

    public void setValueAt(Object value, int rowIndex, int columnIndex) {

    }



}
