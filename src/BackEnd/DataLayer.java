package BackEnd;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import BackEnd.Database.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DataLayer {

    private static final String SERVER_URL = "http://localhost:8080";
    private Gson gson = new Gson();

    // Example: Fetch a product by ID
    public Product loadProductID(int id) {
        try {
            String endpoint = SERVER_URL + "/product?id=" + id;
            String response = sendHttpRequest("GET", endpoint, null);
            return gson.fromJson(response, Product.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Example: Register a new customer
    public boolean registerCustomer(Customer customer) {
        try {
            String endpoint = SERVER_URL + "/customer";
            String requestBody = gson.toJson(customer);
            String response = sendHttpRequest("POST", endpoint, requestBody);
            return response.equalsIgnoreCase("Customer registered successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Example: Search for customers by criteria
    public List<Customer> searchCustomersByCriteria(Customer customer) {
        try {
            String endpoint = SERVER_URL + "/customer/search";
            String requestBody = gson.toJson(customer);
            String response = sendHttpRequest("POST", endpoint, requestBody);
            return gson.fromJson(response, new TypeToken<List<Customer>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    // Example: Check if a customer exists by name
    public boolean customerExistsByName(String customerName) {
        try {
            String endpoint = SERVER_URL + "/customer?name=" + customerName;
            String response = sendHttpRequest("GET", endpoint, null);
            return response.equalsIgnoreCase("Customer exists");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Product> searchProductsByCriteria(Product product) {
        try {
            String endpoint = SERVER_URL + "/product/search";
            String requestBody = gson.toJson(product);
            String response = sendHttpRequest("POST", endpoint, requestBody);
            return gson.fromJson(response, new TypeToken<List<Product>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    
    public boolean productExistsByName(String productName) {
        try {
            String endpoint = SERVER_URL + "/product?name=" + productName;
            String response = sendHttpRequest("GET", endpoint, null);
            return response.equalsIgnoreCase("Product exists");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getProductUnitPrice(String productName) {
        try {
            String endpoint = SERVER_URL + "/product/unitPrice?name=" + productName;
            String response = sendHttpRequest("GET", endpoint, null);
            return Double.parseDouble(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public int insertCustomerOrder(int customerId, String orderDate, double totalOrderPrice) {
        try {
            String endpoint = SERVER_URL + "/order";
            String requestBody = gson.toJson(new CustomerOrder(customerId, orderDate, totalOrderPrice));
            String response = sendHttpRequest("POST", endpoint, requestBody);
            return Integer.parseInt(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean insertItemOrder(int orderId, int productId, int quantity, double unitPrice, double totalPrice) {
        try {
            String endpoint = SERVER_URL + "/itemOrder";
            ItemOrder itemOrder = new ItemOrder(orderId, productId, quantity, unitPrice, totalPrice);
            String requestBody = gson.toJson(itemOrder);
            String response = sendHttpRequest("POST", endpoint, requestBody);
            return response.equalsIgnoreCase("Item order inserted successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public CustomerOrder loadOrderById(int orderId) {
        try {
            String endpoint = SERVER_URL + "/order?id=" + orderId;
            String response = sendHttpRequest("GET", endpoint, null);
            return gson.fromJson(response, CustomerOrder.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ItemOrder> loadItemsByOrderId(int orderId) {
        try {
            String endpoint = SERVER_URL + "/itemOrder?orderId=" + orderId;
            String response = sendHttpRequest("GET", endpoint, null);
            return gson.fromJson(response, new TypeToken<List<ItemOrder>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public boolean updateCustomer(Customer customer) {
        try {
            String endpoint = SERVER_URL + "/customer";
            String requestBody = gson.toJson(customer);
            String response = sendHttpRequest("PUT", endpoint, requestBody);
            return response.equalsIgnoreCase("Customer updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean decreaseProductQuantity(int productId, int quantityOrdered) {
        try {
            String endpoint = SERVER_URL + "/product/decrease";
            String requestBody = gson.toJson(new ProductUpdate(productId, -quantityOrdered));
            String response = sendHttpRequest("PUT", endpoint, requestBody);
            return response.equalsIgnoreCase("Product quantity updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean increaseProductQuantity(int productId, int quantityOrdered) {
        try {
            String endpoint = SERVER_URL + "/product/increase";
            String requestBody = gson.toJson(new ProductUpdate(productId, quantityOrdered));
            String response = sendHttpRequest("PUT", endpoint, requestBody);
            return response.equalsIgnoreCase("Product quantity updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public int getProductIdByName(String productName) {
        try {
            String endpoint = SERVER_URL + "/product/id?name=" + productName;
            String response = sendHttpRequest("GET", endpoint, null);
            return Integer.parseInt(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if the product is not found or an error occurs
    }

    public int getCustomerIdByName(String customerName) {
        try {
            String endpoint = SERVER_URL + "/customer/id?name=" + customerName;
            String response = sendHttpRequest("GET", endpoint, null);
            return Integer.parseInt(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if the customer is not found or an error occurs
    }

    public boolean updateCustomerPayment(Customer customer) {
        try {
            String endpoint = SERVER_URL + "/customer/payment";
            String requestBody = gson.toJson(customer);
            String response = sendHttpRequest("PUT", endpoint, requestBody);
            return response.equalsIgnoreCase("Customer payment updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateProductInventory(Product product) {
        try {
            String endpoint = SERVER_URL + "/product/inventory";
            String requestBody = gson.toJson(product);
            String response = sendHttpRequest("PUT", endpoint, requestBody);
            return response.equalsIgnoreCase("Product inventory updated successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<SalesRecord> getSalesRecords(SalesFilter filter) {
        try {
            String endpoint = SERVER_URL + "/salesReport";
            String requestBody = gson.toJson(filter);
            String response = sendHttpRequest("POST", endpoint, requestBody);
            return gson.fromJson(response, new TypeToken<List<SalesRecord>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    

    // Utility method to send HTTP requests
    private String sendHttpRequest(String method, String endpoint, String requestBody) throws IOException {
        try {
            // Use URI to build and convert to URL
            URI uri = URI.create(endpoint);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");

            // Send request body for POST/PUT methods
            if (requestBody != null && (method.equals("POST") || method.equals("PUT"))) {
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            // Read response
            int responseCode = connection.getResponseCode();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    responseCode == 200 ? connection.getInputStream() : connection.getErrorStream(),
                    StandardCharsets.UTF_8))) {
                String response = br.lines().collect(Collectors.joining("\n"));
                if (responseCode != 200) {
                    throw new IOException("HTTP error code: " + responseCode + "\nResponse: " + response);
                }
                return response;
            } finally {
                connection.disconnect();
            }
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid URL: " + endpoint, e);
        }
    }
}
