import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DashboardFrame extends JFrame {
    private final Color BG_COLOR = new Color(0x1E, 0x1E, 0x24);
    private final Color PANEL_COLOR = new Color(0x2A, 0x2A, 0x32);
    private final Color ACCENT_COLOR = new Color(0xFF, 0x6B, 0x6B);
    private final Color DRIVER_COLOR = new Color(0x4E, 0x9F, 0x3D);
    private final Color TEXT_COLOR = new Color(0xFF, 0xFF, 0xFF);

    private JPanel contentPanel;
    private List<CartItem> globalCart = new ArrayList<>();
    private String userRole;

    public DashboardFrame(String role) {
        this.userRole = role;
        setTitle("Online Food Ordering System - " + role + " Panel");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setBackground(PANEL_COLOR);
        sidebar.setPreferredSize(new Dimension(220, 600));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(30, 15, 30, 15));

        JLabel logoLabel = new JLabel(role.toUpperCase() + " PORTAL");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoLabel.setForeground(role.equals("Driver") ? DRIVER_COLOR : ACCENT_COLOR);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logoLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 50)));

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BG_COLOR);

        if (role.equalsIgnoreCase("Admin")) {
            setupAdminUI(sidebar);
        } else if (role.equalsIgnoreCase("Driver")) {
            setupDriverUI(sidebar);
        } else {
            setupCustomerUI(sidebar);
        }

        JButton logoutBtn = new JButton("LOGOUT");
        logoutBtn.setMaximumSize(new Dimension(190, 40));
        logoutBtn.setBackground(new Color(0xD9, 0x38, 0x38));
        logoutBtn.setForeground(TEXT_COLOR);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logoutBtn);

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void setupCustomerUI(JPanel sidebar) {
        JButton menuBtn = createSidebarButton("Browse Menu");
        JButton cartBtn = createSidebarButton("My Cart");

        menuBtn.addActionListener(e -> switchPanel(new FoodMenuPanel(globalCart)));
        cartBtn.addActionListener(e -> switchPanel(new CartPanel(globalCart)));

        sidebar.add(menuBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(cartBtn);

        setWelcomeMessage("Welcome Customer! Go to 'Browse Menu' to order delicious food.");
    }

    private void setupAdminUI(JPanel sidebar) {
        JButton viewOrdersBtn = createSidebarButton("Manage Orders");

        JPanel adminOrdersPanel = new JPanel();
        adminOrdersPanel.setBackground(BG_COLOR);
        adminOrdersPanel.setLayout(new BoxLayout(adminOrdersPanel, BoxLayout.Y_AXIS));
        adminOrdersPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        viewOrdersBtn.addActionListener(e -> {
            adminOrdersPanel.removeAll();
            loadOrdersFromDB(adminOrdersPanel, false);
            switchPanel(new JScrollPane(adminOrdersPanel) {{ setBorder(null); }});
        });

        sidebar.add(viewOrdersBtn);
        setWelcomeMessage("Welcome Admin! Click 'Manage Orders' to check customer actions.");
    }

    private void setupDriverUI(JPanel sidebar) {
        JButton deliveryBtn = createSidebarButton("Pending Deliveries");

        JPanel driverPanel = new JPanel();
        driverPanel.setBackground(BG_COLOR);
        driverPanel.setLayout(new BoxLayout(driverPanel, BoxLayout.Y_AXIS));
        driverPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        deliveryBtn.addActionListener(e -> {
            driverPanel.removeAll();
            loadOrdersFromDB(driverPanel, true);
            switchPanel(new JScrollPane(driverPanel) {{ setBorder(null); }});
        });

        sidebar.add(deliveryBtn);
        setWelcomeMessage("Welcome Delivery Rider! Click 'Pending Deliveries' to see routes.");
    }

    private void loadOrdersFromDB(JPanel panel, boolean isDriver) {
        String query = "SELECT * FROM orders ORDER BY order_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("order_id");
                JPanel row = new JPanel(new BorderLayout());
                row.setBackground(PANEL_COLOR);
                row.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
                row.setMaximumSize(new Dimension(650, 60));

                JLabel lblSummary = new JLabel("Order #" + id + ": " + rs.getString("items_summary"));
                lblSummary.setForeground(TEXT_COLOR);
                row.add(lblSummary, BorderLayout.WEST);

                if (isDriver) {
                    JButton doneBtn = new JButton("DELIVERED");
                    doneBtn.setBackground(DRIVER_COLOR);
                    doneBtn.setForeground(TEXT_COLOR);
                    doneBtn.setFocusPainted(false);
                    doneBtn.setBorderPainted(false);
                    doneBtn.addActionListener(e -> {
                        JOptionPane.showMessageDialog(this, "Order #" + id + " Status Updated: Delivered!");
                        row.setVisible(false);
                    });
                    row.add(doneBtn, BorderLayout.EAST);
                } else {
                    JLabel lblPrice = new JLabel("LKR " + rs.getDouble("total_amount"));
                    lblPrice.setForeground(ACCENT_COLOR);
                    lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    row.add(lblPrice, BorderLayout.EAST);
                }

                panel.add(row);
                panel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            panel.revalidate();
            panel.repaint();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void switchPanel(Container panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void setWelcomeMessage(String text) {
        JLabel msg = new JLabel(text, SwingConstants.CENTER);
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        msg.setForeground(TEXT_COLOR);
        contentPanel.add(msg, BorderLayout.CENTER);
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(190, 40));
        btn.setBackground(BG_COLOR);
        btn.setForeground(TEXT_COLOR);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}