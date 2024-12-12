package Clients.Customer.Controller;

import Clients.Customer.Customer_app;
import Clients.Customer.View.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomerMainController implements ActionListener {
    private CustomerMainView customerMainView;

    public CustomerMainController(CustomerMainView customerMainView) {
        this.customerMainView = customerMainView;

        // Add action listeners for buttons
        this.customerMainView.getBtnSelfOrder().addActionListener(this);
        this.customerMainView.getBtnViewOrderHistory().addActionListener(this);
        this.customerMainView.getBtnUpdateProfile().addActionListener(this);
        this.customerMainView.getBtnUpdatePaymentInfo().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == customerMainView.getBtnSelfOrder()) {
            // Handle "Self-Order" button click
            customerMainView.setVisible(false);
            Customer_app.getInstance().getSelfOrderView().setVisible(true);
        } else if (e.getSource() == customerMainView.getBtnViewOrderHistory()) {
            // Handle "View Order History" button click
            customerMainView.setVisible(false);
            Customer_app.getInstance().getOrderHistoryView().setVisible(true);
        } else if (e.getSource() == customerMainView.getBtnUpdateProfile()) {
            // Handle "Update Profile" button click
            customerMainView.setVisible(false);
            Customer_app.getInstance().getUpdateProfileView().setVisible(true);
        } else if (e.getSource() == customerMainView.getBtnUpdatePaymentInfo()) {
            // Handle "Update Payment Information" button click
            customerMainView.setVisible(false);
            Customer_app.getInstance().getUpdatePaymentInfoView().setVisible(true);
        }
    }
}
