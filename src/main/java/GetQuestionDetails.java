import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetQuestionDetails extends JPanel {
    private final ConnectionProvider connectionProvider;

    private final JTextField courseField;
    private final JTextField difficultyField;
    private final JTable resultTable;
    private final DefaultTableModel tableModel;

    public GetQuestionDetails(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;

        SpringLayout layout = new SpringLayout();
        setLayout(layout);

        JLabel courseLabel = new JLabel("Course:");
        layout.putConstraint(SpringLayout.WEST, courseLabel, 4, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, courseLabel, 4, SpringLayout.NORTH, this);
        add(courseLabel);
        courseField = new JTextField(4);
        layout.putConstraint(SpringLayout.WEST, courseField, 8, SpringLayout.EAST, courseLabel);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, courseField, 0, SpringLayout.VERTICAL_CENTER, courseLabel);
        add(courseField);

        JLabel difficultyLabel = new JLabel("Difficulty:");
        layout.putConstraint(SpringLayout.WEST, courseField, 8, SpringLayout.EAST, courseLabel);
        layout.putConstraint(SpringLayout.NORTH, difficultyLabel, 12, SpringLayout.SOUTH, courseLabel);
        add(difficultyLabel);
        difficultyField = new JTextField(4);
        layout.putConstraint(SpringLayout.WEST, difficultyField, 8, SpringLayout.EAST, difficultyLabel);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, difficultyField, 0, SpringLayout.VERTICAL_CENTER, difficultyLabel);
        add(difficultyField);

        JButton submitButton = new JButton("Get Question Details");
        layout.putConstraint(SpringLayout.WEST, submitButton, 4, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, submitButton, 16, SpringLayout.SOUTH, difficultyLabel);
        submitButton.addActionListener(this::submitQuery);
        add(submitButton);

        String[] columnNames = {"Description", "Solution", "Difficulty"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);


        resultTable.getColumnModel().getColumn(0).setPreferredWidth(540);
        resultTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        resultTable.getColumnModel().getColumn(2).setPreferredWidth(60);

        JScrollPane scrollPane = new JScrollPane(resultTable);
        layout.putConstraint(SpringLayout.WEST, scrollPane, 0, SpringLayout.WEST, courseField);
        layout.putConstraint(SpringLayout.NORTH, scrollPane, 8, SpringLayout.SOUTH, submitButton);


        scrollPane.setPreferredSize(new Dimension(650, 400));

        add(scrollPane);

    }

    private void submitQuery(ActionEvent event) {
        String course = courseField.getText();
        int difficulty;
        try {
            difficulty = Integer.parseInt(difficultyField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error! Invalid input. Difficulty [1-3]", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT q.Description, q.Solution, quiz.Difficulty " +
                     "FROM Question q " +
                     "JOIN Quiz quiz ON quiz.ID = q.Quiz_ID " +
                     "JOIN Course c ON c.Id = quiz.Course_id " +
                     "WHERE c.Name = ? AND quiz.Difficulty = ? " +
                     "ORDER BY quiz.Difficulty")) {
            statement.setString(1, course);
            statement.setInt(2, difficulty);
            ResultSet resultSet = statement.executeQuery();

            tableModel.setRowCount(0);

            while (resultSet.next()) {
                String description = resultSet.getString("Description");
                String solution = resultSet.getString("Solution");
                int questionDifficulty = resultSet.getInt("Difficulty");

                Object[] row = {description, solution, questionDifficulty};
                tableModel.addRow(row);
            }

            if(tableModel.getRowCount() == 0)
                JOptionPane.showMessageDialog(this, "No such record exists.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "An error occurred while retrieving data.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
