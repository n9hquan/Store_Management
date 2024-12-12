package BackEnd.Server;

import com.sun.net.httpserver.HttpServer;

import BackEnd.Database.Customer;
import BackEnd.Database.CustomerOrder;
import BackEnd.Database.ItemOrder;
import BackEnd.Database.Product;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.InetSocketAddress;
import java.net.URI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.concurrent.Executors;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocalhostServer {
    private static Connection connection;

    public static void main(String[] args) throws Exception {
        // Load MySQL JDBC driver
        Class.forName("com.mysql.cj.jdbc.Driver");
        
        // Establish database connection
        String dbUrl = "jdbc:mysql://localhost:3306/storedatabase";
        String username = "root";
        String password = "pass";
        connection = DriverManager.getConnection(dbUrl, username, password);

        // Create HTTP server on port 8080
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // Create contexts for different endpoints
        server.createContext("/products", new ProductHandler());
        server.createContext("/customers", new CustomerHandler());
        
        // Use a thread pool for handling requests
        server.setExecutor(Executors.newCachedThreadPool());
        
        server.start();
        System.out.println("Server running on http://localhost:" + port);
    }

    // Handler for Product-related requests
    static class ProductHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response;
            try {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                
                if ("GET".equals(method) && path.endsWith("/search")) {
                    // Handle product search
                    response = handleProductSearch(exchange);
                } else {
                    // Existing default product fetching logic
                    response = handleDefaultProductFetch(exchange);
                }
    
                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } catch (SQLException e) {
                response = "Database Error: " + e.getMessage();
                exchange.sendResponseHeaders(500, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    
        private String handleProductSearch(HttpExchange exchange) throws SQLException {
            // Parse query parameters
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = parseQuery(query);
            
            // Build dynamic SQL query
            StringBuilder sqlQuery = new StringBuilder("SELECT * FROM Product WHERE 1=1");
            List<Object> parameters = new ArrayList<>();
            
            if (params.containsKey("productId")) {
                sqlQuery.append(" AND ProductID = ?");
                parameters.add(Integer.parseInt(params.get("productId")));
            }
            if (params.containsKey("name")) {
                sqlQuery.append(" AND ProductName LIKE ?");
                parameters.add("%" + params.get("name") + "%");
            }
            if (params.containsKey("category")) {
                sqlQuery.append(" AND Category LIKE ?");
                parameters.add("%" + params.get("category") + "%");
            }
            if (params.containsKey("price")) {
                sqlQuery.append(" AND UnitPrice = ?");
                parameters.add(Double.parseDouble(params.get("price")));
            }
            if (params.containsKey("supplierId")) {
                sqlQuery.append(" AND SupplierID = ?");
                parameters.add(Integer.parseInt(params.get("supplierId")));
            }
            
            // Prepare and execute statement
            PreparedStatement stmt = connection.prepareStatement(sqlQuery.toString());
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            StringBuilder responseBuilder = new StringBuilder();
            
            while (rs.next()) {
                responseBuilder.append("ProductID:").append(rs.getInt("ProductID"))
                               .append(",ProductName:\"").append(rs.getString("ProductName")).append("\"")
                               .append(",UnitPrice:").append(rs.getDouble("UnitPrice"))
                               .append(",Category:\"").append(rs.getString("Category")).append("\"")
                               .append(",SupplierID:").append(rs.getInt("SupplierID"))
                               .append("\n");
            }
            
            return responseBuilder.toString();
        }
    
        private String handleDefaultProductFetch(HttpExchange exchange) throws SQLException {
            // Existing method to fetch default products
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT ProductID, ProductName, UnitPrice, Category, SupplierID FROM Product LIMIT 10"
            );
            ResultSet rs = stmt.executeQuery();
            
            StringBuilder responseBuilder = new StringBuilder();
            while (rs.next()) {
                responseBuilder.append("ProductID:").append(rs.getInt("ProductID"))
                               .append(",ProductName:\"").append(rs.getString("ProductName")).append("\"")
                               .append(",UnitPrice:").append(rs.getDouble("UnitPrice"))
                               .append(",Category:\"").append(rs.getString("Category")).append("\"")
                               .append(",SupplierID:").append(rs.getInt("SupplierID"))
                               .append("\n");
            }
            
            return responseBuilder.toString();
        }
    
        // Existing parseQuery method from previous implementation
        private Map<String, String> parseQuery(String query) {
            Map<String, String> params = new HashMap<>();
            if (query != null) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    try {
                        params.put(
                            URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8.toString()),
                            URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.toString())
                        );
                    } catch (UnsupportedEncodingException e) {
                        // Handle exception
                    }
                }
            }
            return params;
        }
    }

    // Handler for Customer-related requests
    static class CustomerHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response;
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            
            // Handle different types of requests
            if ("POST".equals(method) && path.endsWith("/register")) {
                // Handle customer registration
                response = handleCustomerRegistration(exchange);
            } else if ("GET".equals(method)) {
                // Handle fetching customers
                response = handleCustomerFetching(exchange);
            } else {
                response = "Unsupported method";
                exchange.sendResponseHeaders(405, response.getBytes().length);
            }

            // Send response
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (SQLException e) {
            response = "Database Error: " + e.getMessage();
            exchange.sendResponseHeaders(500, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    private String handleCustomerRegistration(HttpExchange exchange) throws IOException, SQLException {
        // Read the request body
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        
        // Parse query parameters
        Map<String, String> params = parseQuery(query);
        
        // Prepare and execute the insert statement
        PreparedStatement stmt = connection.prepareStatement(
            "INSERT INTO Customer (CustomerName, PhoneNumber, Email) VALUES (?, ?, ?)"
        );
        stmt.setString(1, params.get("name"));
        stmt.setString(2, params.get("phone"));
        stmt.setString(3, params.get("email"));
        
        int rowsAffected = stmt.executeUpdate();
        
        // Return response
        return (rowsAffected > 0) ? "success" : "failed";
    }

    private String handleCustomerFetching(HttpExchange exchange) throws SQLException {
        // Check if there's a specific customer ID in the query
        String query = exchange.getRequestURI().getQuery();
        PreparedStatement stmt;
        
        if (query != null && query.startsWith("id=")) {
            // Fetch specific customer
            int customerId = Integer.parseInt(query.substring(3));
            stmt = connection.prepareStatement(
                "SELECT CustomerID, CustomerName, Email, PhoneNumber FROM Customer WHERE CustomerID = ?"
            );
            stmt.setInt(1, customerId);
        } else {
            // Fetch first 10 customers
            stmt = connection.prepareStatement(
                "SELECT CustomerID, CustomerName, Email, PhoneNumber FROM Customer LIMIT 10"
            );
        }
        
        ResultSet rs = stmt.executeQuery();
        StringBuilder responseBuilder = new StringBuilder();
        
        while (rs.next()) {
            responseBuilder.append("CustomerID:").append(rs.getInt("CustomerID"))
                           .append(",CustomerName:\"").append(rs.getString("CustomerName")).append("\"")
                           .append(",Email:\"").append(rs.getString("Email")).append("\"")
                           .append(",PhoneNumber:\"").append(rs.getString("PhoneNumber")).append("\"\n");
        }
        
        return responseBuilder.toString();
    }

    // Helper method to parse query parameters
    private Map<String, String> parseQuery(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                try {
                    params.put(
                        URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8.toString()),
                        URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.toString())
                    );
                } catch (UnsupportedEncodingException e) {
                    // Handle exception
                }
            }
        }
        return params;
    }
}

    public Product loadProductID(int id) {
    try {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/products?id=" + id))
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        if (!responseBody.isEmpty() && !responseBody.contains("Error")) {
            // You might want to use a JSON parsing library like Gson or Jackson for more robust parsing
            String[] parts = responseBody.split(",");
            
            Product product = new Product();
            for (String part : parts) {
                if (part.contains("ProductID")) 
                    product.setProductID(Integer.parseInt(part.split(":")[1].trim()));
                else if (part.contains("ProductName"))
                    product.setName(part.split(":")[1].trim().replace("\"", ""));
                else if (part.contains("UnitPrice"))
                    product.setPrice(Double.parseDouble(part.split(":")[1].trim()));
                else if (part.contains("Category"))
                    product.setCategory(part.split(":")[1].trim().replace("\"", ""));
                else if (part.contains("SupplierID"))
                    product.setSupplierID(Integer.parseInt(part.split(":")[1].trim()));
            }

            return product;
            }
        } catch (Exception e) {
            System.out.println("Error fetching product: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    public boolean registerCustomer(Customer customer) {
    try {
        // Encode parameters to handle special characters
        String name = URLEncoder.encode(customer.getCustomerName(), StandardCharsets.UTF_8);
        String phone = URLEncoder.encode(customer.getPhoneNumber(), StandardCharsets.UTF_8);
        String email = URLEncoder.encode(customer.getEmail(), StandardCharsets.UTF_8);

        // Construct the request body
        String requestBody = String.format("name=%s&phone=%s&email=%s", name, phone, email);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/customers/register"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Check the response
        if (response.statusCode() == 200) {
            return response.body().trim().equalsIgnoreCase("success");
        } else {
            System.out.println("Server returned error: " + response.body());
            return false;
        }
    } catch (Exception e) {
        System.out.println("Error registering customer: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

    public List<Product> searchProductsByCriteria(Product product) {
    List<Product> matchingProducts = new ArrayList<>();
    
    try {
        // Build query parameters
        List<String> queryParams = new ArrayList<>();
        
        if (product.getProductID() > 0) 
            queryParams.add("productId=" + product.getProductID());
        
        if (product.getName() != null && !product.getName().isEmpty()) 
            queryParams.add("name=" + URLEncoder.encode(product.getName(), StandardCharsets.UTF_8));
        
        if (product.getCategory() != null && !product.getCategory().isEmpty()) 
            queryParams.add("category=" + URLEncoder.encode(product.getCategory(), StandardCharsets.UTF_8));
        
        if (product.getPrice() > 0) 
            queryParams.add("price=" + product.getPrice());
        
        if (product.getSupplierID() > 0) 
            queryParams.add("supplierId=" + product.getSupplierID());
        
        // Construct URL
        String queryString = queryParams.isEmpty() ? "" : "?" + String.join("&", queryParams);
        URI uri = URI.create("http://localhost:8080/products/search" + queryString);

        // Create HTTP client and request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .GET()
            .build();

        // Send request and get response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Parse response
        String responseBody = response.body();
        if (!responseBody.isEmpty() && !responseBody.contains("Error")) {
            // Split the response into individual product strings
            String[] productStrings = responseBody.split("\n");
            
            for (String productStr : productStrings) {
                if (!productStr.trim().isEmpty()) {
                    Product p = new Product();
                    
                    // Parse each product string
                    String[] attributes = productStr.split(",");
                    for (String attr : attributes) {
                        String[] keyValue = attr.split(":");
                        if (keyValue.length == 2) {
                            String key = keyValue[0].trim();
                            String value = keyValue[1].trim().replace("\"", "");
                            
                            switch (key) {
                                case "ProductID":
                                    p.setProductID(Integer.parseInt(value));
                                    break;
                                case "ProductName":
                                    p.setName(value);
                                    break;
                                case "UnitPrice":
                                    p.setPrice(Double.parseDouble(value));
                                    break;
                                case "Category":
                                    p.setCategory(value);
                                    break;
                                case "SupplierID":
                                    p.setSupplierID(Integer.parseInt(value));
                                    break;
                            }
                        }
                    }
                    
                    matchingProducts.add(p);
                }
            }
        }
    } catch (Exception e) {
        System.out.println("Error searching products: " + e.getMessage());
        e.printStackTrace();
    }
    
    return matchingProducts;
}

public List<Customer> searchCustomersByCriteria(Customer customer) {
    List<Customer> matchingCustomers = new ArrayList<>();
    try {
        // Build the query parameters for the HTTP GET request
        List<String> queryParams = new ArrayList<>();
        if (customer.getCustomerID() > 0) {
            queryParams.add("id=" + customer.getCustomerID());
        }
        if (customer.getCustomerName() != null && !customer.getCustomerName().isEmpty()) {
            queryParams.add("name=" + URLEncoder.encode(customer.getCustomerName(), StandardCharsets.UTF_8));
        }
        if (customer.getPhoneNumber() != null && !customer.getPhoneNumber().isEmpty()) {
            queryParams.add("phone=" + URLEncoder.encode(customer.getPhoneNumber(), StandardCharsets.UTF_8));
        }
        if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
            queryParams.add("email=" + URLEncoder.encode(customer.getEmail(), StandardCharsets.UTF_8));
        }

        // Construct the full URL with query parameters
        String queryString = queryParams.isEmpty() ? "" : "?" + String.join("&", queryParams);
        String url = "http://localhost:8080/customers/search" + queryString;

        // Send the GET request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse the response body
        String responseBody = response.body();
        if (responseBody != null && !responseBody.isEmpty()) {
            String[] customerStrings = responseBody.split("\n");
            for (String customerStr : customerStrings) {
                if (!customerStr.trim().isEmpty()) {
                    Customer c = new Customer();
                    String[] attributes = customerStr.split(",");
                    for (String attr : attributes) {
                        String[] keyValue = attr.split(":");
                        if (keyValue.length == 2) {
                            String key = keyValue[0].trim();
                            String value = keyValue[1].trim().replace("\"", "");
                            switch (key) {
                                case "CustomerID":
                                    c.setCustomerID(Integer.parseInt(value));
                                    break;
                                case "CustomerName":
                                    c.setCustomerName(value);
                                    break;
                                case "PhoneNumber":
                                    c.setPhoneNumber(value);
                                    break;
                                case "Email":
                                    c.setEmail(value);
                                    break;
                            }
                        }
                    }
                    matchingCustomers.add(c);
                }
            }
        }
    } catch (Exception e) {
        System.out.println("Error searching customers by criteria!");
        e.printStackTrace();
    }

    return matchingCustomers;
}


public boolean customerExistsByName(String customerName) {
    try {
        // Encode the customer name to handle special characters in the URL
        String encodedName = URLEncoder.encode(customerName, StandardCharsets.UTF_8);
        
        // Create the URL for the GET request
        String url = "http://localhost:8080/customers/search?name=" + encodedName;

        // Create an HTTP client and request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check the response body for any matching customers
        String responseBody = response.body();
        return responseBody != null && !responseBody.isEmpty();
    } catch (Exception e) {
        System.out.println("Error checking customer existence by name!");
        e.printStackTrace();
    }
    return false;
}


public boolean productExistsByName(String productName) {
    try {
        // Encode the product name to handle special characters in the URL
        String encodedProductName = URLEncoder.encode(productName, StandardCharsets.UTF_8);

        // Construct the URL for the GET request
        String url = "http://localhost:8080/products/search?name=" + encodedProductName;

        // Create an HTTP client and request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check the response body for any matching products
        String responseBody = response.body();
        return responseBody != null && !responseBody.isEmpty();
    } catch (Exception e) {
        System.out.println("Error checking product existence by name!");
        e.printStackTrace();
    }
    return false;
}

public double getProductUnitPrice(String productName) {
    double unitPrice = 0.0;
    try {
        // Encode the product name to handle special characters in the URL
        String encodedProductName = URLEncoder.encode(productName, StandardCharsets.UTF_8);

        // Construct the URL for the GET request
        String url = "http://localhost:8080/products/search?name=" + encodedProductName;

        // Create an HTTP client and request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse the response body
        String responseBody = response.body();
        if (responseBody != null && !responseBody.isEmpty()) {
            // Assuming the response contains product details in text format
            String[] productDetails = responseBody.split(",");
            for (String detail : productDetails) {
                if (detail.startsWith("UnitPrice:")) {
                    unitPrice = Double.parseDouble(detail.split(":")[1].trim());
                    break;
                }
            }
        }
    } catch (Exception e) {
        System.out.println("Error fetching product unit price!");
        e.printStackTrace();
    }
    
    return unitPrice;
}

public int getCustomerIdByName(String customerName) {
    int customerId = -1;
    try {
        // Encode the customer name to handle special characters in the URL
        String encodedCustomerName = URLEncoder.encode(customerName, StandardCharsets.UTF_8);

        // Construct the URL for the GET request
        String url = "http://localhost:8080/customers/search?name=" + encodedCustomerName;

        // Create an HTTP client and request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse the response body
        String responseBody = response.body();
        if (responseBody != null && !responseBody.isEmpty()) {
            // Assuming the response contains customer details in text format
            String[] customerDetails = responseBody.split(",");
            for (String detail : customerDetails) {
                if (detail.startsWith("CustomerID:")) {
                    customerId = Integer.parseInt(detail.split(":")[1].trim());
                    break;
                }
            }
        }
    } catch (Exception e) {
        System.out.println("Error fetching customer ID!");
        e.printStackTrace();
    }

    return customerId;
}

public int getProductIdByName(String productName) {
    int productId = -1;
    try {
        // Encode the product name to handle special characters in the URL
        String encodedProductName = URLEncoder.encode(productName, StandardCharsets.UTF_8);

        // Construct the URL for the GET request
        String url = "http://localhost:8080/products/search?name=" + encodedProductName;

        // Create an HTTP client and request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse the response body
        String responseBody = response.body();
        if (responseBody != null && !responseBody.isEmpty()) {
            // Assuming the response contains product details in text format
            String[] productDetails = responseBody.split(",");
            for (String detail : productDetails) {
                if (detail.startsWith("ProductID:")) {
                    productId = Integer.parseInt(detail.split(":")[1].trim());
                    break;
                }
            }
        }
    } catch (Exception e) {
        System.out.println("Error fetching product ID!");
        e.printStackTrace();
    }

    return productId;
}

public int insertCustomerOrder(int customerId, String orderDate, double totalOrderPrice) {
    int orderId = -1;
    try {
        // Encode parameters to handle special characters in the URL
        String encodedOrderDate = URLEncoder.encode(orderDate, StandardCharsets.UTF_8);
        String url = "http://localhost:8080/orders/create?customerId=" + customerId + "&orderDate=" + encodedOrderDate + "&totalOrderPrice=" + totalOrderPrice;

        // Create an HTTP client and request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .POST(HttpRequest.BodyPublishers.noBody())  // Using POST as the method for creation
            .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check if the response contains a valid order ID
        String responseBody = response.body();
        if (responseBody != null && !responseBody.isEmpty()) {
            try {
                // Assuming the server responds with the newly created order ID
                orderId = Integer.parseInt(responseBody);
            } catch (NumberFormatException e) {
                System.out.println("Error: The server did not return a valid order ID.");
                e.printStackTrace();
            }
        }
    } catch (Exception e) {
        System.out.println("Error while inserting customer order!");
        e.printStackTrace();
    }
    
    return orderId;
}

public boolean insertItemOrder(int orderId, int productId, int quantity, double unitPrice, double totalPrice) {
    try {
        // Encode parameters to handle special characters in the URL
        String url = "http://localhost:8080/orders/item/create?" +
                     "orderId=" + orderId + "&" +
                     "productId=" + productId + "&" +
                     "quantity=" + quantity + "&" +
                     "unitPrice=" + unitPrice + "&" +
                     "totalPrice=" + totalPrice;

        // Create an HTTP client and request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .POST(HttpRequest.BodyPublishers.noBody())  // POST method to create the item order
            .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check if the response indicates success
        String responseBody = response.body();
        if (responseBody != null && !responseBody.isEmpty()) {
            // Assuming the server responds with a success message or status
            if (responseBody.equals("success")) {
                return true;
            }
        }
    } catch (Exception e) {
        System.out.println("Error while inserting item order!");
        e.printStackTrace();
    }

    return false;
}

public boolean updateCustomer(Customer customer) {
    try {
        // Encode customer data to handle special characters in the URL
        String encodedPhoneNumber = URLEncoder.encode(customer.getPhoneNumber(), StandardCharsets.UTF_8);
        String encodedEmail = URLEncoder.encode(customer.getEmail(), StandardCharsets.UTF_8);
        String encodedCustomerName = URLEncoder.encode(customer.getCustomerName(), StandardCharsets.UTF_8);

        // Construct the URL for the PUT request
        String url = "http://localhost:8080/customers/update?" +
                     "name=" + encodedCustomerName + "&" +
                     "phone=" + encodedPhoneNumber + "&" +
                     "email=" + encodedEmail;

        // Create an HTTP client and request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .PUT(HttpRequest.BodyPublishers.noBody())  // Using PUT as the method for updating
            .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check if the response indicates success
        String responseBody = response.body();
        if (responseBody != null && !responseBody.isEmpty()) {
            if (responseBody.equals("success")) {
                return true;
            }
        }
    } catch (Exception e) {
        System.out.println("Error while updating customer!");
        e.printStackTrace();
    }

    return false;
}
public boolean updateCustomerPayment(Customer customer) {
    try {
        // Encode customer data to handle special characters in the URL
        String encodedPaymentType = URLEncoder.encode(customer.getPaymentType(), StandardCharsets.UTF_8);
        String encodedCardNumber = URLEncoder.encode(customer.getCardNumber(), StandardCharsets.UTF_8);
        String encodedExpirationDate = URLEncoder.encode(customer.getExpirationDate(), StandardCharsets.UTF_8);
        
        // Construct the URL for the PUT request
        String url = "http://localhost:8080/customers/payment/update?" +
                     "customerId=" + customer.getCustomerID() + "&" +
                     "paymentType=" + encodedPaymentType + "&" +
                     "cardNumber=" + encodedCardNumber + "&" +
                     "expirationDate=" + encodedExpirationDate;

        // Create an HTTP client and request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .PUT(HttpRequest.BodyPublishers.noBody())  // PUT method to update the customer payment
            .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check if the response indicates success
        String responseBody = response.body();
        if (responseBody != null && !responseBody.isEmpty()) {
            if (responseBody.equals("success")) {
                return true;
            }
        }
    } catch (Exception e) {
        System.out.println("Error while updating customer payment information!");
        e.printStackTrace();
    }

    return false;
}
public boolean decreaseProductQuantity(int productId, int quantityOrdered) {
    try {
        // Construct the URL for the PUT request
        String url = "http://localhost:8080/products/update/quantity?" +
                     "productId=" + productId + "&" +
                     "quantityOrdered=" + quantityOrdered;

        // Create an HTTP client and request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .PUT(HttpRequest.BodyPublishers.noBody())  // PUT method to update product quantity
            .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check if the response indicates success
        String responseBody = response.body();
        if (responseBody != null && !responseBody.isEmpty()) {
            if (responseBody.equals("success")) {
                return true;  // Return true if the quantity update was successful
            }
        }
    } catch (Exception e) {
        System.out.println("Error while decreasing product quantity!");
        e.printStackTrace();
    }

    return false;  // Return false if the update failed
}
public boolean increaseProductQuantity(int productId, int quantityOrdered) {
    try {
        // Construct the URL for the PUT request
        String url = "http://localhost:8080/products/update/quantity/increase?" +
                     "productId=" + productId + "&" +
                     "quantityOrdered=" + quantityOrdered;

        // Create an HTTP client and request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .PUT(HttpRequest.BodyPublishers.noBody())  // PUT method to increase product quantity
            .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check if the response indicates success
        String responseBody = response.body();
        if (responseBody != null && !responseBody.isEmpty()) {
            if (responseBody.equals("success")) {
                return true;  // Return true if the quantity increase was successful
            }
        }
    } catch (Exception e) {
        System.out.println("Error while increasing product quantity!");
        e.printStackTrace();
    }

    return false;  // Return false if the update failed
}
public boolean updateProductInventory(Product product) {
    try {
        // Construct the URL for the PUT request
        String url = "http://localhost:8080/products/update?" +
                     "productId=" + product.getProductID() + "&" +
                     "productName=" + URLEncoder.encode(
                         product.getName() != null ? product.getName() : "", StandardCharsets.UTF_8) + "&" +
                     "unitPrice=" + (product.getPrice() > 0 ? product.getPrice() : 0) + "&" +
                     "category=" + URLEncoder.encode(
                         product.getCategory() != null ? product.getCategory() : "", StandardCharsets.UTF_8) + "&" +
                     "supplierId=" + (product.getSupplierID() > 0 ? product.getSupplierID() : 0) + "&" +
                     "quantity=" + product.getQuantity();

        // Create an HTTP client and request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .PUT(HttpRequest.BodyPublishers.noBody())  // Using PUT as the method for updating the product
            .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Check if the response indicates success
        String responseBody = response.body();
        if (responseBody != null && !responseBody.isEmpty()) {
            return responseBody.equals("success");  // Return true if the update was successful
        }
    } catch (Exception e) {
        System.out.println("Error while updating product inventory!");
        e.printStackTrace();
    }

    return false;  // Return false if the update failed
}
public CustomerOrder loadOrderById(int orderId) {
    try {
        // Construct the URL for the GET request
        String url = "http://localhost:8080/orders/load?orderId=" + orderId;

        // Create an HTTP client and request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse the response body
        String responseBody = response.body();
        if (responseBody != null && !responseBody.isEmpty()) {
            // Assuming the server responds with the order data in a string format
            String[] orderDetails = responseBody.split(",");
            if (orderDetails.length > 0) {
                CustomerOrder order = new CustomerOrder();
                for (String detail : orderDetails) {
                    String[] keyValue = detail.split(":");
                    if (keyValue.length == 2) {
                        switch (keyValue[0].trim()) {
                            case "OrderID":
                                order.setOrderId(Integer.parseInt(keyValue[1].trim()));
                                break;
                            case "CustomerID":
                                order.setCustomerId(Integer.parseInt(keyValue[1].trim()));
                                break;
                            case "OrderDate":
                                order.setOrderDate(keyValue[1].trim());
                                break;
                            case "TotalOrderPrice":
                                order.setTotalPrice(Double.parseDouble(keyValue[1].trim()));
                                break;
                        }
                    }
                }
                return order;
            }
        }
    } catch (Exception e) {
        System.out.println("Error while loading order by ID!");
        e.printStackTrace();
    }

    return null;  // Return null if the order was not found or there was an error
}
public List<ItemOrder> loadItemsByOrderId(int orderId) {
    List<ItemOrder> items = new ArrayList<>();
    try {
        // Construct the URL for the GET request
        String url = "http://localhost:8080/orders/items/load?orderId=" + orderId;

        // Create an HTTP client and request
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();

        // Send the request and get the response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse the response body
        String responseBody = response.body();
        if (responseBody != null && !responseBody.isEmpty()) {
            String[] itemStrings = responseBody.split("\n");
            for (String itemStr : itemStrings) {
                if (!itemStr.trim().isEmpty()) {
                    ItemOrder item = new ItemOrder();
                    String[] attributes = itemStr.split(",");
                    for (String attr : attributes) {
                        String[] keyValue = attr.split(":");
                        if (keyValue.length == 2) {
                            String key = keyValue[0].trim();
                            String value = keyValue[1].trim().replace("\"", "");

                            switch (key) {
                                case "ItemOrderID":
                                    item.setItemOrderId(Integer.parseInt(value));
                                    break;
                                case "OrderID":
                                    item.setOrderId(Integer.parseInt(value));
                                    break;
                                case "ProductID":
                                    item.setProductId(Integer.parseInt(value));
                                    break;
                                case "Quantity":
                                    item.setQuantity(Integer.parseInt(value));
                                    break;
                                case "UnitPrice":
                                    item.setUnitPrice(Double.parseDouble(value));
                                    break;
                                case "TotalPrice":
                                    item.setTotalPrice(Double.parseDouble(value));
                                    break;
                            }
                        }
                    }
                    items.add(item);
                }
            }
        }
    } catch (Exception e) {
        System.out.println("Error while loading items by order ID!");
        e.printStackTrace();
    }

    return items;
}


    // Cleanup method (optional, but recommended)
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing database connection!");
            e.printStackTrace();
        }
    }
}