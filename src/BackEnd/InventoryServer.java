package BackEnd;
import com.sun.net.httpserver.HttpServer;

import BackEnd.Database.*;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class InventoryServer {
    private static final int PORT = 8080;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/storedatabase";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "pass";
    private static Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        
        // Product Endpoints
        server.createContext("/product", new ProductHandler());
        server.createContext("/product/search", new ProductSearchHandler());
        server.createContext("/product/decrease", new ProductDecreaseHandler());
        server.createContext("/product/increase", new ProductIncreaseHandler());
        server.createContext("/product/id", new ProductIdHandler());
        server.createContext("/product/unitPrice", new ProductUnitPriceHandler());
        server.createContext("/product/inventory", new ProductInventoryHandler());

        // Customer Endpoints
        server.createContext("/customer", new CustomerHandler());
        server.createContext("/customer/search", new CustomerSearchHandler());
        server.createContext("/customer/id", new CustomerIdHandler());
        server.createContext("/customer/payment", new CustomerPaymentHandler());

        // Order Endpoints
        server.createContext("/order", new OrderHandler());
        server.createContext("/itemOrder", new ItemOrderHandler());

        // Sales Report Endpoint
        server.createContext("/salesReport", new SalesReportHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Server running on port " + PORT);
    }

    // Product Handlers
    static class ProductHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                if ("GET".equals(exchange.getRequestMethod())) {
                    String query = exchange.getRequestURI().getQuery();
                    if (query != null && query.startsWith("id=")) {
                        int id = Integer.parseInt(query.substring(3));
                        String sql = "SELECT * FROM products WHERE product_id = ?";
                        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            pstmt.setInt(1, id);
                            try (ResultSet rs = pstmt.executeQuery()) {
                                if (rs.next()) {
                                    Product product = new Product(
                                        rs.getInt("product_id"),
                                        rs.getString("product_name"),
                                        rs.getDouble("unit_price"),
                                        rs.getInt("quantity")
                                    );
                                    sendResponse(exchange, gson.toJson(product));
                                }
                            }
                        }
                    } else if (query != null && query.startsWith("name=")) {
                        String productName = query.substring(5);
                        String sql = "SELECT COUNT(*) FROM products WHERE product_name = ?";
                        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            pstmt.setString(1, productName);
                            try (ResultSet rs = pstmt.executeQuery()) {
                                if (rs.next() && rs.getInt(1) > 0) {
                                    sendResponse(exchange, "Product exists");
                                } else {
                                    sendResponse(exchange, "Product does not exist");
                                }
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, "Database error: " + e.getMessage());
            }
        }
    }

    static class ProductSearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendErrorResponse(exchange, "Method not allowed");
                return;
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                    .lines().collect(Collectors.joining("\n"));
                
                Product searchProduct = gson.fromJson(requestBody, Product.class);
                
                StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM products WHERE 1=1");
                List<Object> params = new ArrayList<>();
                
                if (searchProduct.getProductName() != null) {
                    sqlBuilder.append(" AND product_name LIKE ?");
                    params.add("%" + searchProduct.getProductName() + "%");
                }
                if (searchProduct.getUnitPrice() > 0) {
                    sqlBuilder.append(" AND unit_price <= ?");
                    params.add(searchProduct.getUnitPrice());
                }
                if (searchProduct.getQuantity() > 0) {
                    sqlBuilder.append(" AND quantity >= ?");
                    params.add(searchProduct.getQuantity());
                }
                
                try (PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {
                    for (int i = 0; i < params.size(); i++) {
                        pstmt.setObject(i + 1, params.get(i));
                    }
                    
                    try (ResultSet rs = pstmt.executeQuery()) {
                        List<Product> products = new ArrayList<>();
                        while (rs.next()) {
                            Product product = new Product(
                                rs.getInt("product_id"),
                                rs.getString("product_name"),
                                rs.getDouble("unit_price"),
                                rs.getInt("quantity")
                            );
                            products.add(product);
                        }
                        sendResponse(exchange, gson.toJson(products));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, "Database error: " + e.getMessage());
            }
        }
    }

    static class ProductIdHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                if ("GET".equals(exchange.getRequestMethod())) {
                    String query = exchange.getRequestURI().getQuery();
                    if (query != null && query.startsWith("name=")) {
                        String productName = query.substring(5);
                        String sql = "SELECT product_id FROM products WHERE product_name = ?";
                        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            pstmt.setString(1, productName);
                            try (ResultSet rs = pstmt.executeQuery()) {
                                if (rs.next()) {
                                    sendResponse(exchange, String.valueOf(rs.getInt("product_id")));
                                } else {
                                    sendErrorResponse(exchange, "Product not found");
                                }
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, "Database error: " + e.getMessage());
            }
        }
    }

    static class ProductUnitPriceHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                if ("GET".equals(exchange.getRequestMethod())) {
                    String query = exchange.getRequestURI().getQuery();
                    if (query != null && query.startsWith("name=")) {
                        String productName = query.substring(5);
                        String sql = "SELECT unit_price FROM products WHERE product_name = ?";
                        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            pstmt.setString(1, productName);
                            try (ResultSet rs = pstmt.executeQuery()) {
                                if (rs.next()) {
                                    sendResponse(exchange, String.valueOf(rs.getDouble("unit_price")));
                                } else {
                                    sendErrorResponse(exchange, "Product not found");
                                }
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, "Database error: " + e.getMessage());
            }
        }
    }

    static class ProductDecreaseHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"PUT".equals(exchange.getRequestMethod())) {
                sendErrorResponse(exchange, "Method not allowed");
                return;
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                    .lines().collect(Collectors.joining("\n"));
                
                ProductUpdate update = gson.fromJson(requestBody, ProductUpdate.class);
                
                String sql = "UPDATE products SET quantity = quantity + ? WHERE product_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, update.getQuantityChange());
                    pstmt.setInt(2, update.getProductId());
                    
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        sendResponse(exchange, "Product quantity updated successfully");
                    } else {
                        sendErrorResponse(exchange, "Product not found");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, "Database error: " + e.getMessage());
            }
        }
    }

    static class ProductInventoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"PUT".equals(exchange.getRequestMethod())) {
                sendErrorResponse(exchange, "Method not allowed");
                return;
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                    .lines().collect(Collectors.joining("\n"));
                
                Product product = gson.fromJson(requestBody, Product.class);
                
                String sql = "UPDATE products SET quantity = ?, unit_price = ? WHERE product_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, product.getQuantity());
                    pstmt.setDouble(2, product.getUnitPrice());
                    pstmt.setInt(3, product.getProductId());
                    
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        sendResponse(exchange, "Product inventory updated successfully");
                    } else {
                        sendErrorResponse(exchange, "Product not found");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, "Database error: " + e.getMessage());
            }
        }
    }

    // Customer Handlers
    static class CustomerHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                switch (exchange.getRequestMethod()) {
                    case "POST":
                        handleCustomerRegistration(exchange, conn);
                        break;
                    case "GET":
                        handleCustomerExistence(exchange, conn);
                        break;
                    case "PUT":
                        handleCustomerUpdate(exchange, conn);
                        break;
                    default:
                        sendErrorResponse(exchange, "Method not allowed");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, "Database error: " + e.getMessage());
            }
        }

        private void handleCustomerRegistration(HttpExchange exchange, Connection conn) throws IOException, SQLException {
            String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                .lines().collect(Collectors.joining("\n"));
            
            Customer customer = gson.fromJson(requestBody, Customer.class);
            
            String sql = "INSERT INTO customers (customer_name, email, phone, payment_amount) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, customer.getCustomerName());
                pstmt.setString(2, customer.getEmail());
                pstmt.setString(3, customer.getPhone());
                pstmt.setDouble(4, customer.getPaymentAmount());
                
                pstmt.executeUpdate();
                sendResponse(exchange, "Customer registered successfully");
            }
        }

        private void handleCustomerExistence(HttpExchange exchange, Connection conn) throws IOException, SQLException {
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.startsWith("name=")) {
                String customerName = query.substring(5);
                String sql = "SELECT COUNT(*) FROM customers WHERE customer_name = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, customerName);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            sendResponse(exchange, "Customer exists");
                        } else {
                            sendResponse(exchange, "Customer does not exist");
                        }
                    }
                }
            }
        }

        private void handleCustomerUpdate(HttpExchange exchange, Connection conn) throws IOException, SQLException {
            String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                .lines().collect(Collectors.joining("\n"));
            
            Customer customer = gson.fromJson(requestBody, Customer.class);
            
            String sql = "UPDATE customers SET email = ?, phone = ? WHERE customer_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, customer.getEmail());
                pstmt.setString(2, customer.getPhone());
                pstmt.setInt(3, customer.getCustomerId());
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    sendResponse(exchange, "Customer updated successfully");
                } else {
                    sendErrorResponse(exchange, "Customer not found");
                }
            }
        }
    }

    static class CustomerIdHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                if ("GET".equals(exchange.getRequestMethod())) {
                    String query = exchange.getRequestURI().getQuery();
                    if (query != null && query.startsWith("name=")) {
                        String customerName = query.substring(5);
                        String sql = "SELECT customer_id FROM customers WHERE customer_name = ?";
                        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            pstmt.setString(1, customerName);
                            try (ResultSet rs = pstmt.executeQuery()) {
                                if (rs.next()) {
                                    sendResponse(exchange, String.valueOf(rs.getInt("customer_id")));
                                } else {
                                    sendErrorResponse(exchange, "Customer not found");
                                }
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, "Database error: " + e.getMessage());
            }
        }
    }

    // Order Handlers
    static class OrderHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                switch (exchange.getRequestMethod()) {
                    case "POST":
                        handleOrderInsertion(exchange, conn);
                        break;
                    case "GET":
                        handleOrderRetrieval(exchange, conn);
                        break;
                    default:
                        sendErrorResponse(exchange, "Method not allowed");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, "Database error: " + e.getMessage());
            }
        }

        private void handleOrderInsertion(HttpExchange exchange, Connection conn) throws IOException, SQLException {
            String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                .lines().collect(Collectors.joining("\n"));
            
            CustomerOrder order = gson.fromJson(requestBody, CustomerOrder.class);
            
            String sql = "INSERT INTO orders (customer_id, order_date, total_price) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, order.getCustomerId());
                pstmt.setString(2, order.getOrderDate());
                pstmt.setDouble(3, order.getTotalOrderPrice());
                
                pstmt.executeUpdate();
                
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        sendResponse(exchange, String.valueOf(generatedKeys.getInt(1)));
                    } else {
                        sendErrorResponse(exchange, "Failed to create order");
                    }
                }
            }
        }

        private void handleOrderRetrieval(HttpExchange exchange, Connection conn) throws IOException, SQLException {
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.startsWith("id=")) {
                int orderId = Integer.parseInt(query.substring(3));
                String sql = "SELECT * FROM orders WHERE order_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, orderId);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            CustomerOrder order = new CustomerOrder(
                                rs.getInt("order_id"),
                                rs.getInt("customer_id"),
                                rs.getString("order_date"),
                                rs.getDouble("total_price")
                            );
                            sendResponse(exchange, gson.toJson(order));
                        } else {
                            sendErrorResponse(exchange, "Order not found");
                        }
                    }
                }
            }
        }
    }

    static class ItemOrderHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
                switch (exchange.getRequestMethod()) {
                    case "POST":
                        handleItemOrderInsertion(exchange, conn);
                        break;
                    case "GET":
                        handleItemOrderRetrieval(exchange, conn);
                        break;
                    default:
                        sendErrorResponse(exchange, "Method not allowed");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, "Database error: " + e.getMessage());
            }
        }

        private void handleItemOrderInsertion(HttpExchange exchange, Connection conn) throws IOException, SQLException {
            String requestBody = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
                .lines().collect(Collectors.joining("\n"));
            
            ItemOrder itemOrder = gson.fromJson(requestBody, ItemOrder.class);
            
            String sql = "INSERT INTO item_orders (order_id, product_id, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, itemOrder.getOrderId());
                pstmt.setInt(2, itemOrder.getProductId());
                pstmt.setInt(3, itemOrder.getQuantity());
                pstmt.setDouble(4, itemOrder.getUnitPrice());
                pstmt.setDouble(5, itemOrder.getTotalPrice());
                
                pstmt.executeUpdate();
                sendResponse(exchange, "Item order inserted successfully");
            }
        }
    // Implement other handlers similarly...
    // (ProductDecreaseHandler, ProductIncreaseHandler, etc.)

    // Utility methods
    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private static void sendErrorResponse(HttpExchange exchange, String errorMessage) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(500, errorMessage.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(errorMessage.getBytes());
        }
    }

    // Model classes (simplified versions of what might be in your actual project)
    static class Product {
        private int productId;
        private String productName;
        private double unitPrice;
        private int quantity;

        // Constructor, getters, and setters
        public Product(int productId, String productName, double unitPrice, int quantity) {
            this.productId = productId;
            this.productName = productName;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
        }

        // Getters and setters
        public int getProductId() { return productId; }
        public String getProductName() { return productName; }
        public double getUnitPrice() { return unitPrice; }
        public int getQuantity() { return quantity; }
        public void setProductId(int productId) { this.productId = productId; }
        public void setProductName(String productName) { this.productName = productName; }
        public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    // Other model classes would be implemented similarly
    }
}