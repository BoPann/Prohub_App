import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReadOnlyQuery {
    public final String label;
    public final String query;

    public ReadOnlyQuery(String label, String query) {
        this.label = label;
        this.query = query;
    }

    public ReadOnlyQueryResult run(Connection connection) throws SQLException {
        try(Statement statement = connection.createStatement()) {
            try(ResultSet resultSet = statement.executeQuery(query)) {
               return ReadOnlyQueryResult.fromResultSet(resultSet);
            }
        }
    }
}
