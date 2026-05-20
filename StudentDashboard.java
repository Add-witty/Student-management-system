import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.Border;

public class StudentDashboard extends JFrame {

    private String studentUsername;

    // Colors and Fonts
    private final Color primaryColor = new Color(25, 118, 210);
    private final Color secondaryColor = new Color(66, 66, 66);
    private final Color backgroundColor = new Color(245, 245, 245);
    private final Color buttonHoverColor = new Color(21, 101, 192);
    private final Color buttonTextColor = Color.WHITE;
    private final Color panelBackgroundColor = Color.WHITE;

    private final Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
    private final Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private final Font infoFont = new Font("Segoe UI", Font.PLAIN, 12);

    public StudentDashboard(String studentUsername) {
        this.studentUsername = studentUsername;

        setTitle("Student Dashboard - " + studentUsername);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(backgroundColor);

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel headerLabel = new JLabel("Student Dashboard");
        headerLabel.setFont(titleFont.deriveFont(20f));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        // Main Panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // Buttons
        JButton noticeButton = createDashboardButton("View Notices");
        JButton marksButton = createDashboardButton("View Marks");
        JButton assignmentButton = createDashboardButton("View Assignment Status");
        JButton eventsButton = createDashboardButton("View Upcoming Events");

        // Spacer to push logout button to the right
headerPanel.add(Box.createHorizontalStrut(400)); // adjust as per your layout

// Logout Button
JButton logoutButton = new JButton("Logout");
logoutButton.setFont(buttonFont);
logoutButton.setForeground(buttonTextColor);
logoutButton.setBackground(new Color(198, 40, 40)); // red-ish logout color
logoutButton.setFocusPainted(false);
logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

logoutButton.addActionListener(e -> {
    dispose(); // Close the dashboard
    
    JOptionPane.showMessageDialog(null, "You have been logged out.");

    new LoginPage();
});

headerPanel.add(logoutButton);


        // Add buttons to panel (2 rows, 2 columns)
        gbc.gridx = 0; gbc.gridy = 0; mainPanel.add(noticeButton, gbc);
        gbc.gridx = 1; gbc.gridy = 0; mainPanel.add(marksButton, gbc);
        gbc.gridx = 0; gbc.gridy = 1; mainPanel.add(assignmentButton, gbc);
        gbc.gridx = 1; gbc.gridy = 1; mainPanel.add(eventsButton, gbc);

        // Actions
        noticeButton.addActionListener(e -> new ViewNotices().setVisible(true));
        marksButton.addActionListener(e -> new ViewMarks(WIDTH).setVisible(true));
        eventsButton.addActionListener(e -> new ViewEvents().setVisible(true));
        assignmentButton.addActionListener(e -> {
            JFrame assignmentFrame = new JFrame("View Assignments");
            assignmentFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            assignmentFrame.setSize(600, 400);
            assignmentFrame.setLocationRelativeTo(null);
            assignmentFrame.add(new ViewAssignmentsPanel(WIDTH));
            assignmentFrame.setVisible(true);
        });

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(backgroundColor);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel footerLabel = new JLabel("Logged in as: " + studentUsername);
        footerLabel.setFont(infoFont);
        footerLabel.setForeground(secondaryColor);
        footerPanel.add(footerLabel);

        // Add to frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private JButton createDashboardButton(String text) {
        JButton button = new JButton(text);
        button.setFont(buttonFont);
        button.setForeground(secondaryColor);
        button.setBackground(panelBackgroundColor);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);

        Border lineBorder = BorderFactory.createLineBorder(primaryColor.brighter(), 1);
        Border paddingBorder = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        button.setBorder(BorderFactory.createCompoundBorder(lineBorder, paddingBorder));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(primaryColor);
                button.setForeground(buttonTextColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(panelBackgroundColor);
                button.setForeground(secondaryColor);
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new StudentDashboard("test.student@example.com");
        });
    }
}
