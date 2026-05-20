import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class EditMarksDialog extends JDialog {
    private JTextField ccaField, lcaField, finalField;
    private int studentId;

    public EditMarksDialog(JFrame parent, int studentId, String name, int cca, int lca, int finalMarks) {
        super(parent, "Edit Marks for " + name, true);
        this.studentId = studentId;
        setSize(300, 200);
        setLayout(new GridLayout(4, 2, 10, 10));

        add(new JLabel("CCA:"));
        ccaField = new JTextField(String.valueOf(cca));
        add(ccaField);

        add(new JLabel("LCA:"));
        lcaField = new JTextField(String.valueOf(lca));
        add(lcaField);

        add(new JLabel("Final:"));
        finalField = new JTextField(String.valueOf(finalMarks));
        add(finalField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveMarks());
        add(saveButton);

        setVisible(true);
    }

    private void saveMarks() {
        try (Connection conn = Database.connect()) {
            String query = "INSERT INTO marks (student_id, subject, cca, lca, final) VALUES (?, ?, ?, ?, ?) " +
                           "ON DUPLICATE KEY UPDATE cca = ?, lca = ?, final = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, studentId);
            stmt.setString(2, "TOC");
            stmt.setInt(3, Integer.parseInt(ccaField.getText()));
            stmt.setInt(4, Integer.parseInt(lcaField.getText()));
            stmt.setInt(5, Integer.parseInt(finalField.getText()));
            stmt.setInt(6, Integer.parseInt(ccaField.getText()));
            stmt.setInt(7, Integer.parseInt(lcaField.getText()));
            stmt.setInt(8, Integer.parseInt(finalField.getText()));

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Marks Updated Successfully!");
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
