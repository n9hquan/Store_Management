package Clients.Customer.Controller;
import BackEnd.*;
import Clients.Customer.View.SelfOrderHistoryView;
import Clients.Customer.Customer_app;

import javax.swing.*;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SelfOrderHistoryController {
    private SelfOrderHistoryView view;
    private DataLayer dataLayer;

    // Changed constructor to accept OrderRecordsView instead of JFrame
    public SelfOrderHistoryController(SelfOrderHistoryView view, DataLayer dataLayer) {
        this.view = view;
        this.dataLayer = dataLayer;
        initializeController();
    }

    private void initializeController() {
        view.getSearchButton().addActionListener(e -> performSearch());
        view.getCloseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.dispose(); // Close the dialog
                Customer_app.getInstance().getCustomerMainView().setVisible(true);
            }
        });
    }

    private void performSearch() {
        String searchType = view.getSearchType();
        String searchText = view.getSearchText();

        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                "Please enter a search term",
                "Search Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Clear existing records
            view.clearRecords();

            // Build the query based on search type
            String query = "SELECT co.OrderID, c.CustomerName, co.OrderDate, " +
                         "p.ProductName, io.Quantity, io.UnitPrice, io.TotalPrice " +
                         "FROM CustomerOrder co " +
                         "JOIN Customer c ON co.CustomerID = c.CustomerID " +
                         "JOIN ItemOrder io ON co.OrderID = io.OrderID " +
                         "JOIN Product p ON io.ProductID = p.ProductID " +
                         (searchType.equals("name") ? 
                            "WHERE c.CustomerName LIKE ? " :
                            "WHERE co.OrderID = ? ") +
                         "ORDER BY co.OrderDate DESC";

            conn = dataLayer.getConnection();
            stmt = conn.prepareStatement(query);

            if (searchType.equals("name")) {
                stmt.setString(1, "%" + searchText + "%");
            } else {
                try {
                    stmt.setInt(1, Integer.parseInt(searchText));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(view,
                        "Please enter a valid Order ID number",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            rs = stmt.executeQuery();
            boolean foundResults = false;

            while (rs.next()) {
                foundResults = true;
                view.addRecord(
                    rs.getInt("OrderID"),
                    rs.getString("CustomerName"),
                    rs.getString("OrderDate"),
                    rs.getString("ProductName"),
                    rs.getInt("Quantity"),
                    rs.getDouble("UnitPrice"),
                    rs.getDouble("TotalPrice")
                );
            }

            if (!foundResults) {
                JOptionPane.showMessageDialog(view,
                    searchType.equals("name") ?
                        "No orders found for customer: " + searchText :
                        "No order found with ID: " + searchText,
                    "No Results",
                    JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(view,
                "Error accessing database: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            // Properly close all resources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                // Don't close the connection as it's managed by DataLayer
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}