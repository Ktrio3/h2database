package usf.edu.seclab.h2plus;

import org.h2.jdbc.JdbcPreparedStatement;

import java.sql.*;

public class TestUtils {

    public static final String DB_NAME = "jdbc:h2:/Users/cagricetin/Documents/DEV/SQLIProject/h2database/h2/sample";

    public static void testSimple() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_NAME);
            String sql = "select * from injTest where id=? and name like ?";
            JdbcPreparedStatement selectStatement = (JdbcPreparedStatement) connection.prepareStatement(sql);
            selectStatement.setInt(1, 10);
            selectStatement.setString(2, "use%");
            ResultSet resultSet = selectStatement.executeQuery();
            printResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void printResultSet(ResultSet rs) throws SQLException {
        while (rs.next()) {
            System.out.println("name = " + rs.getString("name") + ", id= " +
                    rs.getInt("id"));
        }
    }

    public static void fillDatabase() {
        Connection connection = null;
        try {
            // create a database connection
            connection = DriverManager.getConnection(DB_NAME);
            Statement s = connection.createStatement();
            for (int i = 0; i < 1000; i++) {
                s.executeUpdate("insert into injTest (id, name, group_id) values (" + i + ", 'user" + i + "', '"
                        + i % 100 + "')");
            }
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
