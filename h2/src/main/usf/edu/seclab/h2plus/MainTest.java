package usf.edu.seclab.h2plus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MainTest {

    public static void main(String[] args) {
//        testDatabase();
//        TestUtils.fillDatabase();
        TestUtils.testSimple();
    }

    private static void testDatabase() {
        Connection connection = null;
        try {
            // create a database connection
            connection = DriverManager.getConnection(TestUtils.DB_NAME);
            SQLInjectionUtils.testInjectionSelectCount(connection);
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e);
            }
        }
    }
}
