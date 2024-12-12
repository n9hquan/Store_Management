package BackEnd.MongoDB;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBLayer {
    private MongoClient mongoClient;
    private MongoDatabase database;

    public MongoDBLayer() {
        try {
            // Use the connection string with password placeholder
            String connectionString = "mongodb+srv://quannguyen220066:<password>@storedatabasemongodb.icjmf.mongodb.net/";
            
            // IMPORTANT: Replace <password> with your actual password
            connectionString = connectionString.replace("<password>", "pass");
            
            ConnectionString connString = new ConnectionString(connectionString);
            
            MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .retryWrites(true)
                .build();

            this.mongoClient = MongoClients.create(settings);
            
            // Explicitly specify the database name
            this.database = mongoClient.getDatabase("StoreDatabaseMongoDB");
        } catch (Exception e) {
            System.err.println("Failed to connect to MongoDB: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("MongoDB connection error", e);
        }
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    // Optional: Method to check database connection and collections
    public void printDatabaseInfo() {
        System.out.println("Connected to Database: " + database.getName());
        System.out.println("Existing Collections:");
        for (String collectionName : database.listCollectionNames()) {
            System.out.println("- " + collectionName);
        }
    }
}