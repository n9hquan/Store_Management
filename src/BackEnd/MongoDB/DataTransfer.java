package BackEnd.MongoDB;

import BackEnd.DataLayer;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataTransfer {
    public static void main(String[] args) {
        DataLayer mysqlLayer = null;
        MongoDBLayer mongoLayer = null;
        Connection mysqlConnection = null;
        
        try {
            mysqlLayer = new DataLayer();
            mongoLayer = new MongoDBLayer();

            // Establish MySQL connection once
            mysqlConnection = mysqlLayer.getConnection();

            // Print initial database info
            mongoLayer.printDatabaseInfo();

            // Transfer Customers
            long customerCount = transferCustomers(mysqlConnection, mongoLayer);

            // Transfer Item Orders
            long itemOrderCount = transferItemOrders(mysqlConnection, mongoLayer);

            // Verify transfer
            verifyTransfer(mongoLayer, customerCount, itemOrderCount);

        } catch (Exception e) {
            System.err.println("Error during data transfer: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Ensure connections are closed
            try {
                if (mysqlConnection != null && !mysqlConnection.isClosed()) {
                    mysqlConnection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing MySQL connection: " + e.getMessage());
            }
            
            if (mysqlLayer != null) {
                mysqlLayer.closeConnection();
            }
            
            if (mongoLayer != null) {
                mongoLayer.close();
            }
        }
    }

    private static long transferCustomers(Connection connection, MongoDBLayer mongoLayer) {
        MongoCollection<Document> customerCollection = mongoLayer.getCollection("Customers");
        ReplaceOptions replaceOptions = new ReplaceOptions().upsert(true);

        String query = "SELECT * FROM Customer";
        long customerCount = 0;
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Document customer = new Document("_id", rs.getInt("CustomerID"))
                    .append("CustomerName", rs.getString("CustomerName"))
                    .append("PhoneNumber", rs.getString("PhoneNumber"))
                    .append("Email", rs.getString("Email"))
                    .append("PaymentInfo", new Document()
                        .append("PaymentType", rs.getString("PaymentType"))
                        .append("CardNumber", rs.getString("CardNumber"))
                        .append("ExpirationDate", rs.getString("ExpirationDate"))
                    );

                customerCollection.replaceOne(
                    new Document("_id", rs.getInt("CustomerID")), 
                    customer, 
                    replaceOptions
                );
                customerCount++;
            }
            System.out.println("Transferred " + customerCount + " customers to MongoDB");

        } catch (SQLException e) {
            System.err.println("Error transferring customers: " + e.getMessage());
            e.printStackTrace();
        }
        
        return customerCount;
    }

    private static long transferItemOrders(Connection connection, MongoDBLayer mongoLayer) {
        MongoCollection<Document> itemOrderCollection = mongoLayer.getCollection("ItemOrders");
        ReplaceOptions replaceOptions = new ReplaceOptions().upsert(true);

        String query = "SELECT * FROM ItemOrder";
        long itemOrderCount = 0;
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Document itemOrder = new Document("_id", rs.getInt("ItemOrderID"))
                    .append("OrderID", rs.getInt("OrderID"))
                    .append("ProductID", rs.getInt("ProductID"))
                    .append("Quantity", rs.getInt("Quantity"))
                    .append("UnitPrice", rs.getDouble("UnitPrice"))
                    .append("TotalPrice", rs.getDouble("TotalPrice"));

                itemOrderCollection.replaceOne(
                    new Document("_id", rs.getInt("ItemOrderID")), 
                    itemOrder, 
                    replaceOptions
                );
                itemOrderCount++;
            }
            System.out.println("Transferred " + itemOrderCount + " item orders to MongoDB");

        } catch (SQLException e) {
            System.err.println("Error transferring item orders: " + e.getMessage());
            e.printStackTrace();
        }
        
        return itemOrderCount;
    }

    private static void verifyTransfer(MongoDBLayer mongoLayer, long expectedCustomerCount, long expectedItemOrderCount) {
        MongoCollection<Document> customerCollection = mongoLayer.getCollection("Customers");
        MongoCollection<Document> itemOrderCollection = mongoLayer.getCollection("ItemOrders");

        long actualCustomerCount = customerCollection.countDocuments();
        long actualItemOrderCount = itemOrderCollection.countDocuments();

        System.out.println("\n--- Transfer Verification ---");
        System.out.println("Expected Customers: " + expectedCustomerCount);
        System.out.println("Actual Customers in MongoDB: " + actualCustomerCount);
        
        System.out.println("Expected Item Orders: " + expectedItemOrderCount);
        System.out.println("Actual Item Orders in MongoDB: " + actualItemOrderCount);

        if (actualCustomerCount == expectedCustomerCount && actualItemOrderCount == expectedItemOrderCount) {
            System.out.println("Transfer Successful: All records transferred correctly!");
        } else {
            System.out.println("Transfer Incomplete: Mismatch in record counts!");
        }
    }
}