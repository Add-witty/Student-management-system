import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class ViewAssignmentsPanel extends JPanel {
    private JComboBox<String> subjectBox;
    private JTable assignmentTable;
    private DefaultTableModel tableModel;
    private int studentId;

    public ViewAssignmentsPanel(int studentId) {
        this.studentId = studentId;
        setLayout(new BorderLayout(10, 10));

        // Top Panel
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Subject:"));
        subjectBox = new JComboBox<>();
        loadSubjects();
        topPanel.add(subjectBox);

        JButton viewButton = new JButton("View Assignments");
        topPanel.add(viewButton);
        add(topPanel, BorderLayout.NORTH);

        // Table Setup
        tableModel = new DefaultTableModel();
        assignmentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(assignmentTable);
        add(scrollPane, BorderLayout.CENTER);

        // View button logic
        viewButton.addActionListener(e -> loadAssignmentData());
    }

    private void loadSubjects() {
        try (Connection conn = Database.connect()) {
            String sql = "SELECT DISTINCT subject FROM assignments WHERE student_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                subjectBox.addItem(rs.getString("subject"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading subjects: " + e.getMessage());
        }
    }

    private void loadAssignmentData() {
        String subject = (String) subjectBox.getSelectedItem();
        if (subject == null) return;

        try (Connection conn = Database.connect()) {
            String sql = "SELECT assignment_number, assignment_name, status FROM assignments WHERE student_id = ? AND subject = ? ORDER BY assignment_number";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, studentId);
            stmt.setString(2, subject);
            ResultSet rs = stmt.executeQuery();

            Vector<String> columns = new Vector<>(Arrays.asList("Assignment No.", "Assignment Name", "Status"));
            Vector<Vector<String>> data = new Vector<>();

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add("A" + rs.getInt("assignment_number"));
                row.add(rs.getString("assignment_name"));
                row.add(rs.getString("status"));
                data.add(row);
            }

            tableModel.setDataVector(data, columns);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading assignments: " + e.getMessage());
        }
    }
}
