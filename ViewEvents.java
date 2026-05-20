import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ViewEvents extends JFrame {
    private JTable eventsTable;
    private DefaultTableModel tableModel;

    public ViewEvents() {
        setTitle("Upcoming Events");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"Type", "Date", "Description"}, 0);
        eventsTable = new JTable(tableModel);
        add(new JScrollPane(eventsTable), BorderLayout.CENTER);

        loadEvents();

        setVisible(true);
    }

    private void loadEvents() {
        tableModel.setRowCount(0);
        try (Connection conn = Database.connect()) {
            String query = "SELECT event_type, event_date, description FROM events ORDER BY event_date ASC";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String type = rs.getString("event_type");
                String date = rs.getDate("event_date").toString();
                String desc = rs.getString("description");
                tableModel.addRow(new Object[]{type, date, desc});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
