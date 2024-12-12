package Clients.Cashier.Controller;
import Clients.Cashier.View.*;
import Clients.Cashier.Cashier_app;
import BackEnd.*;


import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Pattern;

public class OrderController {
    private OrderView orderView;
    private DataLayer dataLayer;

    public OrderController(OrderView orderView, DataLayer dataLayer) {
        this.orderView = orderView;
        this.dataLayer = dataLayer;
        initialize();
    }

    private void initialize() {
        orderView.getBtnAddOrder().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddOrder();
            }
        });

        orderView.getBtnSubmit().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSubmitOrder();
            }
        });

        orderView.getBtnCancel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                orderView.dispose();
                Cashier_app.getInstance().getCashierMainView().setVisible(true);
            }
        });

        orderView.getBtnDeleteOrder().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDeleteOrder();
            }
        });

        orderView.getRootPane().setDefaultButton(orderView.getBtnAddOrder());
    }

    private void handleAddOrder() {
        String customerName = orderView.getCustomerName().trim();
        String orderDate = orderView.getOrderDate().trim();
        String productNameInput = orderView.getProductName().trim();
        String quantityStr = orderView.getQuantityText();

        if (!validateInputs(customerName, orderDate, productNameInput, quantityStr)) {
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        String productName = capitalizeProductName(productNameInput);

        if (!isCustomerExists(customerName)) {
            JOptionPane.showMessageDialog(orderView, "Customer does not exist. Please register a new customer.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isProductExists(productName)) {
            JOptionPane.showMessageDialog(orderView, "No product found with the given name.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double unitPrice = dataLayer.getProductUnitPrice(productName);
        orderView.addOrderToTable(customerName, productName, unitPrice, quantity, unitPrice * quantity);
        orderView.setCustomerNameEditable(false);
        orderView.setOrderDateEditable(false);
        clearInputs();
    }

    private void handleDeleteOrder() {
        int selectedRow = orderView.getOrderTable().getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(orderView, "Please select an order to delete.", "Deletion Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String customerName = (String) orderView.getTableModel().getValueAt(selectedRow, 0);
        String productName = (String) orderView.getTableModel().getValueAt(selectedRow, 1);
        int quantity = (int) orderView.getTableModel().getValueAt(selectedRow, 3);

        int confirm = JOptionPane.showConfirmDialog(orderView,
            "Are you sure you want to delete the order for " + customerName + " (" + productName + ", Quantity: " + quantity + ")?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            orderView.getTableModel().removeRow(selectedRow);
            JOptionPane.showMessageDialog(orderView, "Order deleted successfully!", "Deletion Successful", JOptionPane.INFORMATION_MESSAGE);
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
        if (orderView.getTableModel().getRowCount() == 0) {
            JOptionPane.showMessageDialog(orderView, "You must add at least one order before submitting.", 
                "Submission Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        int confirm = JOptionPane.showConfirmDialog(orderView,
                "Do you want to submit the order?",
                "Confirm Submission",
                JOptionPane.YES_NO_OPTION);
    
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Get customer ID
                String customerName = orderView.getCustomerNameText();
                int customerId = dataLayer.getCustomerIdByName(customerName);
                if (customerId == -1) {
                    throw new Exception("Customer not found: " + customerName);
                }
    
                // Calculate total order price
                double totalOrderPrice = 0;
                for (int i = 0; i < orderView.getTableModel().getRowCount(); i++) {
                    double totalPrice = (double) orderView.getTableModel().getValueAt(i, 4);
                    totalOrderPrice += totalPrice;
                }
    
                // Insert main order
                String orderDate = orderView.getOrderDateText();
                int orderId = dataLayer.insertCustomerOrder(customerId, orderDate, totalOrderPrice);
                if (orderId == -1) {
                    throw new Exception("Failed to create customer order");
                }
    
                // Insert each item and update product quantities
                boolean allItemsInserted = true;
                for (int i = 0; i < orderView.getTableModel().getRowCount(); i++) {
                    String productName = (String) orderView.getTableModel().getValueAt(i, 1);
                    int productId = dataLayer.getProductIdByName(productName);
                    if (productId == -1) {
                        throw new Exception("Product not found: " + productName);
                    }
    
                    int quantity = (int) orderView.getTableModel().getValueAt(i, 3);
                    double unitPrice = (double) orderView.getTableModel().getValueAt(i, 2);
                    double totalPrice = (double) orderView.getTableModel().getValueAt(i, 4);
    
                    boolean itemInserted = dataLayer.insertItemOrder(orderId, productId, quantity, unitPrice, totalPrice);
                    if (!itemInserted) {
                        allItemsInserted = false;
                        break;
                    }
    
                    // Update product quantity
                    boolean quantityUpdated = updateProductQuantity(productId, quantity);
                    if (!quantityUpdated) {
                        allItemsInserted = false;
                        break;
                    }
                }
    
                if (!allItemsInserted) {
                    throw new Exception("Failed to insert all order items or update product quantities");
                }
    
                JOptionPane.showMessageDialog(orderView, "Order submitted successfully!", 
                    "Submission Successful", JOptionPane.INFORMATION_MESSAGE);
    
                // Reset the form
                orderView.getTableModel().setRowCount(0);
                clearInputs();
                orderView.setCustomerNameEditable(true);
                orderView.setOrderDateEditable(true);
                orderView.clearCustomerName();
                orderView.clearOrderDate();
    
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(orderView, 
                    "Error submitting order: " + ex.getMessage(),
                    "Submission Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Update the product quantity after the order is placed
    private boolean updateProductQuantity(int productId, int quantityOrdered) {
        // Decrease the product quantity by the quantity ordered
        boolean success = dataLayer.decreaseProductQuantity(productId, quantityOrdered);
        if (!success) {
            JOptionPane.showMessageDialog(orderView, 
                "Failed to update the product quantity. Please try again.",
                "Update Error", JOptionPane.ERROR_MESSAGE);
        }
        return success;
    }

    private boolean validateInputs(String customerName, String orderDate, String productName, String quantityStr) {
        if (customerName.isEmpty() || orderDate.isEmpty() || productName.isEmpty() || quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(orderView, "All fields are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(orderView, "Quantity must be greater than 0.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(orderView, "Please enter a valid quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate order date format (YYYY-MM-DD)
        if (!isValidDateFormat(orderDate)) {
            JOptionPane.showMessageDialog(orderView, "Order date must be in the format YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean isValidDateFormat(String date) {
        String regex = "^\\d{4}-\\d{2}-\\d{2}$"; // Check format YYYY-MM-DD
        if (!Pattern.matches(regex, date)) {
            return false;
        }

        // Split the date into components
        String[] parts = date.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        // Check if the date is a valid calendar date
        return isValidDate(year, month, day);
    }

    private boolean isValidDate(int year, int month, int day) {
        // Check month range
        if (month < 1 || month > 12) {
            return false;
        }

        // Check day range
        if (day < 1 || day > 31) {
            return false;
        }

        // Handle month-specific day limits
        if (month == 2) { // February
            if (isLeapYear(year)) {
                return day <= 29; // Leap year
            } else {
                return day <= 28; // Non-leap year
            }
        } else if (month == 4 || month == 6 || month == 9 || month == 11) { // April, June, September, November
            return day <= 30;
        }

        return true; // For other months
    }

    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    private boolean isCustomerExists(String customerName) {
        return dataLayer.customerExistsByName(customerName);
    }

    private boolean isProductExists(String productName) {
        return dataLayer.productExistsByName(productName);
    }

    private void clearInputs() {
        orderView.clearProductName();
        orderView.clearQuantity();
    }
}
