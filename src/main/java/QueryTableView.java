import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.*;
import java.sql.Connection;
import java.util.Vector;

public class QueryTableView extends JPanel {
    private final ConnectionProvider connectionProvider;
    private final JTextArea queryText = new JTextArea();
    private final JLabel errorText = new JLabel();
    private final JTable resultsTable = new JTable();
    private ReadOnlyQuery currentQuery;

    public QueryTableView(ReadOnlyQuery[] queries, ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        currentQuery = queries[0];

        queryText.setText(currentQuery.query);
        queryText.setEditable(false);

        this.setLayout(new BorderLayout());
        this.add(BorderLayout.NORTH, buildQuerySelectionArea(queries));
        this.add(BorderLayout.CENTER, buildResultsArea());
    }

    private JPanel buildQuerySelectionArea(ReadOnlyQuery[] queries) {
        JPanel root = new JPanel();
        root.setLayout(new BorderLayout());

        JPanel topPane = new JPanel();
        JComboBox<ReadOnlyQuery> querySelector = new JComboBox<>(queries);
        querySelector.setRenderer(new QueryListCellRenderer());
        querySelector.addItemListener(this::onQuerySelected);
        topPane.add(querySelector);

        JScrollPane scrollPane = new JScrollPane(queryText); // Wrap the queryText field in a JScrollPane
        scrollPane.setPreferredSize(new Dimension(600, 200)); // Set the desired size of the scrollable area
        topPane.add(scrollPane);

        root.add(BorderLayout.CENTER, topPane);

        JButton runButton = new JButton("Run");
        runButton.addActionListener(this::runQuery);
        root.add(BorderLayout.SOUTH, runButton);

        return root;
    }


    private JPanel buildResultsArea() {
        JPanel resultsArea = new JPanel();
        resultsArea.setLayout(new BorderLayout());
        resultsArea.add(BorderLayout.NORTH, errorText);
        resultsArea.add(BorderLayout.CENTER, new JScrollPane(resultsTable));
        resultsTable.setFillsViewportHeight(true);
        return resultsArea;
    }

    private void onQuerySelected(ItemEvent event) {
        ReadOnlyQuery query = (ReadOnlyQuery)event.getItem();
        currentQuery = query;
        queryText.setText(query.query);
        clearTable();
        clearError();
    }

    private void runQuery(ActionEvent event) {
        try {
            Connection connection = connectionProvider.getConnection();
            ReadOnlyQueryResult result = currentQuery.run(connection);
            clearError();
            DefaultTableModel tableModel = (DefaultTableModel)resultsTable.getModel();
            tableModel.setDataVector(result.data, result.columnNames);
            resultsTable.setVisible(true);
        } catch(Exception ex) {
            ex.printStackTrace();
            clearTable();
            errorText.setText(ex.getMessage());
            errorText.setVisible(true);
        }
    }

    private void clearTable() {
        ((DefaultTableModel)resultsTable.getModel()).setDataVector(new Vector<>(), new Vector<>());
        resultsTable.setVisible(false);
    }

    private void clearError() {
        errorText.setText("");
        errorText.setVisible(false);
    }

    private static class QueryListCellRenderer implements ListCellRenderer<ReadOnlyQuery> {
        @Override
        public Component getListCellRendererComponent(JList<? extends ReadOnlyQuery> list, ReadOnlyQuery value, int index, boolean isSelected, boolean cellHasFocus) {
            return new JLabel(value.label);
        }
    }
}
