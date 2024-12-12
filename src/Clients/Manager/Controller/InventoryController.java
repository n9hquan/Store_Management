package Clients.Manager.Controller;

import Clients.Manager.View.InventoryView;
import Clients.Manager.Manager_app;
import BackEnd.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class InventoryController {
    private InventoryView inventoryView;
    private DataLayer dataLayer;

    public InventoryController(InventoryView inventoryView, DataLayer dataLayer) {
        this.inventoryView = inventoryView;
        this.dataLayer = dataLayer;
        initialize();
    }

    private void initialize() {
        inventoryView.getBtnAddOrder().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddOrder();
            }
        });

        inventoryView.getBtnSubmit().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSubmitOrder();
            }
        });

        inventoryView.getBtnCancel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inventoryView.dispose();
                Manager_app.getInstance().getManagerMainView().setVisible(true);
            }
        });

        inventoryView.getBtnDeleteOrder().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDeleteOrder();
            }
        });

        inventoryView.getRootPane().setDefaultButton(inventoryView.getBtnAddOrder());
    }

    private void handleAddOrder() {
        String productNameInput = inventoryView.getProductName().trim();
        String quantityStr = inventoryView.getQuantityText();

        if (!validateInputs(productNameInput, quantityStr)) {
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        String productName = capitalizeProductName(productNameInput);

        if (!isProductExists(productName)) {
            JOptionPane.showMessageDialog(inventoryView, "No product found with the given name.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double unitPrice = dataLayer.getProductUnitPrice(productName);
        inventoryView.addOrderToTable(productName, unitPrice, quantity, unitPrice * quantity);
        clearInputs();
    }

    private void handleDeleteOrder() {
        int selectedRow = inventoryView.getOrderTable().getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(inventoryView, "Please select an order to delete.", "Deletion Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String productName = (String) inventoryView.getTableModel().getValueAt(selectedRow, 0);
        int quantity = (int) inventoryView.getTableModel().getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(inventoryView,
            "Are you sure you want to delete the inventory entry for " + productName + " (Quantity: " + quantity + ")?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            inventoryView.getTableModel().removeRow(selectedRow);
            JOptionPane.showMessageDialog(inventoryView, "Inventory entry deleted successfully!", "Deletion Successful", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String capitalizeProductName(String productName) {
        String[] words = productName.toLowerCase().split(" ");
        StringBuilder capitalizedProductName = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                capitalizedProductName.append(Character.toUpperCase(word.charAt(0)));
                capitalizedProductName.append(word.substring(1)).append(" ");
            }
        }
        return capitalizedProductName.toString().trim();
    }

    private void handleSubmitOrder() {
        if (inventoryView.getTableModel().getRowCount() == 0) {
            JOptionPane.showMessageDialog(inventoryView, "You must add at least one inventory entry before submitting.", 
                "Submission Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        int confirm = JOptionPane.showConfirmDialog(inventoryView,
                "Do you want to submit the inventory updates?",
                "Confirm Submission",
                JOptionPane.YES_NO_OPTION);
    
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Calculate total inventory value
                boolean allItemsProcessed = true;
                
                for (int i = 0; i < inventoryView.getTableModel().getRowCount(); i++) {
                    String productName = (String) inventoryView.getTableModel().getValueAt(i, 0);
                    int productId = dataLayer.getProductIdByName(productName);
                    if (productId == -1) {
                        throw new Exception("Product not found: " + productName);
                    }
    
                    int quantity = (int) inventoryView.getTableModel().getValueAt(i, 2);
    
                    // Update product quantity (INCREASE instead of decrease)
                    boolean quantityUpdated = updateProductQuantity(productId, quantity);
                    if (!quantityUpdated) {
                        allItemsProcessed = false;
                        break;
                    }
                }
    
                if (!allItemsProcessed) {
                    throw new Exception("Failed to update product quantities");
                }
    
                JOptionPane.showMessageDialog(inventoryView, "Inventory updated successfully!", 
                    "Submission Successful", JOptionPane.INFORMATION_MESSAGE);
    
                // Reset the form
                inventoryView.getTableModel().setRowCount(0);
                clearInputs();
    
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(inventoryView, 
                    "Error updating inventory: " + ex.getMessage(),
                    "Submission Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Update the product quantity after the inventory update
    private boolean updateProductQuantity(int productId, int quantityToAdd) {
        // INCREASE the product quantity by the quantity specified
        boolean success = dataLayer.increaseProductQuantity(productId, quantityToAdd);
        if (!success) {
            JOptionPane.showMessageDialog(inventoryView, 
                "Failed to update the product quantity. Please try again.",
                "Update Error", JOptionPane.ERROR_MESSAGE);
        }
        return success;
    }

    private boolean validateInputs(String productName, String quantityStr) {
        if (productName.isEmpty() || quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(inventoryView, "All fields are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(inventoryView, "Quantity must be greater than 0.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(inventoryView, "Please enter a valid quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean isProductExists(String productName) {
        return dataLayer.productExistsByName(productName);
    }

    private void clearInputs() {
        inventoryView.clearProductName();
        inventoryView.clearQuantity();
    }
}