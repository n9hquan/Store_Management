package Clients.Manager.Controller;

import Clients.Manager.Manager_app;
import Clients.Manager.View.SalesReportView;
import BackEnd.DataLayer;
import BackEnd.Database.SalesFilter;
import BackEnd.Database.SalesRecord;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class SalesReportController {
    private SalesReportView view;
    private DataLayer dataLayer;

    public SalesReportController(SalesReportView view, DataLayer dataLayer) {
        this.view = view;
        this.dataLayer = dataLayer;

        // Initialize action listeners
        initializeListeners();
    }

    private void initializeListeners() {
        // Search button listener
        view.getSearchButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateSalesReport();
            }
        });

        // Close button listener
        view.getCloseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.dispose();
                Manager_app.getInstance().getManagerMainView().setVisible(true);
            }
        });
    }

    private void generateSalesReport() {
    // Clear previous records
    view.clearRecords();

    // Get search criteria
    String productName = view.getProductNameText();
    String startDateStr = view.getStartDateText();
    String endDateStr = view.getEndDateText();

    // Validate inputs
    if (!validateInputs(productName, startDateStr, endDateStr)) {
        return;
    }

    try {
        // Create filter object to send as a query to the DataLayer
        SalesFilter filter = new SalesFilter(productName, startDateStr, endDateStr);

        // Query DataLayer for sales records
        List<SalesRecord> records = dataLayer.getSalesRecords(filter);

        // Populate table
        for (SalesRecord record : records) {
            view.addRecord(
                record.getOrderDate(),
                record.getProductName(),
                record.getQuantity(),
                record.getTotalPrice()
            );
        }

        // Show message if no results
        if (view.getRecordsTable().getRowCount() == 0) {
            JOptionPane.showMessageDialog(view, 
                "No sales records found for the specified criteria.", 
                "Search Results", 
                JOptionPane.INFORMATION_MESSAGE);
        }

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(view, 
            "Error retrieving sales records: " + ex.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}


    private boolean validateInputs(String productName, String startDateStr, String endDateStr) {
        // Validate date format if dates are provided
        try {
            if (!startDateStr.isEmpty()) {
                LocalDate.parse(startDateStr);
            }
            if (!endDateStr.isEmpty()) {
                LocalDate.parse(endDateStr);
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(view, 
                "Invalid date format. Please use YYYY-MM-DD.", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Optional: Additional validation for product name if needed
        // For example, check if product exists in the database
        if (!productName.isEmpty() && !dataLayer.productExistsByName(productName)) {
            JOptionPane.showMessageDialog(view, 
                "Product not found in the database.", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
}