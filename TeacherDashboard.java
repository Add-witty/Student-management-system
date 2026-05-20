import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.Border;

public class TeacherDashboard extends JFrame {
    private String teacherName;

    // Red-themed color palette
    private final Color primaryColor = new Color(198, 40, 40);
    private final Color secondaryColor = new Color(66, 66, 66);
    private final Color backgroundColor = new Color(252, 228, 236);
    private final Color buttonHoverColor = new Color(183, 28, 28);
    private final Color buttonTextColor = Color.WHITE;
    private final Color panelBackgroundColor = Color.WHITE;

    private final Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
    private final Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
    private final Font infoFont = new Font("Segoe UI", Font.PLAIN, 12);

    public TeacherDashboard(String teacherName) {
        this.teacherName = teacherName;
        setTitle("Teacher Dashboard - " + teacherName);
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(backgroundColor);

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel headerLabel = new JLabel("Teacher Dashboard");
        headerLabel.setFont(titleFont.deriveFont(20f));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        // Main Panel
        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JButton addStudentButton = createDashboardButton("Add/Delete Student");
        JButton noticeButton = createDashboardButton("Write Notice");
        JButton marksButton = createDashboardButton("Manage Marks");
        JButton assignmentButton = createDashboardButton("Edit/View Assignments");
        JButton eventsButton = createDashboardButton("Create Event");

        mainPanel.add(addStudentButton);
        mainPanel.add(noticeButton);
        mainPanel.add(marksButton);
        mainPanel.add(assignmentButton);
        mainPanel.add(eventsButton);

        // Footer
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(backgroundColor);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel footerLabel = new JLabel("Logged in as: " + teacherName);
        footerLabel.setFont(infoFont);
        footerLabel.setForeground(secondaryColor);
        footerPanel.add(footerLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Log Out");
        logoutButton.setFont(buttonFont);
        logoutButton.setBackground(primaryColor);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setFocusPainted(false);

        logoutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutButton.setBackground(buttonHoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                logoutButton.setBackground(primaryColor);
            }
        });

        logoutButton.addActionListener(e -> {
            dispose(); // Close current window
            new LoginPage(); // Open login page
        });

        footerPanel.add(logoutButton, BorderLayout.EAST);

        // Add to frame
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        // Button Actions
        addStudentButton.addActionListener(e -> new ManageStudents());
        noticeButton.addActionListener(e -> new AddNotice());
        marksButton.addActionListener(e -> new ManageMarks());
        eventsButton.addActionListener(e -> new CreateEvent());
        assignmentButton.addActionListener(e -> {
            JFrame frame = new JFrame("Assignment Manager");
            frame.setContentPane(new AssignmentPanel());
            frame.setSize(500, 400);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        });

        setVisible(true);
    }

    private JButton createDashboardButton(String text) {
        JButton button = new JButton(text);
        button.setFont(buttonFont);
        button.setForeground(secondaryColor);
        button.setBackground(panelBackgroundColor);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);

        Border lineBorder = BorderFactory.createLineBorder(primaryColor.darker(), 1);
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
            new TeacherDashboard("Prof. Agrawal");
        });
    }
}
