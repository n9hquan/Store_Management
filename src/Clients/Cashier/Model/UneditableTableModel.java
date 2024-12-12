package Clients.Cashier.Model;

import javax.swing.table.DefaultTableModel;

public class UneditableTableModel extends DefaultTableModel {
    public UneditableTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false; // All cells are uneditable
    }
}