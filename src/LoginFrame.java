import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private final Color BG_COLOR = new Color(0x1E, 0x1E, 0x24);
    private final Color PANEL_COLOR = new Color(0x2A, 0x2A, 0x32);
    private final Color ACCENT_COLOR = new Color(0xFF, 0x6B, 0x6B);
    private final Color TEXT_COLOR = new Color(0xFF, 0xFF, 0xFF);

    private JPanel mainCardPanel;
    private CardLayout cardLayout;

    public LoginFrame() {
        // App Title එක Food Ordering System ලෙස වෙනස් කරන ලදී
        setTitle("Welcome - Food Ordering System");
        setSize(420, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);

        mainCardPanel.add(createLoginPanel(), "LoginCard");
        mainCardPanel.add(createRegisterPanel(), "RegisterCard");

        add(mainCardPanel);
        cardLayout.show(mainCardPanel, "LoginCard");
    }

    // --- LOGIN PANEL UI & ACTION ---
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 35, 40, 35));

        // UI Header එක "LOGIN TO FOOD ORDERING SYSTEM" ලෙස වෙනස් කරන ලදී
        JLabel title = new JLabel("LOGIN TO FOOD ORDERING SYSTEM");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18)); // දිග වැඩි නිසා Font Size එක 18 දක්වා සකසන ලදී
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField txtUser = new JTextField();
        styleInput(txtUser);
        JPasswordField txtPass = new JPasswordField();
        styleInput(txtPass);

        JButton loginBtn = new JButton("LOGIN");
        styleButton(loginBtn);

        loginBtn.addActionListener(e -> {
            String username = txtUser.getText();
            String password = new String(txtPass.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String query = "SELECT role FROM users WHERE username = ? AND password = ?";

            try (Connection conn = DatabaseConnection.getConnection()) {
                if (conn == null) {
                    JOptionPane.showMessageDialog(this, "Database connection is null!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (PreparedStatement pst = conn.prepareStatement(query)) {
                    pst.setString(1, username);
                    pst.setString(2, password);

                    try (ResultSet rs = pst.executeQuery()) {
                        if (rs.next()) {
                            String role = rs.getString("role");
                            JOptionPane.showMessageDialog(this, "Success! Logged in as " + role, "Notification", JOptionPane.INFORMATION_MESSAGE);
                            new DashboardFrame(role).setVisible(true);
                            this.dispose();
                        } else {
                            JOptionPane.showMessageDialog(this, "Invalid username or password. Try again!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } catch (SQLException ex) {

                JOptionPane.showMessageDialog(this,
                        "Database Connection Error!\n\n" +
                                "Please make sure XAMPP Control Panel is OPEN and\n" +
                                "MySQL Service is started/running properly.\n\n" +
                                "Details: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton regSwitchLink = new JButton("Don't have an account? Register");
        styleLinkButton(regSwitchLink);
        regSwitchLink.addActionListener(e -> cardLayout.show(mainCardPanel, "RegisterCard"));

        panel.add(title); panel.add(Box.createRigidArea(new Dimension(0, 35)));
        panel.add(new JLabel("Username:") {{ setForeground(TEXT_COLOR); }}); panel.add(txtUser);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(new JLabel("Password:") {{ setForeground(TEXT_COLOR); }}); panel.add(txtPass);
        panel.add(Box.createRigidArea(new Dimension(0, 30))); panel.add(loginBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15))); panel.add(regSwitchLink);

        return panel;
    }

    // --- REGISTER PANEL UI & ACTION ---
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_COLOR);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 35, 40, 35));

        JLabel title = new JLabel("CREATE ACCOUNT");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_COLOR);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField txtUser = new JTextField();
        styleInput(txtUser);
        JPasswordField txtPass = new JPasswordField();
        styleInput(txtPass);

        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Customer", "Driver", "Admin"});
        roleCombo.setMaximumSize(new Dimension(350, 35));
        roleCombo.setBackground(BG_COLOR);
        roleCombo.setForeground(TEXT_COLOR);

        JButton regBtn = new JButton("REGISTER NOW");
        styleButton(regBtn);

        regBtn.addActionListener(e -> {
            String username = txtUser.getText();
            String password = new String(txtPass.getPassword());
            String role = roleCombo.getSelectedItem().toString();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fields cannot be empty!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection()) {
                if (conn == null) {
                    JOptionPane.showMessageDialog(this, "Database connection is null!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (PreparedStatement pst = conn.prepareStatement(query)) {
                    pst.setString(1, username);
                    pst.setString(2, password);
                    pst.setString(3, role);
                    pst.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Account Registered Successfully as " + role + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(mainCardPanel, "LoginCard");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Registration Failed!\n\n" +
                                "Please make sure XAMPP Control Panel is OPEN and\n" +
                                "MySQL Service is started/running properly.\n\n" +
                                "Details: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton loginSwitchLink = new JButton("Already have an account? Login");
        styleLinkButton(loginSwitchLink);
        loginSwitchLink.addActionListener(e -> cardLayout.show(mainCardPanel, "LoginCard"));

        panel.add(title); panel.add(Box.createRigidArea(new Dimension(0, 35)));
        panel.add(new JLabel("Username:") {{ setForeground(TEXT_COLOR); }}); panel.add(txtUser);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(new JLabel("Password:") {{ setForeground(TEXT_COLOR); }}); panel.add(txtPass);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(new JLabel("Select User Role:") {{ setForeground(TEXT_COLOR); }}); panel.add(roleCombo);
        panel.add(Box.createRigidArea(new Dimension(0, 30))); panel.add(regBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15))); panel.add(loginSwitchLink);

        return panel;
    }

    private void styleInput(JTextField field) {
        field.setMaximumSize(new Dimension(350, 35));
        field.setBackground(BG_COLOR);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    private void styleButton(JButton btn) {
        btn.setMaximumSize(new Dimension(350, 40));
        btn.setBackground(ACCENT_COLOR);
        btn.setForeground(TEXT_COLOR);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleLinkButton(JButton btn) {
        btn.setForeground(ACCENT_COLOR);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}