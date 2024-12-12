package BackEnd.Database;

public class SalesFilter {
    private String productName;
    private String startDate;
    private String endDate;

    public SalesFilter(String productName, String startDate, String endDate) {
        this.productName = productName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
