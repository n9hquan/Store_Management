package BackEnd.Database;

public class Order {
    private int customerId;
    private String orderDate;
    private double totalOrderPrice;

    // Constructor that takes customerId, orderDate, and totalOrderPrice
    public Order(int customerId, String orderDate, double totalOrderPrice) {
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.totalOrderPrice = totalOrderPrice;
    }

    // Getters and setters
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalOrderPrice() {
        return totalOrderPrice;
    }

    public void setTotalOrderPrice(double totalOrderPrice) {
        this.totalOrderPrice = totalOrderPrice;
    }
}
