package Clients.Manager.View;

import javax.swing.*;

import Clients.Cashier.Model.UneditableTableModel;

import java.awt.*;

public class InventoryView extends JDialog {
    protected JTextField txtProductName; // Made protected
    protected JTextField txtQuantity; // Made protected
    private JButton btnAddOrder;
    private JButton btnDeleteOrder; // New Delete button
    private JButton btnSubmit; // New Submit button
    private JButton btnCancel;
    private JTable orderTable;
    protected UneditableTableModel tableModel;
    private JLabel lblTotalOrderPrice; // Label for total order price
    
    public String getProductNameText() {
        return txtProductName.getText().trim();
    }
    
    public String getQuantityText() {
        return txtQuantity.getText().trim();
    }

    public InventoryView(Frame owner) {
        super(owner, "Manage Inventory", false);
        setSize(600, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10)); // Changed to 5 rows for the adjusted input

        inputPanel.add(new JLabel("Product Name:"));
        txtProductName = new JTextField();
        inputPanel.add(txtProductName);

        inputPanel.add(new JLabel("Quantity:"));
        txtQuantity = new JTextField();
        inputPanel.add(txtQuantity);

        btnAddOrder = new JButton("Add Order");
        btnDeleteOrder = new JButton("Delete Order"); // Initialize Delete button
        btnSubmit = new JButton("Submit"); // Initialize Submit button
        btnCancel = new JButton("Return to Main Menu");

        inputPanel.add(btnAddOrder);
        inputPanel.add(btnDeleteOrder); // Add Delete button to the panel
        inputPanel.add(btnSubmit); // Add Submit button to the panel
        inputPanel.add(btnCancel);

        add(inputPanel, BorderLayout.NORTH);

        String[] columnNames = {"Product Name", "Unit Price", "Quantity", "Total Price"};
        tableModel = new UneditableTableModel(columnNames, 0);
        orderTable = new JTable(tableModel);
        add(new JScrollPane(orderTable), BorderLayout.CENTER);

        // Create a label to display the total order price
        lblTotalOrderPrice = new JLabel("Total Order Price: $0.00");
        lblTotalOrderPrice.setHorizontalAlignment(SwingConstants.RIGHT);
        add(lblTotalOrderPrice, BorderLayout.SOUTH);
    }

    public String getProductName() {
        return txtProductName.getText();
    }

    public int getQuantity() {
        return Integer.parseInt(txtQuantity.getText());
    }

    public JButton getBtnAddOrder() {
        return btnAddOrder;
    }

    public JButton getBtnDeleteOrder() {
        return btnDeleteOrder; // Getter for the Delete button
    }

    public JButton getBtnSubmit() {
        return btnSubmit; // Getter for the Submit button
    }

    public JButton getBtnCancel() {
        return btnCancel;
    }

    public void addOrderToTable(String productName, double unitPrice, int quantity, double totalPrice) {
        Object[] rowData = {productName, unitPrice, quantity, totalPrice};
        tableModel.addRow(rowData);
        updateTotalOrderPrice(); // Update the total price after adding an order
    }

    public void removeOrderFromTable(int rowIndex) {
        tableModel.removeRow(rowIndex);
        updateTotalOrderPrice(); // Update the total price after deleting an order
    }

    private void updateTotalOrderPrice() {
        double total = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            total += (double) tableModel.getValueAt(i, 3); // Assuming Total Price is in the 4th column
        }
        lblTotalOrderPrice.setText(String.format("Total Order Price: $%.2f", total));
    }

    public UneditableTableModel getTableModel() {
        return tableModel;
    }    

    public JTable getOrderTable() {
        return orderTable;
    }

    public void clearInputs() {
        txtProductName.setText("");
        txtQuantity.setText("");
    }

    public void clearProductName() {
        txtProductName.setText("");
    }

    public void clearQuantity() {
        txtQuantity.setText("");
    }
}
