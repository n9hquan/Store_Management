package Clients.Customer.View;

import javax.swing.*;
import java.awt.*;

public class CustomerMainView extends JFrame {
    private JButton btnSelfOrder = new JButton("Self-Order");
    private JButton btnViewOrderHistory = new JButton("View Order History");
    private JButton btnUpdateProfile = new JButton("Update Profile");
    private JButton btnUpdatePaymentInfo = new JButton("Update Payment Information");

    public JButton getBtnSelfOrder() {
        return btnSelfOrder;
    }

    public JButton getBtnViewOrderHistory() {
        return btnViewOrderHistory;
    }

    public JButton getBtnUpdateProfile() {
        return btnUpdateProfile;
    }

    public JButton getBtnUpdatePaymentInfo() {
        return btnUpdatePaymentInfo;
    }

    public CustomerMainView() {
        // Basic frame setup
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only close this window, not entire application
        this.setSize(400, 400);
        setLocationRelativeTo(null);

        // Set button sizes
        btnSelfOrder.setPreferredSize(new Dimension(300, 50));
        btnViewOrderHistory.setPreferredSize(new Dimension(300, 50));
        btnUpdateProfile.setPreferredSize(new Dimension(300, 50));
        btnUpdatePaymentInfo.setPreferredSize(new Dimension(300, 50));

        // Create and setup title panel
        JLabel title = new JLabel("Customer Main Menu");
        title.setFont(new Font("Sans Serif", Font.BOLD, 20));
        JPanel panelTitle = new JPanel();
        panelTitle.add(title);

        // Create panels for buttons
        JPanel panelSelfOrder = new JPanel();
        panelSelfOrder.add(btnSelfOrder);

        JPanel panelViewOrderHistory = new JPanel();
        panelViewOrderHistory.add(btnViewOrderHistory);

        JPanel panelUpdateProfile = new JPanel();
        panelUpdateProfile.add(btnUpdateProfile);

        JPanel panelUpdatePaymentInfo = new JPanel();
        panelUpdatePaymentInfo.add(btnUpdatePaymentInfo);

        // Add panels to frame
        this.getContentPane().add(panelTitle);
        this.getContentPane().add(panelSelfOrder);
        this.getContentPane().add(panelViewOrderHistory);
        this.getContentPane().add(panelUpdateProfile);
        this.getContentPane().add(panelUpdatePaymentInfo);
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CustomerMainView view = new CustomerMainView();
            view.setVisible(true);
        });
    }
}