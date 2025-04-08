import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerDAO {
    public void addCustomer(String name, String email) throws SQLException {
        String sql = "INSERT INTO customers (name, email) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.executeUpdate();
        }
    }

    // ✅ Thêm phương thức này
    public int getCustomerIdByEmail(String email) throws SQLException {
        String sql = "SELECT id FROM customers WHERE email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new SQLException("Không tìm thấy khách hàng với email: " + email);
            }
        }
    }
}
