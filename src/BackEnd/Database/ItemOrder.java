package BackEnd.Database;

public class ItemOrder {
    private int orderId;
    private int productId;
    private int quantity;
    private double unitPrice;
    private double totalPrice;

    public ItemOrder(int orderId, int productId, int quantity, double unitPrice, double totalPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }

    // Getters and setters
}
