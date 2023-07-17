import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReadOnlyQueryResult {
    final String[] columnNames;
    final Object[][] data;

    private ReadOnlyQueryResult(String[] columnNames, Object[][] data) {
        this.columnNames = columnNames;
        this.data = data;
    }

    public static ReadOnlyQueryResult fromResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metadata = resultSet.getMetaData();
        int columnCount = metadata.getColumnCount();
        String[] columnNames = new String[columnCount];
        for(int i = 0; i < columnCount; i++) {
            // ResultSet is 1-indexed, arrays are 0-indexed
            columnNames[i] = metadata.getColumnName(i+1);
        }
        List<Object[]> results = new ArrayList<>();
        while(resultSet.next()) {
            Object[] row = new Object[columnCount];
            for(int i = 0 ; i < columnCount; i++) {
                // ResultSet is 1-indexed, arrays are 0-indexed
                row[i] = resultSet.getObject(i+1);
            }
            results.add(row);
        }
        return new ReadOnlyQueryResult(columnNames, results.toArray(new Object[0][]));
    }
}
