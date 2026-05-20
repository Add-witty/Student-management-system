import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ManageStudents extends JFrame {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, emailField;
    private JButton addButton, deleteButton;

    public ManageStudents() {
        setTitle("Manage Students");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Table setup
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Email"}, 0);
        studentTable = new JTable(tableModel);
        loadStudents();
        add(new JScrollPane(studentTable), BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        nameField = new JTextField();
        emailField = new JTextField();
        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        add(formPanel, BorderLayout.NORTH);

        // Buttons
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add Student");
        deleteButton = new JButton("Delete Student");
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add button action
        addButton.addActionListener(e -> addStudent());

        // Delete button action
        deleteButton.addActionListener(e -> deleteStudent());

        setVisible(true);
    }

    private void loadStudents() {
        tableModel.setRowCount(0);
        try (Connection conn = Database.connect()) {
            String query = "SELECT id, name, email FROM student";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getString("email")});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addStudent() {
    String name = nameField.getText();
    String email = emailField.getText();

    if (name.isEmpty() || email.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill all fields.");
        return;
    }

    try (Connection conn = Database.connect()) {
        // Step 1: Insert into student table
        String insertStudentQuery = "INSERT INTO student (name, email, password) VALUES (?, ?, ?)";
        PreparedStatement studentStmt = conn.prepareStatement(insertStudentQuery, PreparedStatement.RETURN_GENERATED_KEYS);
        studentStmt.setString(1, name);
        studentStmt.setString(2, email);
        studentStmt.setString(3, "pass123");
        studentStmt.executeUpdate();

        ResultSet generatedKeys = studentStmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            int studentId = generatedKeys.getInt(1);

            // Step 2: Insert into users table for login
            String insertUserQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement userStmt = conn.prepareStatement(insertUserQuery);
            userStmt.setString(1, String.valueOf(studentId)); // or use email if that's the login
            userStmt.setString(2, "pass123");
            userStmt.setString(3, "student");
            userStmt.executeUpdate();
        }

        JOptionPane.showMessageDialog(this, "Student added successfully with login password 'pass123'.");
        nameField.setText("");
        emailField.setText("");
        loadStudents();
    } catch (Exception e) {
        e.printStackTrace();
    }
}


    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a student to delete.");
            return;
        }
        int studentId = (int) tableModel.getValueAt(selectedRow, 0);
        try (Connection conn = Database.connect()) {
            String query = "DELETE FROM student WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, studentId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student deleted successfully.");
            loadStudents();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
