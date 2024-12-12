package Clients.Manager.View;
import Clients.Manager.Model.UneditableTableModel;

import javax.swing.*;
import java.awt.*;

public class SalesReportView extends JDialog {
    private JTextField txtProductName;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JButton btnSearch;
    private JButton btnClose;
    private JTable recordsTable;
    private UneditableTableModel tableModel;

    public SalesReportView(Frame owner) {
        super(owner, "Sales Report", false);
        setSize(800, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Create search panel
        JPanel searchPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Product Name field
        JLabel lblProductName = new JLabel("Product Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        searchPanel.add(lblProductName, gbc);

        txtProductName = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        searchPanel.add(txtProductName, gbc);

        // Start Date field
        JLabel lblStartDate = new JLabel("Start Date (YYYY-MM-DD):");
        gbc.gridx = 0;
        gbc.gridy = 1;
        searchPanel.add(lblStartDate, gbc);

        txtStartDate = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 1;
        searchPanel.add(txtStartDate, gbc);

        // End Date field
        JLabel lblEndDate = new JLabel("End Date (YYYY-MM-DD):");
        gbc.gridx = 0;
        gbc.gridy = 2;
        searchPanel.add(lblEndDate, gbc);

        txtEndDate = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 2;
        searchPanel.add(txtEndDate, gbc);

        // Search button
        btnSearch = new JButton("Search");
        gbc.gridx = 2;
        gbc.gridy = 1;
        searchPanel.add(btnSearch, gbc);

        // Close button
        btnClose = new JButton("Close");
        gbc.gridx = 3;
        gbc.gridy = 1;
        searchPanel.add(btnClose, gbc);

        add(searchPanel, BorderLayout.NORTH);

        // Create table for results
        String[] columnNames = {"Order Date", "Product Name", "Quantity", "Total Price"};
        tableModel = new UneditableTableModel(columnNames, 0);
        recordsTable = new JTable(tableModel);

        // Set column widths
        recordsTable.getColumnModel().getColumn(0).setPreferredWidth(100);  // Order Date
        recordsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Product Name
        recordsTable.getColumnModel().getColumn(2).setPreferredWidth(70);  // Quantity
        recordsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Total Price

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(recordsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
    }

    // Getters for components
    public String getProductNameText() {
        return txtProductName.getText().trim();
    }

    public String getStartDateText() {
        return txtStartDate.getText().trim();
    }

    public String getEndDateText() {
        return txtEndDate.getText().trim();
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
    public void addRecord(String orderDate, String productName, int quantity, double totalPrice) {
        Object[] rowData = {orderDate, productName, quantity, totalPrice};
        tableModel.addRow(rowData);
    }

    // Method to clear all records from the table
    public void clearRecords() {
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
    }
}
