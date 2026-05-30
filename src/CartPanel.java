import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CartPanel extends JPanel {
    private final Color BG_COLOR = new Color(0x1E, 0x1E, 0x24);
    private final Color PANEL_COLOR = new Color(0x2A, 0x2A, 0x32);
    private final Color ACCENT_COLOR = new Color(0xFF, 0x6B, 0x6B);
    private final Color TEXT_COLOR = new Color(0xFF, 0xFF, 0xFF);

    private List<CartItem> cartList;
    private double grandTotal = 0.0;


    public CartPanel(List<CartItem> cartList) {
        this.cartList = cartList;
        setBackground(BG_COLOR);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Your Shopping Cart");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_COLOR);
        add(titleLabel, BorderLayout.NORTH);

        JPanel itemsListPanel = new JPanel();
        itemsListPanel.setBackground(BG_COLOR);
        itemsListPanel.setLayout(new BoxLayout(itemsListPanel, BoxLayout.Y_AXIS));

        for (CartItem item : cartList) {
            JPanel itemRow = new JPanel(new BorderLayout());
            itemRow.setBackground(PANEL_COLOR);
            itemRow.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            itemRow.setMaximumSize(new Dimension(800, 50));

            JLabel nameLabel = new JLabel(item.getMenuItem().getItemName() + " (x" + item.getQuantity() + ")");
            nameLabel.setForeground(TEXT_COLOR);
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            JLabel priceLabel = new JLabel("LKR " + String.format("%.2f", item.getTotalPrice()));
            priceLabel.setForeground(ACCENT_COLOR);
            priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

            itemRow.add(nameLabel, BorderLayout.WEST);
            itemRow.add(priceLabel, BorderLayout.EAST);

            itemsListPanel.add(itemRow);
            itemsListPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            grandTotal += item.getTotalPrice();
        }

        JScrollPane scrollPane = new JScrollPane(itemsListPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(PANEL_COLOR);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel totalLabel = new JLabel("Grand Total: LKR " + String.format("%.2f", grandTotal));
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(TEXT_COLOR);

        JButton placeOrderBtn = new JButton("PLACE ORDER NOW");
        placeOrderBtn.setBackground(ACCENT_COLOR);
        placeOrderBtn.setForeground(TEXT_COLOR);
        placeOrderBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        placeOrderBtn.setFocusPainted(false);
        placeOrderBtn.setBorderPainted(false);
        placeOrderBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        placeOrderBtn.addActionListener(e -> placeOrder());

        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(placeOrderBtn, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void placeOrder() {
        if (cartList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder summary = new StringBuilder();
        for (CartItem item : cartList) {
            summary.append(item.getMenuItem().getItemName()).append(" x").append(item.getQuantity()).append(", ");
        }

        String query = "INSERT INTO orders (items_summary, total_amount) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, summary.toString());
            pst.setDouble(2, grandTotal);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "🎉 Order Placed Successfully via Localhost!", "Success", JOptionPane.INFORMATION_MESSAGE);
            cartList.clear();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}