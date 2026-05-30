import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FoodMenuPanel extends JPanel {
    private final Color BG_COLOR = new Color(0x1E, 0x1E, 0x24);
    private final Color CARD_COLOR = new Color(0x2A, 0x2A, 0x32);
    private final Color ACCENT_COLOR = new Color(0xFF, 0x6B, 0x6B);
    private final Color TEXT_COLOR = new Color(0xFF, 0xFF, 0xFF);

    private List<CartItem> cartList;

    public FoodMenuPanel(List<CartItem> cartList) {
        this.cartList = cartList;
        setBackground(BG_COLOR);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Explore Our Delicious Menu");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel();
        gridPanel.setBackground(BG_COLOR);
        gridPanel.setLayout(new GridLayout(0, 3, 20, 20));

        for (MenuItem item : getSampleMenu()) {
            gridPanel.add(createFoodCard(item));
        }

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createFoodCard(MenuItem item) {
        JPanel card = new JPanel();
        card.setBackground(CARD_COLOR);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel catLabel = new JLabel(item.getCategory().toUpperCase());
        catLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        catLabel.setForeground(ACCENT_COLOR);

        JLabel nameLabel = new JLabel(item.getItemName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_COLOR);

        JLabel priceLabel = new JLabel("LKR " + String.format("%.2f", item.getPrice()));
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        priceLabel.setForeground(new Color(0xBB, 0xBB, 0xBB));

        JButton addToCartBtn = new JButton("Add to Cart");
        addToCartBtn.setMaximumSize(new Dimension(200, 35));
        addToCartBtn.setBackground(ACCENT_COLOR);
        addToCartBtn.setForeground(TEXT_COLOR);
        addToCartBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addToCartBtn.setFocusPainted(false);
        addToCartBtn.setBorderPainted(false);
        addToCartBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        addToCartBtn.addActionListener(e -> {
            boolean exists = false;
            for (CartItem cartItem : cartList) {
                if (cartItem.getMenuItem().getItemID() == item.getItemID()) {
                    cartItem.incrementQuantity();
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                cartList.add(new CartItem(item, 1));
            }
            JOptionPane.showMessageDialog(this, item.getItemName() + " added to cart!");
        });

        card.add(catLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(nameLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(priceLabel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(addToCartBtn);

        return card;
    }

    private List<MenuItem> getSampleMenu() {
        List<MenuItem> list = new ArrayList<>();
        list.add(new MenuItem(1, "Crispy Chicken Burger", 850.00, "Burger"));
        list.add(new MenuItem(2, "Cheese Blast Pizza", 1850.00, "Pizza"));
        list.add(new MenuItem(3, "Submarine Sandwich", 720.00, "Submarine"));
        list.add(new MenuItem(4, "Spicy Seafood Rice", 1200.00, "Rice"));
        list.add(new MenuItem(5, "Chocolate Milkshake", 450.00, "Beverage"));
        list.add(new MenuItem(6, "Peri Peri French Fries", 380.00, "Sides"));
        return list;
    }
}