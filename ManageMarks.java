import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ManageMarks extends JFrame {
    private JComboBox<String> subjectDropdown;
    private JTable marksTable;
    private DefaultTableModel tableModel;
    private JButton saveButton;
    private java.util.List<String> subjects = new ArrayList<>();

    public ManageMarks() {
        setTitle("Manage Marks");
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top Panel with Subject Dropdown and Add/Delete buttons
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Subject:"));

        subjectDropdown = new JComboBox<>();
        subjectDropdown.addActionListener(e -> loadStudentMarks());
        topPanel.add(subjectDropdown);

        JButton addSubjectButton = new JButton("Add Subject");
        JButton deleteSubjectButton = new JButton("Delete Subject");

        addSubjectButton.addActionListener(e -> addSubject());
        deleteSubjectButton.addActionListener(e -> deleteSubject());

        topPanel.add(addSubjectButton);
        topPanel.add(deleteSubjectButton);

        add(topPanel, BorderLayout.NORTH);

        // Table Setup
        String[] columns = {"Student ID", "Name", "CCA", "LCA", "Final", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 2 && column <= 4;
            }
        };

        marksTable = new JTable(tableModel);
        marksTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int row = marksTable.getSelectedRow();
                if (row != -1) updateTotal(row);
            }
        });

        add(new JScrollPane(marksTable), BorderLayout.CENTER);

        // Bottom Panel with Save Button
        saveButton = new JButton("Save Marks");
        saveButton.addActionListener(this::saveMarks);
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(saveButton);
        add(bottomPanel, BorderLayout.SOUTH);

        loadSubjects();
        setVisible(true);
    }

    private void loadSubjects() {
        // Load distinct subjects from marks table
        subjects.clear();
        subjectDropdown.removeAllItems();

        try (Connection conn = Database.connect()) {
            String query = "SELECT DISTINCT subject FROM marks";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String subject = rs.getString("subject");
                subjects.add(subject);
                subjectDropdown.addItem(subject);
            }

            // Load data if any subject exists
            if (!subjects.isEmpty()) {
                subjectDropdown.setSelectedIndex(0);
                loadStudentMarks();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addSubject() {
        String newSubject = JOptionPane.showInputDialog(this, "Enter new subject name:");
        if (newSubject != null && !newSubject.trim().isEmpty()) {
            if (!subjects.contains(newSubject)) {
                subjectDropdown.addItem(newSubject);
                subjectDropdown.setSelectedItem(newSubject);
                subjects.add(newSubject);
                JOptionPane.showMessageDialog(this, "Subject added.");
            } else {
                JOptionPane.showMessageDialog(this, "Subject already exists.");
            }
        }
    }

    private void deleteSubject() {
        String selected = (String) subjectDropdown.getSelectedItem();
        if (selected == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete subject \"" + selected + "\"?\nAll its marks will be deleted!",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = Database.connect()) {
                String query = "DELETE FROM marks WHERE subject = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, selected);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Subject deleted successfully.");

                loadSubjects();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadStudentMarks() {
        tableModel.setRowCount(0);
        HashSet<Integer> addedStudents = new HashSet<>();
        String selectedSubject = (String) subjectDropdown.getSelectedItem();
        if (selectedSubject == null) return;

        try (Connection conn = Database.connect()) {
            String query = "SELECT s.id, s.name, " +
                    "COALESCE(m.cca, 0) AS cca, COALESCE(m.lca, 0) AS lca, COALESCE(m.final, 0) AS final " +
                    "FROM student s " +
                    "LEFT JOIN marks m ON s.id = m.student_id AND m.subject = ? " +
                    "ORDER BY s.id";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, selectedSubject);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int studentId = rs.getInt("id");
                if (addedStudents.contains(studentId)) continue;
                addedStudents.add(studentId);

                String name = rs.getString("name");
                int cca = rs.getInt("cca");
                int lca = rs.getInt("lca");
                int finalMarks = rs.getInt("final");
                int total = cca + lca + finalMarks;

                tableModel.addRow(new Object[]{studentId, name, cca, lca, finalMarks, total});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTotal(int row) {
        try {
            int cca = Integer.parseInt(tableModel.getValueAt(row, 2).toString());
            int lca = Integer.parseInt(tableModel.getValueAt(row, 3).toString());
            int finalMarks = Integer.parseInt(tableModel.getValueAt(row, 4).toString());
            tableModel.setValueAt(cca + lca + finalMarks, row, 5);
        } catch (NumberFormatException e) {
            tableModel.setValueAt(0, row, 5);
        }
    }

    private void saveMarks(ActionEvent e) {
        String selectedSubject = (String) subjectDropdown.getSelectedItem();
        if (selectedSubject == null) return;

        try (Connection conn = Database.connect()) {
            String query = "INSERT INTO marks (student_id, subject, cca, lca, final) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE cca = VALUES(cca), lca = VALUES(lca), final = VALUES(final)";
            PreparedStatement stmt = conn.prepareStatement(query);

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int studentId = (int) tableModel.getValueAt(i, 0);
                int cca = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
                int lca = Integer.parseInt(tableModel.getValueAt(i, 3).toString());
                int finalMarks = Integer.parseInt(tableModel.getValueAt(i, 4).toString());

                stmt.setInt(1, studentId);
                stmt.setString(2, selectedSubject);
                stmt.setInt(3, cca);
                stmt.setInt(4, lca);
                stmt.setInt(5, finalMarks);
                stmt.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Marks saved successfully.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
