import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class ViewNotices extends JFrame {

    private JTextArea noticeArea;

    // --- UI Colors and Fonts ---
    private final Color primaryColor = new Color(25, 118, 210);      // Material Blue
    private final Color secondaryColor = new Color(66, 66, 66);      // Dark Gray
    private final Color backgroundColor = new Color(245, 245, 245);  // Light Gray
    private final Color panelBackgroundColor = Color.WHITE;          // White
    private final Color timestampColor = new Color(120, 120, 120);   // Light Gray

    private final Font titleFont = new Font("Segoe UI", Font.BOLD, 20);
    private final Font contentFont = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font timestampFont = new Font("Segoe UI", Font.ITALIC, 12);

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm");

    public ViewNotices() {
        setTitle("Notices");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(backgroundColor);

        // --- Header ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(primaryColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel headerLabel = new JLabel("University Notices");
        headerLabel.setFont(titleFont);
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        // --- Content Panel ---
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        noticeArea = new JTextArea();
        noticeArea.setEditable(false);
        noticeArea.setLineWrap(true);
        noticeArea.setWrapStyleWord(true);
        noticeArea.setFont(contentFont);
        noticeArea.setBackground(panelBackgroundColor);
        noticeArea.setForeground(secondaryColor);
        noticeArea.setMargin(new Insets(10, 15, 10, 15));

        JScrollPane scrollPane = new JScrollPane(noticeArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(primaryColor.brighter()));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Add Panels to Frame ---
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        loadNotices();

        setVisible(true);
    }

    private void loadNotices() {
        StringBuilder noticesText = new StringBuilder();
        boolean noticesFound = false;

        try (
            Connection conn = Database.connect();
            PreparedStatement stmt = conn.prepareStatement("SELECT notice_text, created_at FROM notices ORDER BY created_at DESC");
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                noticesFound = true;
                Timestamp timestamp = rs.getTimestamp("created_at");
                String formattedDate = (timestamp != null) ? dateFormat.format(timestamp) : "N/A";

                noticesText.append("----------------------------------------------------------\n");
                noticesText.append("Posted on: ").append(formattedDate).append("\n");
                noticesText.append("----------------------------------------------------------\n");
                noticesText.append(rs.getString("notice_text")).append("\n\n");
            }

            if (!noticesFound) {
                noticeArea.setForeground(timestampColor);
                noticeArea.setText("No notices found.");
            } else {
                noticeArea.setForeground(secondaryColor);
                noticeArea.setText(noticesText.toString());
                noticeArea.setCaretPosition(0); // scroll to top
            }

        } catch (SQLException e) {
            e.printStackTrace();
            noticeArea.setForeground(Color.RED);
            noticeArea.setText("Error loading notices.\n\nDetails: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            noticeArea.setForeground(Color.RED);
            noticeArea.setText("Unexpected error.\n\nDetails: " + e.getMessage());
        }
    }

    // Optional: for quick testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ViewNotices();
        });
    }
}
