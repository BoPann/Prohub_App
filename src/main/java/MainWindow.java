import javax.swing.*;

public class MainWindow {
    private final JFrame window;

    public MainWindow(ConnectionProvider connectionProvider) {
        // TODO: customize window with your project or team's name, add styling as you like
        window = new JFrame("ProHub\uD83D\uDE0E");
        window.setSize(800, 600);

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        // TODO: replace with your own queries
        ReadOnlyQuery[] queries = new ReadOnlyQuery[] {

            new ReadOnlyQuery("List the average score of students in different majors in descending order",
                    "SELECT s.Major, AVG(r.Score) AS Average_Score\n" +
                            "FROM Student s\n" +
                            "JOIN Enrollment e ON s.ID = e.Student_ID\n" +
                            "JOIN Result r ON e.ID = r.Student_id\n" +
                            "GROUP BY s.Major\n" +
                            "ORDER BY Average_Score DESC;\n"),
            new ReadOnlyQuery("List the most experienced tutors ",
                    "SELECT *\n" +
                    "FROM Tutor\n" +
                    "WHERE Experience = (\n" +
                    "    SELECT MAX(Experience)\n" +
                    "    FROM Tutor\n" +
                    ");\n"),
            new ReadOnlyQuery("List the course names along with the count of enrollments for each course ",
                    "SELECT c.Name AS Class_Name, \n" +
                    "       (SELECT COUNT(*) FROM Enrollment e WHERE e.Class_ID = c.ID) AS Enrollment_Count\n" +
                    "FROM Course c;\n"),

            new ReadOnlyQuery("List students across majors who scored less than 60 in their quizzes ",
                    "SELECT Student.Name as Student_Name, Student.Major as Student_Major, Score, Difficulty\n" +
                    "FROM Student\n" +
                    "JOIN Result ON Student.ID = Result.Student_id\n" +
                    "JOIN Quiz ON Quiz.ID = Result.Quiz_id\n" +
                    "JOIN Course ON Course.Id = Quiz.Course_id\n" +
                    "WHERE Score < 60\n" +
                    "ORDER BY Student.Major, Score ASC;\n"),

            new ReadOnlyQuery("List the average scores of quizzes grouped by difficulty level in descending order\n",
                    "SELECT q.Difficulty, AVG(r.Score) AS Average_Score\n" +
                            "FROM Quiz q\n" +
                            "JOIN Result r ON q.Id = r.Quiz_id\n" +
                            "GROUP BY q.Difficulty\n" +
                            "ORDER BY Average_Score DESC;\n"),

            new ReadOnlyQuery("List the average scores of students that attend morning classes to evening classes.",
                    "SELECT CASE WHEN TIME_FORMAT(Class_time, '%h:%i %p') < '12:00 PM' \n" +
                            "THEN 'Morning' ELSE 'Afternoon/Evening' END AS Class_Timing, AVG(r.Score) AS Average_Result\n" +
                            "FROM Class c\n" +
                            "JOIN Enrollment e ON c.ID = e.Class_ID\n" +
                            "JOIN Result r ON e.Student_ID = r.Student_id\n" +
                            "GROUP BY Class_Timing;\n")
        };
        QueryTableView queryTableView = new QueryTableView(queries, connectionProvider);
        tabs.add("All Queries", queryTableView);

        // TODO: add your own components for showing parameterized queries
        tabs.add("Insert, Update, or Delete", new InsertUpdateDeleteView(connectionProvider));
        tabs.add("Find your Promates! ", new FindPromates(connectionProvider));
        tabs.add("Get question details", new GetQuestionDetails(connectionProvider));
        window.setContentPane(tabs);
    }

    public void show() {
        window.setVisible(true);
    }
}
