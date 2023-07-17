import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertUpdateDeleteView extends JPanel {

    private final ConnectionProvider connectionProvider;
    private JTextField tableNameField;
    private JTextField columnField;
    private JTextField valueField;
    private JTextField idField;

    public InsertUpdateDeleteView(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        SpringLayout layout = new SpringLayout();
        setLayout(layout);

        JLabel tableNameLabel = new JLabel("Table Name:");
        tableNameField = new JTextField(20);
        JLabel columnLabel = new JLabel("Column:");
        columnField = new JTextField(20);
        JLabel valueLabel = new JLabel("Value:");
        valueField = new JTextField(20);
        JLabel idLabel = new JLabel("ID: ");
        idField = new JTextField(20);
        JButton insertButton = new JButton("Insert");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        add(tableNameLabel);
        add(tableNameField);
        add(columnLabel);
        add(columnField);
        add(valueLabel);
        add(valueField);
        add(idLabel);
        add(idField);
        add(insertButton);
        add(updateButton);
        add(deleteButton);

        layout.putConstraint(SpringLayout.WEST, tableNameLabel, 10, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, tableNameLabel, 10, SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.WEST, tableNameField, 10, SpringLayout.EAST, tableNameLabel);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, tableNameField, 0, SpringLayout.VERTICAL_CENTER, tableNameLabel);
        layout.putConstraint(SpringLayout.WEST, columnLabel, 10, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, columnLabel, 10, SpringLayout.SOUTH, tableNameLabel);
        layout.putConstraint(SpringLayout.WEST, columnField, 10, SpringLayout.EAST, columnLabel);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, columnField, 0, SpringLayout.VERTICAL_CENTER, columnLabel);
        layout.putConstraint(SpringLayout.WEST, valueLabel, 10, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, valueLabel, 10, SpringLayout.SOUTH, columnLabel);
        layout.putConstraint(SpringLayout.WEST, valueField, 10, SpringLayout.EAST, valueLabel);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, valueField, 0, SpringLayout.VERTICAL_CENTER, valueLabel);
        layout.putConstraint(SpringLayout.WEST, idLabel, 10, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, idLabel, 10, SpringLayout.SOUTH, valueLabel);
        layout.putConstraint(SpringLayout.WEST, idField, 10, SpringLayout.EAST, idLabel);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, idField, 0, SpringLayout.VERTICAL_CENTER, idLabel);
        layout.putConstraint(SpringLayout.WEST, insertButton, 10, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, insertButton, 20, SpringLayout.SOUTH, idLabel);
        layout.putConstraint(SpringLayout.WEST, updateButton, 10, SpringLayout.EAST, insertButton);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, updateButton, 0, SpringLayout.VERTICAL_CENTER, insertButton);
        layout.putConstraint(SpringLayout.WEST, deleteButton, 10, SpringLayout.EAST, updateButton);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, deleteButton, 0, SpringLayout.VERTICAL_CENTER, updateButton);



        setPreferredSize(new Dimension(400, 200));

        insertButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tableName = tableNameField.getText();
                String column = columnField.getText();
                String value = valueField.getText();

                try (Connection connection = connectionProvider.getConnection()) {
                    String insertStatement = "INSERT INTO " + tableName + " (" + column + ") VALUES (?)";
                    PreparedStatement statement = connection.prepareStatement(insertStatement);
                    statement.setString(1, value);
                    int rowsInserted = statement.executeUpdate();

                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "Record inserted successfully. ", "Success", JOptionPane.INFORMATION_MESSAGE);

                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error! Insert operation failed.\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tableName = tableNameField.getText();
                String column = columnField.getText();
                String value = valueField.getText();
                int selectedID = Integer.parseInt(idField.getText());

                try (Connection connection = connectionProvider.getConnection()) {
                    System.out.println("Connected to the database.");

                    PreparedStatement statement = connection.prepareStatement("UPDATE " + tableName + " SET " + column + " = ?" + " WHERE " +  "Id = ?");
                    statement.setString(1, value);
                    statement.setInt(2, selectedID);
                    System.out.println(statement);

                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected == 0)
                        JOptionPane.showMessageDialog(null, "No records found.\n" , "Error", JOptionPane.ERROR_MESSAGE);

                    JOptionPane.showMessageDialog(null, "Updated " + rowsAffected + " row(s).");

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error! update operation failed.\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String tableName = tableNameField.getText();
                String column = columnField.getText();
                String value = valueField.getText();

                try (Connection connection = connectionProvider.getConnection()) {
                    PreparedStatement statement = connection.prepareStatement("DELETE FROM " + tableName + " WHERE " + column + " = ?");
                    statement.setString(1, value);

                    int rowsAffected = statement.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Deleted " + rowsAffected + " row(s).");

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error! delete operation failed.\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
