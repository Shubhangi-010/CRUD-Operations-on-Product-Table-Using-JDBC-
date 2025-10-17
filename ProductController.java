
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductController {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/product_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "password";

    private static final String INSERT_SQL = "INSERT INTO products (product_name, price, quantity) VALUES (?, ?, ?)";
    private static final String SELECT_ALL_SQL = "SELECT * FROM products";
    private static final String UPDATE_SQL = "UPDATE products SET product_name = ?, price = ?, quantity = ? WHERE product_id = ?";
    private static final String DELETE_SQL = "DELETE FROM products WHERE product_id = ?";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM products WHERE product_id = ?";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
    public boolean addProduct(Product product) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            ps.setString(1, product.getProductName());
            ps.setDouble(2, product.getPrice());
            ps.setInt(3, product.getQuantity());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding product: " + e.getMessage());
            return false;
        }
    }

    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(new Product(
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching products: " + e.getMessage());
        }
        return products;
    }

    // Update: Modify product details with transaction handling
    public boolean updateProduct(Product product) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            try (PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
                ps.setString(1, product.getProductName());
                ps.setDouble(2, product.getPrice());
                ps.setInt(3, product.getQuantity());
                ps.setInt(4, product.getProductId());
                
                int affectedRows = ps.executeUpdate();
                
                if (affectedRows > 0) {
                    conn.commit(); // Commit if successful
                    return true;
                } else {
                    conn.rollback(); // Rollback if no rows were affected
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            if (conn != null) {
                try {
                    System.err.println("Transaction is rolling back...");
                    conn.rollback(); // Rollback on error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    // Delete: Remove a product by ID with transaction handling
    public boolean deleteProduct(int productId) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
                ps.setInt(1, productId);

                int affectedRows = ps.executeUpdate();

                if (affectedRows > 0) {
                    conn.commit();  
                    return true;
                } else {
                    conn.rollback(); 
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
            if (conn != null) {
                try {
                    System.err.println("Transaction is rolling back...");
                    conn.rollback();  
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}