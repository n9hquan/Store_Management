package Clients.Manager.Model;

import java.sql.Date;

public class SalesReportRecord {
    private Date orderDate;
    private String productName;
    private int quantity;
    private double totalPrice;

    public SalesReportRecord(Date orderDate, String productName, int quantity, double totalPrice) {
        this.orderDate = orderDate;
        this.productName = productName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
