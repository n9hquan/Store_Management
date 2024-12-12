package Clients.Customer.View;
import Clients.Customer.Model.*; 

import javax.swing.*;
import java.awt.*;

public class SelfOrderHistoryView extends JDialog {
    private JTextField txtSearch;
    private JComboBox<String> searchTypeCombo;
    private JButton btnSearch;
    private JButton btnClose;
    private JTable recordsTable;
    private UneditableTableModel tableModel;

    public SelfOrderHistoryView(Frame owner) {
        super(owner, "Order Records", false);
        setSize(800, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Create search panel
        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Search type combo box
        String[] searchTypes = {"Customer Name", "Order ID"};
        searchTypeCombo = new JComboBox<>(searchTypes);
        gbc.gridx = 0;
        gbc.gridy = 0;
        searchPanel.add(searchTypeCombo, gbc);

        // Search text field
        txtSearch = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        searchPanel.add(txtSearch, gbc);

        // Search button
        btnSearch = new JButton("Search");
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        searchPanel.add(btnSearch, gbc);

        // Close button
        btnClose = new JButton("Close");
        gbc.gridx = 3;
        gbc.gridy = 0;
        searchPanel.add(btnClose, gbc);

        add(searchPanel, BorderLayout.NORTH);

        // Create table
        String[] columnNames = {"Order ID", "Customer Name", "Order Date", "Product Name", "Quantity", "Unit Price", "Total Price"};
        tableModel = new UneditableTableModel(columnNames, 0);
        recordsTable = new JTable(tableModel);

        // Set column widths
        recordsTable.getColumnModel().getColumn(0).setPreferredWidth(70);  // Order ID
        recordsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Customer Name
        recordsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Order Date
        recordsTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Product Name
        recordsTable.getColumnModel().getColumn(4).setPreferredWidth(70);  // Quantity
        recordsTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Unit Price
        recordsTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Total Price

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(recordsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
    }

    // Getters for components
    public JTextField getSearchField() {
        return txtSearch;
    }

    public JComboBox<String> getSearchTypeCombo() {
        return searchTypeCombo;
    }

    public JButton getSearchButton() {
        return btnSearch;
    }

    public JButton getCloseButton() {
        return btnClose;
    }

    public JTable getRecordsTable() {
        return recordsTable;
    }

    public UneditableTableModel getTableModel() {
        return tableModel;
    }

    // Method to add a record to the table
    public void addRecord(int orderId, String customerName, String orderDate, 
                         String productName, int quantity, double unitPrice, double totalPrice) {
        Object[] rowData = {orderId, customerName, orderDate, productName, 
                           quantity, unitPrice, totalPrice};
        tableModel.addRow(rowData);
    }

    // Method to clear all records from the table
    public void clearRecords() {
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
    }

    // Method to get the search type (returns "name" or "id")
    public String getSearchType() {
        return searchTypeCombo.getSelectedIndex() == 0 ? "name" : "id";
    }

    // Method to get the search text
    public String getSearchText() {
        return txtSearch.getText().trim();
    }
}