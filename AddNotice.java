import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddNotice extends JFrame {
    private JTextArea noticeArea;
    private JButton postButton;

    public AddNotice() {
        setTitle("Post Notice");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        noticeArea = new JTextArea();
        noticeArea.setLineWrap(true);
        noticeArea.setWrapStyleWord(true);
        add(new JScrollPane(noticeArea), BorderLayout.CENTER);

        postButton = new JButton("Post Notice");
        add(postButton, BorderLayout.SOUTH);

        postButton.addActionListener(e -> postNotice());

        setVisible(true);
    }

    private void postNotice() {
        String noticeText = noticeArea.getText().trim();
        if (noticeText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Notice cannot be empty.");
            return;
        }

        try (Connection conn = Database.connect()) {
            String query = "INSERT INTO notices (notice_text) VALUES (?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, noticeText);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Notice posted successfully!");
            noticeArea.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
