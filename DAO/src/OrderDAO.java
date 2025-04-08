import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    public int createOrder(int customerId) throws SQLException {
        String sql = "INSERT INTO orders (customer_id, order_date) VALUES (?, NOW())";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, customerId);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }

    public double calculateTotal(int orderId) throws SQLException {
        String sql = """
            SELECT SUM(p.price * oi.quantity) AS total
            FROM order_items oi
            JOIN products p ON oi.product_id = p.id
            WHERE oi.order_id = ?
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getDouble("total") : 0.0;
        }
    }

    // ✅ Thêm phương thức này:
    public List<String> getOrderHistory(int customerId) throws SQLException {
        String sql = """
            SELECT o.id AS order_id, o.order_date, p.name AS product_name, oi.quantity, p.price
            FROM orders o
            JOIN order_items oi ON o.id = oi.order_id
            JOIN products p ON oi.product_id = p.id
            WHERE o.customer_id = ?
            ORDER BY o.order_date DESC
        """;

        List<String> history = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String date = rs.getString("order_date");
                String product = rs.getString("product_name");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");

                String line = String.format("Đơn %d (%s): %s x%d - %.2f VND",
                        orderId, date, product, quantity, price * quantity);
                history.add(line);
            }
        }

        return history;
    }
}
