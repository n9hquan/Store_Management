package Clients.Customer.View;


import javax.swing.*;
import java.awt.*;

public class UpdateProfileView extends JDialog {
    private JTextField txtCustomerName;
    private JTextField txtPhoneNumber;
    private JTextField txtEmail;
    private JButton btnUpdate;
    private JButton btnCancel;

    public UpdateProfileView(Frame owner) {
        super(owner, "Update Customer Profile", true);
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Panel for input fields
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("Customer Name:"));
        txtCustomerName = new JTextField();
        inputPanel.add(txtCustomerName);

        inputPanel.add(new JLabel("Phone Number:"));
        txtPhoneNumber = new JTextField();
        inputPanel.add(txtPhoneNumber);

        inputPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        inputPanel.add(txtEmail);

        add(inputPanel, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnUpdate = new JButton("Update");
        btnCancel = new JButton("Cancel");

        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public String getCustomerName() {
        return txtCustomerName.getText();
    }

    public void setCustomerName(String customerName) {
        txtCustomerName.setText(customerName);
    }

    public String getPhoneNumber() {
        return txtPhoneNumber.getText();
    }

    public void setPhoneNumber(String phoneNumber) {
        txtPhoneNumber.setText(phoneNumber);
    }

    public String getEmail() {
        return txtEmail.getText();
    }

    public void setEmail(String email) {
        txtEmail.setText(email);
    }

    public JButton getBtnUpdate() {
        return btnUpdate;
    }

    public JButton getBtnCancel() {
        return btnCancel;
    }
}

