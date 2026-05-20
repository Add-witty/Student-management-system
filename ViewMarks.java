import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewMarks extends JFrame {
    private JTable marksTable;
    private DefaultTableModel tableModel;

    public ViewMarks(int studentId) {
        setTitle("Your Marks");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(new String[]{"Subject", "CCA", "LCA", "Final"}, 0);
        marksTable = new JTable(tableModel);
        add(new JScrollPane(marksTable), BorderLayout.CENTER);

        loadMarks(studentId);

        setVisible(true);
    }

    private void loadMarks(int studentId) {
        try (Connection conn = Database.connect()) {
            String query = "SELECT subject, cca, lca, final FROM marks WHERE student_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getString("subject"),
                        rs.getInt("cca"),
                        rs.getInt("lca"),
                        rs.getInt("final")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load marks.");
        }
    }
}
