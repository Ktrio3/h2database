package usf.edu.seclab.h2plus;

import org.h2.jdbc.JdbcPreparedStatement;

import java.sql.*;
import java.util.Random;

public class PerformanceTestUtils {
    public static void testPreparedStatementCol(Connection connection) throws SQLException {
        JdbcPreparedStatement selectStatement = (JdbcPreparedStatement)
                connection.prepareStatement("select * from person where group_id='50' order by ?");
//        selectStatement.setColumnName(1, "name");
        selectStatement.executeQuery();
    }

    public static void testPreparedStatementString(Connection connection) throws SQLException {
        JdbcPreparedStatement selectStatement = (JdbcPreparedStatement)
                connection.prepareStatement("select * from person where group_id= ? order by name");
        selectStatement.setString(1, "50");
//        selectStatement.setNull();
        ResultSet resultSet = selectStatement.executeQuery();
    }

    public static void testHardcodedCol(Connection connection) throws SQLException {
        Statement s = connection.createStatement();
        ResultSet resultSet = s.executeQuery("select * from person where group_id='50' order by name");
    }

    public static void testInputSanitationCol(Connection connection) throws SQLException {
        String input = "name";
//        String testmod = ESAPI.encoder().encodeForSQL(new OracleCodec(), input);
        Statement s = connection.createStatement();
//        ResultSet resultSet = s.executeQuery("select * from person where group_id='50' order by " + testmod);
    }

    public static void testHardcodedPrepStatementCol(Connection connection) throws SQLException {
        JdbcPreparedStatement selectStatement = (JdbcPreparedStatement)
                connection.prepareStatement("select * from person where group_id='50' order by name");
        selectStatement.executeQuery();
    }

    public static void testColConcatWithManCheck(Connection connection) throws SQLException {
        String columnName = "id";
        if (!checkColNames(connection, columnName)) return;

        Statement s = connection.createStatement();
        ResultSet resultSet = s.executeQuery("select * from person where group_id='50' order by " + columnName);
    }

    private static boolean checkColNames(Connection connection, String colName) throws SQLException {
        String sql = "select * from person limit 1";
        Statement s = connection.createStatement();
        ResultSet resultSet = s.executeQuery(sql);
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            if (metaData.getColumnName(i).equals(colName)) {
                return true;
            }
        }

        return false;
    }

    public static void preparedStatementTest(Connection connection) throws SQLException {
        int randomNumber = new Random().nextInt(9999);
        PreparedStatement insertStatement = connection.prepareStatement("insert into person values(?, ?)");
        insertStatement.setInt(1, randomNumber);
        insertStatement.setString(2, "sim" + randomNumber);
        insertStatement.executeUpdate();

        PreparedStatement selectStatements = connection.prepareStatement("select * from person where id = ?");
        selectStatements.setInt(1, randomNumber);
    }

    /**
     * //            long curr = 0;
     * //            for (int i = 0; i < runLong; i++) {
     * //                curr = System.nanoTime();
     * //                testHardcodedPrepStatementCol(connection);
     * //            }
     * //            long diff = System.nanoTime() - curr;
     * //
     * //            totalTime += diff;
     * //
     * //            System.out.println("diff1: " + totalTime / runLong);
     *         long totalTime = 0;
     *         int runLong = 20;
     */
}
