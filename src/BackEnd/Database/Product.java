package BackEnd.Database;

public class Product {
    private int productID;
    private String name;
    private String category;
    private double unitPrice;
    private int supplierID;
    private int quantity;

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return unitPrice;
    }

    public void setPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public int getQuantity() {
        return quantity;  // Getter for quantity
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;  // Setter for quantity
    }
}