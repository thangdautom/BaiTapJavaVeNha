import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    public void addProduct(String name, double price) throws SQLException {
        String sql = "INSERT INTO products (name, price) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.executeUpdate();
        }
    }

    // ✅ Bổ sung phương thức getAllProducts()
    public List<Product> getAllProducts() throws SQLException {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT id, name, price FROM products";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                );
                productList.add(product);
            }
        }

        return productList;
    }
}
