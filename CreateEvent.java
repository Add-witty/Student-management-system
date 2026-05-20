import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class CreateEvent extends JFrame {
    private JComboBox<String> eventTypeBox;
    private JTextField eventDateField;
    private JTextArea descriptionArea;
    private JButton createButton;

    public CreateEvent() {
        setTitle("Create Event");
        setSize(400, 300);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel for inputs
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        eventTypeBox = new JComboBox<>(new String[]{
            "Assignment", "Test", "Extra Lecture", "Group Discussion", "Meeting"
        });

        eventDateField = new JTextField("YYYY-MM-DD");
        descriptionArea = new JTextArea(3, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);

        inputPanel.add(new JLabel("Event Type:"));
        inputPanel.add(eventTypeBox);
        inputPanel.add(new JLabel("Event Date:"));
        inputPanel.add(eventDateField);
        inputPanel.add(new JLabel("Description:"));
        inputPanel.add(descriptionScroll);

        add(inputPanel, BorderLayout.CENTER);

        createButton = new JButton("Create Event");
        add(createButton, BorderLayout.SOUTH);

        createButton.addActionListener(e -> createEvent());

        setVisible(true);
    }

    private void createEvent() {
        String eventType = (String) eventTypeBox.getSelectedItem();
        String eventDate = eventDateField.getText();
        String description = descriptionArea.getText();

        // Basic validation
        if (eventDate.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try (Connection conn = Database.connect()) {
            String query = "INSERT INTO events (event_type, event_date, description) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, eventType);
            stmt.setDate(2, Date.valueOf(LocalDate.parse(eventDate)));
            stmt.setString(3, description);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Event created successfully.");
            eventDateField.setText("");
            descriptionArea.setText("");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating event.");
        }
    }
}
