import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:mysql://localhost:3306/order_db?useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Mặc định XAMPP không có mật khẩu MySQL

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

