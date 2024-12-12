package Clients.Cashier.Controller;

import Clients.Cashier.Cashier_app;
import Clients.Cashier.View.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CashierMainController implements ActionListener {
    private CashierMainView cashierMainView;

    public CashierMainController(CashierMainView cashierMainView) {
        this.cashierMainView = cashierMainView;

        // Add action listeners for buttons
        this.cashierMainView.getBtnUpdateInventory().addActionListener(this);
        this.cashierMainView.getBtnMakeOrders().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cashierMainView.getBtnMakeOrders()) {
            // Handle "Self-Order" button click
            cashierMainView.setVisible(false);
            Cashier_app.getInstance().getOrderView().setVisible(true);
        } else if (e.getSource() == cashierMainView.getBtnUpdateInventory()) {
            // Handle "View Order History" button click
            cashierMainView.setVisible(false);
            Cashier_app.getInstance().getUpdateInventoryView().setVisible(true);
        }
    }
}
