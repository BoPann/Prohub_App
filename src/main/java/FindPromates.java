import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.lang.Integer.parseInt;

public class FindPromates extends JPanel {
    private final ConnectionProvider connectionProvider;

    private final JTextField StudentIDField;
    private final JTable resultTable;
    private final DefaultTableModel tableModel;

    public FindPromates(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;

        SpringLayout layout = new SpringLayout();
        setLayout(layout);

        JLabel courseLabel = new JLabel("Student ID: ");
        layout.putConstraint(SpringLayout.WEST, courseLabel, 4, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, courseLabel, 4, SpringLayout.NORTH, this);
        add(courseLabel);

        StudentIDField = new JTextField(4);
        layout.putConstraint(SpringLayout.WEST, StudentIDField, 8, SpringLayout.EAST, courseLabel);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, StudentIDField, 0, SpringLayout.VERTICAL_CENTER, courseLabel);
        layout.putConstraint(SpringLayout.NORTH, StudentIDField, 0, SpringLayout.NORTH, courseLabel); // Adjusted constraint
        add(StudentIDField);

        JButton submitButton = new JButton("Get Promates!");
        layout.putConstraint(SpringLayout.WEST, submitButton, 8, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, submitButton, 8, SpringLayout.SOUTH, StudentIDField); // Adjusted constraint
        submitButton.addActionListener(this::submitQuery);
        add(submitButton);

        String[] columnNames = {"Student_ID", "Student_Name", "Student_Major", "Tutor_Name", "Tutor_Major"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        layout.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, StudentIDField);
        layout.putConstraint(SpringLayout.NORTH, scrollPane, 8, SpringLayout.SOUTH, submitButton);

        scrollPane.setPreferredSize(new Dimension(650, 100)); // Set the desired width and height

        add(scrollPane);
    }

    private void submitQuery(ActionEvent event) {
        Integer studentID;
        try {
            studentID = Integer.parseInt(StudentIDField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nothing entered", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        try (Connection connection = connectionProvider.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("CALL GetGroupMembersAndTutor(?)");
            statement.setInt(1, studentID);

            ResultSet resultSet = statement.executeQuery();

            tableModel.setRowCount(0);

            while (resultSet.next()) {
                studentID = resultSet.getInt("Student_ID");
                String studentName = resultSet.getString("Student_Name");
                String studentMajor = resultSet.getString("Student_Major");
                String tutorName = resultSet.getString("Tutor_Name");
                String tutorMajor = resultSet.getString("Tutor_Major");

                Object[] row = {studentID, studentName, studentMajor, tutorName, tutorMajor};
                tableModel.addRow(row);
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No such record exists.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
