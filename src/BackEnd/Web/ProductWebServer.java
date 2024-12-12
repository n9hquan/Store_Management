package BackEnd.Web;

import java.io.*;
import java.net.*;
import java.util.*;
import BackEnd.DataLayer;
import BackEnd.Database.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ProductWebServer {
    private static final int PORT = 8080;
    private static DataLayer dataLayer;
    private static Gson gson;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            dataLayer = new DataLayer();
            gson = new GsonBuilder().setPrettyPrinting().create();
            
            System.out.println("Server running on port " + PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    handleRequest(clientSocket);
                } catch (IOException e) {
                    System.err.println("Error handling client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + PORT);
            e.printStackTrace();
        }
    }

    private static void handleRequest(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        
        // Read the request line
        String requestLine = in.readLine();
        if (requestLine == null) return;

        // Parse the request
        String[] requestParts = requestLine.split(" ");
        if (requestParts.length < 3) return;

        String method = requestParts[0];
        String path = requestParts[1];
        
        // Skip headers
        while (in.ready() && !in.readLine().isEmpty()) {}

        // Route the request
        if (method.equals("GET")) {
            if (path.startsWith("/search")) {
                handleProductSearch(path, out);
            } else if (path.startsWith("/products/")) {
                handleProductDetails(path, out);
            } else if (path.startsWith("/users/")) {
                handleUserDetails(path, out);
            } else if (path.startsWith("/orders/")) {
                handleOrderDetails(path, out);
            } else {
                sendErrorResponse(out, 404, "Not Found");
            }
        } else {
            sendErrorResponse(out, 405, "Method Not Allowed");
        }

        clientSocket.close();
    }

    private static void handleProductSearch(String path, PrintWriter out) {
        try {
            // Parse query parameters
            Map<String, String> params = parseQueryParams(path);
            Product searchCriteria = new Product();

            // Set search criteria based on available parameters
            if (params.containsKey("product_id")) {
                searchCriteria.setProductID(Integer.parseInt(params.get("product_id")));
            }
            if (params.containsKey("name")) {
                searchCriteria.setName(params.get("name"));
            }
            if (params.containsKey("category")) {
                searchCriteria.setCategory(params.get("category"));
            }
            if (params.containsKey("price")) {
                searchCriteria.setPrice(Double.parseDouble(params.get("price")));
            }
            if (params.containsKey("supplier_id")) {
                searchCriteria.setSupplierID(Integer.parseInt(params.get("supplier_id")));
            }

            // Perform search
            List<Product> products = dataLayer.searchProductsByCriteria(searchCriteria);

            // Determine response type based on Accept header (simplified)
            if (path.contains("format=json")) {
                sendJsonResponse(out, products);
            } else {
                sendHtmlProductSearchResponse(out, products);
            }
        } catch (Exception e) {
            sendErrorResponse(out, 400, "Bad Request: " + e.getMessage());
        }
    }

    private static void handleProductDetails(String path, PrintWriter out) {
        try {
            // Extract product ID from path
            int productId = Integer.parseInt(path.substring("/products/".length()));
            
            // Fetch product details
            Product product = dataLayer.loadProductID(productId);
            
            if (product != null) {
                sendJsonResponse(out, product);
            } else {
                sendErrorResponse(out, 404, "Product Not Found");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(out, 400, "Invalid Product ID");
        }
    }

    private static void handleUserDetails(String path, PrintWriter out) {
        try {
            // Extract customer ID from path
            int customerId = Integer.parseInt(path.substring("/users/".length()));
            
            // Create search criteria
            Customer searchCriteria = new Customer();
            searchCriteria.setCustomerID(customerId);
            
            // Search for customer
            List<Customer> customers = dataLayer.searchCustomersByCriteria(searchCriteria);
            
            if (!customers.isEmpty()) {
                sendJsonResponse(out, customers.get(0));
            } else {
                sendErrorResponse(out, 404, "Customer Not Found");
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(out, 400, "Invalid Customer ID");
        }
    }

    private static void handleOrderDetails(String path, PrintWriter out) {
        try {
            // Extract order ID from the path
            int orderId = Integer.parseInt(path.substring("/orders/".length()));
    
            // Fetch order details
            CustomerOrder order = dataLayer.loadOrderById(orderId);
            if (order == null) {
                sendErrorResponse(out, 404, "Order Not Found");
                return;
            }
    
            // Fetch items associated with the order
            List<ItemOrder> items = dataLayer.loadItemsByOrderId(orderId);
    
            // Build a response object
            Map<String, Object> response = new HashMap<>();
            response.put("order", order);
            response.put("items", items);
    
            // Send the response in JSON format
            sendJsonResponse(out, response);
        } catch (NumberFormatException e) {
            sendErrorResponse(out, 400, "Invalid Order ID");
        } catch (Exception e) {
            sendErrorResponse(out, 500, "Internal Server Error: " + e.getMessage());
        }
    }
    

    private static void sendHtmlProductSearchResponse(PrintWriter out, List<Product> products) {
        StringBuilder htmlResponse = new StringBuilder();
        htmlResponse.append("HTTP/1.1 200 OK\r\n")
                   .append("Content-Type: text/html\r\n\r\n")
                   .append("<html><body>")
                   .append("<h1>Product Search Results</h1>")
                   .append("<table border='1'>")
                   .append("<tr><th>ID</th><th>Name</th><th>Price</th><th>Category</th><th>Supplier ID</th></tr>");

        for (Product product : products) {
            htmlResponse.append("<tr>")
                       .append("<td>").append(product.getProductID()).append("</td>")
                       .append("<td>").append(product.getName()).append("</td>")
                       .append("<td>").append(product.getPrice()).append("</td>")
                       .append("<td>").append(product.getCategory()).append("</td>")
                       .append("<td>").append(product.getSupplierID()).append("</td>")
                       .append("</tr>");
        }

        htmlResponse.append("</table></body></html>");
        out.println(htmlResponse.toString());
    }

    private static void sendJsonResponse(PrintWriter out, Object data) {
        String jsonResponse = gson.toJson(data);
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: application/json");
        out.println("Content-Length: " + jsonResponse.length());
        out.println();
        out.println(jsonResponse);
    }

    private static void sendErrorResponse(PrintWriter out, int statusCode, String message) {
        out.println("HTTP/1.1 " + statusCode + " " + message);
        out.println("Content-Type: text/plain");
        out.println();
        out.println(message);
    }

    private static Map<String, String> parseQueryParams(String path) {
        Map<String, String> params = new HashMap<>();
        int queryIndex = path.indexOf('?');
        if (queryIndex != -1) {
            String queryString = path.substring(queryIndex + 1);
            for (String param : queryString.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }
}