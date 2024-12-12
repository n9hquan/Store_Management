package Clients.Cashier.Controller;

import Clients.Cashier.Cashier_app;
import Clients.Cashier.View.UpdateInventoryView;
import BackEnd.DataLayer;
import BackEnd.Database.Product;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateInventoryController {
    private UpdateInventoryView view;
    private DataLayer dataLayer;

    public UpdateInventoryController(UpdateInventoryView view) {
        this.view = view;
        this.dataLayer = new DataLayer();
        initController();
    }

    private void initController() {
        view.getBtnUpdate().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleUpdateAction();
            }
        });

        view.getBtnCancel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCancelAction();
            }
        });
    }

    private void handleUpdateAction() {
        String productName = view.getProductName().trim();
        String category = view.getCategory().trim();
        String priceText = view.getPrice().trim();
        String supplierIDText = view.getSupplierID().trim();
        String quantityText = view.getQuantity().trim();

        // Validate product name (compulsory)
        if (productName.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Product name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if the product exists
        int productId = dataLayer.getProductIdByName(productName);
        if (productId == -1) {
            JOptionPane.showMessageDialog(view, "Product does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if any optional field is provided
        boolean hasOptionalUpdate = 
            !category.isEmpty() ||
            !priceText.isEmpty() ||
            !supplierIDText.isEmpty() ||
            !quantityText.isEmpty();

        if (!hasOptionalUpdate) {
            JOptionPane.showMessageDialog(view, "Please provide at least one field to update.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Attempt to update the product
        boolean isUpdated = updateProductInDatabase(productId, productName, category, priceText, supplierIDText, quantityText);

        if (isUpdated) {
            JOptionPane.showMessageDialog(view, "Product inventory updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            view.dispose(); // Close the dialog
            Cashier_app.getInstance().getCashierMainView().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(view, "Failed to update product inventory.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCancelAction() {
        int response = JOptionPane.showConfirmDialog(view, "Are you sure you want to cancel the update?", "Cancel Update", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            view.dispose();
            Cashier_app.getInstance().getCashierMainView().setVisible(true);
        }
    }

    private boolean updateProductInDatabase(int productId, String productName, String category, 
                                            String priceText, String supplierIDText, 
                                            String quantityText) {
        try {
            // Retrieve the existing product
            Product existingProduct = dataLayer.loadProductID(productId);

            if (existingProduct == null) {
                JOptionPane.showMessageDialog(view, "Product not found in database.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Update category if provided
            if (!category.isEmpty()) {
                existingProduct.setCategory(category);
            }

            // Update price if provided
            if (!priceText.isEmpty()) {
                try {
                    double price = Double.parseDouble(priceText);
                    existingProduct.setPrice(price);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(view, "Invalid price format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            // Update supplier ID if provided
            if (!supplierIDText.isEmpty()) {
                try {
                    int supplierID = Integer.parseInt(supplierIDText);
                    existingProduct.setSupplierID(supplierID);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(view, "Invalid supplier ID format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            // Update quantity if provided
            if (!quantityText.isEmpty()) {
                try {
                    int quantityToAdd = Integer.parseInt(quantityText);
                    // Add the new quantity to the existing quantity
                    existingProduct.setQuantity(existingProduct.getQuantity() + quantityToAdd);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(view, "Invalid quantity format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            // Update the product in the database
            return dataLayer.updateProductInventory(existingProduct);

        } catch (Exception e) {
            System.err.println("Error updating product in database: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "An error occurred while updating the product.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
}