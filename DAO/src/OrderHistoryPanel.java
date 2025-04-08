import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;

public class OrderHistoryPanel extends JPanel {
    private JTextField emailField;
    private JButton viewBtn;
    private JTextArea resultArea;
    private CustomerDAO customerDAO = new CustomerDAO();
    private OrderDAO orderDAO = new OrderDAO();

    public OrderHistoryPanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        emailField = new JTextField(20);
        viewBtn = new JButton("Xem đơn hàng");
        topPanel.add(new JLabel("Email khách hàng:"));
        topPanel.add(emailField);
        topPanel.add(viewBtn);

        resultArea = new JTextArea(15, 40);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        viewBtn.addActionListener(this::handleViewOrders);
    }

    private void handleViewOrders(ActionEvent e) {
        String email = emailField.getText();
        try {
            int customerId = customerDAO.getCustomerIdByEmail(email);
            List<String> orders = orderDAO.getOrderHistory(customerId);
            resultArea.setText("");
            for (String order : orders) {
                resultArea.append(order + "\n");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng hoặc lỗi CSDL.");
        }
    }
}
