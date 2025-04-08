import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddOrderPanel extends JPanel {
    private JTextField customerNameField, customerEmailField;
    private DefaultListModel<Product> productListModel;
    private JList<Product> productList;
    private JTextField quantityField;
    private JTextArea cartArea;
    private JButton addToCartBtn, createOrderBtn;

    private CustomerDAO customerDAO = new CustomerDAO();
    private ProductDAO productDAO = new ProductDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private OrderItemDAO orderItemDAO = new OrderItemDAO();

    private ArrayList<OrderItem> cart = new ArrayList<>();

    public AddOrderPanel() {
        setLayout(new BorderLayout());

        // Customer panel
        JPanel customerPanel = new JPanel(new GridLayout(2, 2));
        customerNameField = new JTextField();
        customerEmailField = new JTextField();
        customerPanel.setBorder(BorderFactory.createTitledBorder("Khách hàng"));
        customerPanel.add(new JLabel("Tên khách hàng:"));
        customerPanel.add(customerNameField);
        customerPanel.add(new JLabel("Email khách hàng:"));
        customerPanel.add(customerEmailField);

        // Product list
        productListModel = new DefaultListModel<>();
        productList = new JList<>(productListModel);
        JScrollPane productScroll = new JScrollPane(productList);
        productScroll.setBorder(BorderFactory.createTitledBorder("Danh sách sản phẩm"));

        // Quantity + add button
        JPanel controlPanel = new JPanel(new FlowLayout());
        quantityField = new JTextField(5);
        addToCartBtn = new JButton("Thêm vào giỏ");
        controlPanel.add(new JLabel("Số lượng:"));
        controlPanel.add(quantityField);
        controlPanel.add(addToCartBtn);

        // Cart area
        cartArea = new JTextArea(8, 30);
        cartArea.setEditable(false);
        JScrollPane cartScroll = new JScrollPane(cartArea);
        cartScroll.setBorder(BorderFactory.createTitledBorder("Giỏ hàng"));

        // Create order button
        createOrderBtn = new JButton("Tạo đơn hàng");

        // Add to UI
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(productScroll, BorderLayout.CENTER);
        centerPanel.add(controlPanel, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(cartScroll, BorderLayout.CENTER);
        bottomPanel.add(createOrderBtn, BorderLayout.SOUTH);

        add(customerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load products
        loadProducts();

        // Events
        addToCartBtn.addActionListener(this::handleAddToCart);
        createOrderBtn.addActionListener(this::handleCreateOrder);
    }

    private void loadProducts() {
        try {
            List<Product> products = productDAO.getAllProducts();
            for (Product product : products) {
                productListModel.addElement(product);
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void handleAddToCart(ActionEvent e) {
        Product selected = productList.getSelectedValue();
        if (selected == null || quantityField.getText().isEmpty()) {
            showError("Vui lòng chọn sản phẩm và nhập số lượng.");
            return;
        }
        try {
            int quantity = Integer.parseInt(quantityField.getText());
            cart.add(new OrderItem(selected.getId(), quantity));
            cartArea.append("- " + selected.getName() + " x" + quantity + "\n");
        } catch (NumberFormatException ex) {
            showError("Số lượng không hợp lệ.");
        }
    }

    private void handleCreateOrder(ActionEvent e) {
        try {
            String name = customerNameField.getText();
            String email = customerEmailField.getText();
            customerDAO.addCustomer(name, email);
            int customerId = customerDAO.getCustomerIdByEmail(email);

            int orderId = orderDAO.createOrder(customerId);
            for (OrderItem item : cart) {
                orderItemDAO.addOrderItem(orderId, item.productId(), item.quantity());
            }

            double total = orderDAO.calculateTotal(orderId);
            JOptionPane.showMessageDialog(this, "Đơn hàng đã tạo! Tổng tiền: " + total + " VND");

            cart.clear();
            cartArea.setText("");
        } catch (SQLException ex) {
            showError(ex.getMessage());
        }
    }

    // ✅ Chỉ còn OrderItem (dùng làm cart)
    public record OrderItem(int productId, int quantity) {}

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
