package Clients.Cashier.View;

import javax.swing.*;
import java.awt.*;

public class CashierMainView extends JFrame {
    private JButton btnMakeOrders = new JButton("Make Orders for Customers");
    private JButton btnUpdateInventory = new JButton("Update Inventory");

    public JButton getBtnMakeOrders() {
        return btnMakeOrders;
    }

    public JButton getBtnUpdateInventory() {
        return btnUpdateInventory;
    }

    public CashierMainView() {
        // Basic frame setup
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only close this window, not entire application
        this.setSize(400, 400);
        setLocationRelativeTo(null);

        // Set button sizes
        btnMakeOrders.setPreferredSize(new Dimension(300, 50));
        btnUpdateInventory.setPreferredSize(new Dimension(300, 50));

        // Create and setup title panel
        JLabel title = new JLabel("Cashier Main Menu");
        title.setFont(new Font("Sans Serif", Font.BOLD, 20));
        JPanel panelTitle = new JPanel();
        panelTitle.add(title);

        // Create panels for buttons
        JPanel panelMakeOrders = new JPanel();
        panelMakeOrders.add(btnMakeOrders);

        JPanel panelUpdateInventory = new JPanel();
        panelUpdateInventory.add(btnUpdateInventory);

        // Add panels to frame
        this.getContentPane().add(panelTitle);
        this.getContentPane().add(panelMakeOrders);
        this.getContentPane().add(panelUpdateInventory);
    }
}
