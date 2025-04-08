import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Khởi động ứng dụng GUI trên luồng giao diện
        SwingUtilities.invokeLater(() -> {
            new MainGUI();  // Mở giao diện chính
        });
    }
}
