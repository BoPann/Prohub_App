import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddOrderView extends JPanel {
    private final ConnectionProvider connectionProvider;

    private final JTextField nameField;
    private String name;

    public AddOrderView(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;

        SpringLayout layout = new SpringLayout();
        setLayout(layout);

        JLabel nameLabel = new JLabel("Enter your name:");
        layout.putConstraint(SpringLayout.WEST, nameLabel, 4, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, nameLabel, 4, SpringLayout.NORTH, this);
        add(nameLabel);
        nameField = new JTextField(32);
        layout.putConstraint(SpringLayout.WEST, nameField, 8, SpringLayout.EAST, nameLabel);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, nameField, 0, SpringLayout.VERTICAL_CENTER, nameLabel);
        add(nameField);

        JLabel sizeLabel = new JLabel("Coffee size:");
        layout.putConstraint(SpringLayout.EAST, sizeLabel, 0, SpringLayout.EAST, nameLabel);
        layout.putConstraint(SpringLayout.NORTH, sizeLabel, 12, SpringLayout.SOUTH, nameLabel);
        add(sizeLabel);
        JTextField sizeField = new JTextField(4);
        layout.putConstraint(SpringLayout.WEST, sizeField, 8, SpringLayout.EAST, sizeLabel);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, sizeField, 0, SpringLayout.VERTICAL_CENTER, sizeLabel);
        add(sizeField);

        JLabel milkTypeLabel = new JLabel("Type of milk:");
        layout.putConstraint(SpringLayout.EAST, milkTypeLabel, 0, SpringLayout.EAST, sizeLabel);
        layout.putConstraint(SpringLayout.NORTH, milkTypeLabel, 12, SpringLayout.SOUTH, sizeLabel);
        add(milkTypeLabel);
        JTextField milkTypeField = new JTextField(16);
        layout.putConstraint(SpringLayout.WEST, milkTypeField, 8, SpringLayout.EAST, milkTypeLabel);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, milkTypeField, 0, SpringLayout.VERTICAL_CENTER, milkTypeLabel);
        add(milkTypeField);

        JLabel decafLabel = new JLabel("Decaf: ");
        layout.putConstraint(SpringLayout.EAST, decafLabel, 0, SpringLayout.EAST, milkTypeLabel);
        layout.putConstraint(SpringLayout.NORTH, decafLabel, 12, SpringLayout.SOUTH, milkTypeLabel);
        add(decafLabel);
        JCheckBox isDecaf = new JCheckBox();
        layout.putConstraint(SpringLayout.WEST, isDecaf, 4, SpringLayout.EAST, decafLabel);
        layout.putConstraint(SpringLayout.VERTICAL_CENTER, isDecaf, 1, SpringLayout.VERTICAL_CENTER, decafLabel);
        add(isDecaf);

        JButton submitButton = new JButton("Place order");
        layout.putConstraint(SpringLayout.WEST, submitButton, 4, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, submitButton, 16, SpringLayout.SOUTH, decafLabel);
        submitButton.addActionListener(this::submitOrder);
        add(submitButton);
    }


    private void submitOrder(ActionEvent event) {
        try {
            Connection connection = connectionProvider.getConnection();
            try(PreparedStatement statement = connection.prepareStatement("INSERT INTO orders (name) VALUES(?)")) {
                statement.setString(1, name);
                int numRowsUpdated = statement.executeUpdate();
                if (numRowsUpdated == 1) {
                    // TODO: indicate success
                } else {
                    // TODO: show an error message
                }
            }
        } catch (SQLException e) {
            // TODO: show an error message
        }
        // run our query
        // INSERT INTO ORDERS (name, ...) VALUES (?
    }
}
