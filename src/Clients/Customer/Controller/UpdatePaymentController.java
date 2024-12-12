package Clients.Customer.Controller;
import Clients.Customer.Customer_app;
import Clients.Customer.View.UpdatePaymentView;
import BackEnd.DataLayer;
import BackEnd.Database.Customer;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UpdatePaymentController {
    private UpdatePaymentView view;
    private DataLayer dataLayer;

    public UpdatePaymentController(UpdatePaymentView view) {
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
        String paymentType = view.getPaymentType();
        String cardNumber = view.getCardNumber();
        String expirationDate = view.getExpirationDate();

        // Validate inputs
        if (customerName == null || customerName.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Customer name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if customer exists before proceeding
        if (!dataLayer.customerExistsByName(customerName)) {
            JOptionPane.showMessageDialog(view, "No user exists with the name: " + customerName, "User Not Found", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate other fields if needed
        if (paymentType.isEmpty() || cardNumber.isEmpty() || expirationDate.isEmpty()) {
            JOptionPane.showMessageDialog(view, "All payment fields are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Perform update logic here (update payment information)
        boolean isUpdated = updatePaymentInDatabase(customerName, paymentType, cardNumber, expirationDate);
        
        if (isUpdated) {
            JOptionPane.showMessageDialog(view, "Customer payment information updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            view.dispose(); // Close the dialog
        } else {
            JOptionPane.showMessageDialog(view, "Failed to update customer payment information.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCancelAction() {
        int response = JOptionPane.showConfirmDialog(view, "Are you sure you want to cancel the update?", "Cancel Update", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            view.dispose();
            Customer_app.getInstance().getCustomerMainView().setVisible(true);
        }
    }

    private boolean updatePaymentInDatabase(String customerName, String paymentType, String cardNumber, String expirationDate) {
        try {
            // Retrieve the existing customer (we've already checked existence)
            Customer searchCriteria = new Customer();
            searchCriteria.setCustomerName(customerName);
            List<Customer> existingCustomers = dataLayer.searchCustomersByCriteria(searchCriteria);
            
            if (!existingCustomers.isEmpty()) {
                Customer existingCustomer = existingCustomers.get(0); // Assuming unique names
                existingCustomer.setPaymentType(paymentType);
                existingCustomer.setCardNumber(cardNumber);
                existingCustomer.setExpirationDate(expirationDate);
                
                // Update the customer payment information
                return dataLayer.updateCustomerPayment(existingCustomer);
            }
        } catch (Exception e) {
            System.err.println("Error updating customer payment information in database: " + e.getMessage());
        }
        return false;
    }
}