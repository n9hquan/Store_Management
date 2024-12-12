package Clients.Cashier.View;

import javax.swing.*;
import java.awt.*;

public class UpdateInventoryView extends JDialog {
    private JTextField txtProductName;
    private JTextField txtCategory;
    private JTextField txtPrice;
    private JTextField txtSupplierID;
    private JTextField txtQuantity;
    private JButton btnUpdate;
    private JButton btnCancel;

    public UpdateInventoryView(Frame owner) {
        super(owner, "Update Inventory", true);
        setSize(400, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Panel for input fields
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add Product Name field
        inputPanel.add(new JLabel("Product Name:"));
        txtProductName = new JTextField();
        inputPanel.add(txtProductName);

        // Add Category field
        inputPanel.add(new JLabel("Category:"));
        txtCategory = new JTextField();
        inputPanel.add(txtCategory);

        // Add Price field
        inputPanel.add(new JLabel("Price:"));
        txtPrice = new JTextField();
        inputPanel.add(txtPrice);

        // Add Supplier ID field
        inputPanel.add(new JLabel("Supplier ID:"));
        txtSupplierID = new JTextField();
        inputPanel.add(txtSupplierID);

        // Add Quantity field
        inputPanel.add(new JLabel("Quantity:"));
        txtQuantity = new JTextField();
        inputPanel.add(txtQuantity);

        add(inputPanel, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnUpdate = new JButton("Update");
        btnCancel = new JButton("Cancel");
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Getters and setters for the fields
    public String getProductName() {
        return txtProductName.getText();
    }

    public void setProductName(String productName) {
        txtProductName.setText(productName);
    }

    public String getCategory() {
        return txtCategory.getText();
    }

    public void setCategory(String category) {
        txtCategory.setText(category);
    }

    public String getPrice() {
        return txtPrice.getText();
    }

    public void setPrice(String price) {
        txtPrice.setText(price);
    }

    public String getSupplierID() {
        return txtSupplierID.getText();
    }

    public void setSupplierID(String supplierID) {
        txtSupplierID.setText(supplierID);
    }

    public String getQuantity() {
        return txtQuantity.getText();
    }

    public void setQuantity(String quantity) {
        txtQuantity.setText(quantity);
    }

    public JButton getBtnUpdate() {
        return btnUpdate;
    }

    public JButton getBtnCancel() {
        return btnCancel;
    }
}
