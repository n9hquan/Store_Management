package Clients.Customer.View;
import javax.swing.*;
import java.awt.*;

public class UpdatePaymentView extends JDialog {
    private JTextField txtCustomerName;
    private JTextField txtPaymentType;
    private JTextField txtCardNumber;
    private JTextField txtExpirationDate;
    private JButton btnUpdate;
    private JButton btnCancel;

    public UpdatePaymentView(Frame owner) {
        super(owner, "Update Customer Payment", true);
        setSize(400, 350);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Panel for input fields
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add Customer Name field
        inputPanel.add(new JLabel("Customer Name:"));
        txtCustomerName = new JTextField();
        inputPanel.add(txtCustomerName);

        inputPanel.add(new JLabel("Payment Type:"));
        txtPaymentType = new JTextField();
        inputPanel.add(txtPaymentType);

        inputPanel.add(new JLabel("Card Number:"));
        txtCardNumber = new JTextField();
        inputPanel.add(txtCardNumber);

        inputPanel.add(new JLabel("Expiration Date:"));
        txtExpirationDate = new JTextField();
        inputPanel.add(txtExpirationDate);

        add(inputPanel, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnUpdate = new JButton("Update");
        btnCancel = new JButton("Cancel");
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Add getter for customer name
    public String getCustomerName() {
        return txtCustomerName.getText();
    }

    // Add setter for customer name
    public void setCustomerName(String customerName) {
        txtCustomerName.setText(customerName);
    }

    // Existing getters and setters remain the same
    public String getPaymentType() {
        return txtPaymentType.getText();
    }

    public void setPaymentType(String paymentType) {
        txtPaymentType.setText(paymentType);
    }

    public String getCardNumber() {
        return txtCardNumber.getText();
    }

    public void setCardNumber(String cardNumber) {
        txtCardNumber.setText(cardNumber);
    }

    public String getExpirationDate() {
        return txtExpirationDate.getText();
    }

    public void setExpirationDate(String expirationDate) {
        txtExpirationDate.setText(expirationDate);
    }

    public JButton getBtnUpdate() {
        return btnUpdate;
    }

    public JButton getBtnCancel() {
        return btnCancel;
    }
}