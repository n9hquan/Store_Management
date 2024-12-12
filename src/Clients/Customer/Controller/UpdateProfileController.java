package Clients.Customer.Controller;

import Clients.Customer.Customer_app;
import Clients.Customer.View.UpdateProfileView;
import BackEnd.DataLayer;
import BackEnd.Database.Customer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UpdateProfileController {
    private UpdateProfileView view;
    private DataLayer dataLayer;

    public UpdateProfileController(UpdateProfileView view) {
        this.view = view;
        this.dataLayer = new DataLayer(); // Initialize the DataLayer
        initController();
    }

    private void initController() {
        // Add action listener for the Update button
        view.getBtnUpdate().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleUpdateAction();
            }
        });

        // Add action listener for the Cancel button
        view.getBtnCancel().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleCancelAction();
            }
        });
    }

    private void handleUpdateAction() {
        String customerName = view.getCustomerName();
        String phoneNumber = view.getPhoneNumber();
        String email = view.getEmail();

        // Validate inputs
        if (customerName == null || customerName.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Customer name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (phoneNumber != null && !phoneNumber.matches("\\d{10,15}")) {
            JOptionPane.showMessageDialog(view, "Phone number must be between 10 and 15 digits.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (email != null && !email.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            JOptionPane.showMessageDialog(view, "Invalid email format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Perform update logic here (update or create customer)
        boolean isUpdated = updateCustomerInDatabase(customerName, phoneNumber, email);

        if (isUpdated) {
            JOptionPane.showMessageDialog(view, "Customer profile updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            view.dispose(); // Close the dialog
        } else {
            JOptionPane.showMessageDialog(view, "Failed to update customer profile.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCancelAction() {
        int response = JOptionPane.showConfirmDialog(view, "Are you sure you want to cancel the update?", "Cancel Update", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            view.dispose();
            Customer_app.getInstance().getCustomerMainView().setVisible(true);
        }
    }

    private boolean updateCustomerInDatabase(String customerName, String phoneNumber, String email) {
        try {
            // Check if the customer exists in the database
            if (dataLayer.customerExistsByName(customerName)) {
                // Retrieve the existing customer
                Customer searchCriteria = new Customer();
                searchCriteria.setCustomerName(customerName);
                List<Customer> existingCustomers = dataLayer.searchCustomersByCriteria(searchCriteria);

                if (!existingCustomers.isEmpty()) {
                    Customer existingCustomer = existingCustomers.get(0); // Assuming unique names
                    existingCustomer.setPhoneNumber(phoneNumber);
                    existingCustomer.setEmail(email);

                    // Update the customer details
                    return dataLayer.updateCustomer(existingCustomer);
                }
            } else {
                // Create a new customer
                Customer newCustomer = new Customer();
                newCustomer.setCustomerName(customerName);
                newCustomer.setPhoneNumber(phoneNumber);
                newCustomer.setEmail(email);
                return dataLayer.registerCustomer(newCustomer);
            }
        } catch (Exception e) {
            System.err.println("Error updating customer in database: " + e.getMessage());
        }
        return false;
    }
}
