package Clients.Manager.Controller;

import Clients.Manager.Manager_app;
import Clients.Manager.View.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManagerMainController implements ActionListener {
    private ManagerMainView managerMainView;

    public ManagerMainController(ManagerMainView managerMainView) {
        this.managerMainView = managerMainView;

        // Add action listeners for buttons
        this.managerMainView.getBtnSalesReport().addActionListener(this);
        this.managerMainView.getBtnMakeOrders().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == managerMainView.getBtnMakeOrders()) {
            // Handle "Self-Order" button click
            managerMainView.setVisible(false);
            Manager_app.getInstance().getInventoryView().setVisible(true);
        } else if (e.getSource() == managerMainView.getBtnSalesReport()) {
            // Handle "View Order History" button click
            managerMainView.setVisible(false);
            Manager_app.getInstance().getSalesReportView().setVisible(true);
        }
    }
}
