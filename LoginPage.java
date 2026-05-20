import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;

    // Colors & Fonts
    private final Color primaryColor = new Color(25, 118, 210);
    private final Color secondaryColor = new Color(66, 66, 66);
    private final Color backgroundColor = new Color(245, 245, 245);
    private final Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
    private final Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font buttonFont = new Font("Segoe UI", Font.BOLD, 16);
    private final Font titleFont = new Font("Segoe UI", Font.BOLD, 24);

    public LoginPage() {
        setTitle("University Management System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(backgroundColor);

        // ==== Left Panel ====
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(primaryColor);
        leftPanel.setPreferredSize(new Dimension(350, 500));

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/mit.png"));
            JLabel logoLabel = new JLabel(icon);
            logoLabel.setHorizontalAlignment(JLabel.CENTER);
            leftPanel.add(logoLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            JPanel placeholder = new JPanel() {
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Segoe UI", Font.BOLD, 100));
                    g.drawString("UMS", 80, 250);
                }
            };
            placeholder.setOpaque(false);
            leftPanel.add(placeholder, BorderLayout.CENTER);
        }

        JPanel universityInfo = new JPanel(new GridLayout(2, 1));
        universityInfo.setOpaque(false);
        universityInfo.setBorder(BorderFactory.createEmptyBorder(0, 20, 30, 20));

        JLabel universityName = new JLabel("UNIVERSITY");
        universityName.setFont(new Font("Segoe UI", Font.BOLD, 28));
        universityName.setForeground(Color.WHITE);
        universityName.setHorizontalAlignment(JLabel.CENTER);

        JLabel slogan = new JLabel("Excellence in Education");
        slogan.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        slogan.setForeground(Color.WHITE);
        slogan.setHorizontalAlignment(JLabel.CENTER);

        universityInfo.add(universityName);
        universityInfo.add(slogan);
        leftPanel.add(universityInfo, BorderLayout.SOUTH);

        // ==== Right Panel ====
        JPanel rightPanel = new JPanel(new BorderLayout(0, 20));
        rightPanel.setBackground(backgroundColor);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Sign In");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(secondaryColor);
        titlePanel.add(titleLabel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // Username
        formPanel.add(createField("Username", usernameField = new JTextField()));
        // Password
        formPanel.add(createField("Password", passwordField = new JPasswordField()));
        // Role
        formPanel.add(createField("Login As", roleBox = new JComboBox<>(new String[]{"Teacher", "Student"})));

        // Login Button
        JButton loginButton = new JButton("LOGIN");
        loginButton.setFont(buttonFont);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(primaryColor);
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> authenticateUser());

        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(21, 101, 192));
            }

            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(primaryColor);
            }
        });

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        buttonPanel.add(loginButton, BorderLayout.CENTER);

        JPanel rightContent = new JPanel();
        rightContent.setLayout(new BoxLayout(rightContent, BoxLayout.Y_AXIS));
        rightContent.setOpaque(false);
        rightContent.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        rightContent.add(titlePanel);
        rightContent.add(formPanel);
        rightContent.add(buttonPanel);

        rightPanel.add(rightContent, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        label.setForeground(secondaryColor);

        if (field instanceof JTextField || field instanceof JPasswordField || field instanceof JComboBox) {
            field.setFont(fieldFont);
            field.setPreferredSize(new Dimension(200, 35));
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, primaryColor),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
        }

        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void authenticateUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleBox.getSelectedItem();
        String tableName = role.equals("Teacher") ? "teacher" : "student";

        try (Connection conn = Database.connect()) {
            if (conn != null) {
                String query = "SELECT * FROM " + tableName + " WHERE email = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login Successful!");
                    if (role.equals("Teacher")) {
                        new TeacherDashboard(username);
                    } else {
                        new StudentDashboard(username);
                    }
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials!");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error. Check console for details.");
        }
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}
