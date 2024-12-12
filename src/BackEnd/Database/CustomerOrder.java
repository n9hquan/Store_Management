package BackEnd.Database;

public class CustomerOrder {
    private int customerId;
    private String orderDate;
    private double totalOrderPrice;

    public CustomerOrder(int customerId, String orderDate, double totalOrderPrice) {
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.totalOrderPrice = totalOrderPrice;
    }

    // Getters and setters
}
