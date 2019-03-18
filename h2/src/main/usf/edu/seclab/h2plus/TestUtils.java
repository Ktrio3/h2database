package usf.edu.seclab.h2plus;

import org.h2.jdbc.JdbcPreparedStatement;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Random;

public class TestUtils {

    public static final String DB_NAME = "jdbc:h2:/Users/cagricetin/Documents/DEV/SQLIProject/h2database/h2/sample";


    public static void prepareTestTable() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_NAME);
            StringBuilder sb = new StringBuilder();
            sb.append("create table testTable (");
            for (int i = 0; i < 100; i++) {
                sb.append("col").append(i).append(" int");
                if (i != 99) {
                    sb.append(",");
                }
            }

            sb.append(")");

            Statement statement = connection.createStatement();
            statement.executeUpdate(sb.toString());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * INSERT INTO TESTTABLE (COL0, COL1, COL2, COL3, COL4, COL5, COL6, COL7, COL8, COL9, COL10, COL11, COL12,
     * COL13, COL14, COL15, COL16, COL17, COL18, COL19, COL20, COL21, COL22, COL23, COL24, COL25, COL26, COL27,
     * COL28, COL29, COL30, COL31, COL32, COL33, COL34, COL35, COL36, COL37, COL38, COL39, COL40, COL41, COL42,
     * COL43, COL44, COL45, COL46, COL47, COL48, COL49, COL50, COL51, COL52, COL53, COL54, COL55, COL56, COL57, COL58,
     * COL59, COL60, COL61, COL62, COL63, COL64, COL65, COL66, COL67, COL68, COL69, COL70, COL71, COL72, COL73, COL74,
     * COL75, COL76, COL77, COL78, COL79, COL80, COL81, COL82, COL83, COL84, COL85, COL86, COL87, COL88, COL89, COL90,
     * COL91, COL92, COL93, COL94, COL95, COL96, COL97, COL98, COL99) VALUES (0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
     * 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
     * 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
     * 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
     */
    public static void prepareFillTestTable() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_NAME);
            for (int j = 0; j < 999; j++) {
                StringBuilder sb = new StringBuilder();
                sb.append("INSERT INTO TESTTABLE (COL0, COL1, COL2, COL3, COL4, COL5, COL6, COL7, COL8, COL9, COL10, COL11, COL12,");
                sb.append("COL13, COL14, COL15, COL16, COL17, COL18, COL19, COL20, COL21, COL22, COL23, COL24, COL25, COL26, COL27,");
                sb.append("COL28, COL29, COL30, COL31, COL32, COL33, COL34, COL35, COL36, COL37, COL38, COL39, COL40, COL41, COL42,");
                sb.append("COL43, COL44, COL45, COL46, COL47, COL48, COL49, COL50, COL51, COL52, COL53, COL54, COL55, COL56, COL57, COL58,");
                sb.append("COL59, COL60, COL61, COL62, COL63, COL64, COL65, COL66, COL67, COL68, COL69, COL70, COL71, COL72, COL73, COL74,");
                sb.append("COL75, COL76, COL77, COL78, COL79, COL80, COL81, COL82, COL83, COL84, COL85, COL86, COL87, COL88, COL89, COL90,");
                sb.append("COL91, COL92, COL93, COL94, COL95, COL96, COL97, COL98, COL99) VALUES (");

                for (int i = 0; i < 100; i++) {
                    Random rand = new Random();
                    int value = rand.nextInt(1000);
                    sb.append(value);
                    if (i != 99) {
                        sb.append(",");
                    }
                }

                sb.append(")");
                Statement statement = connection.createStatement();
                statement.executeUpdate(sb.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void testSimple() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_NAME);
            String sql = "select * from injTest where id= ?";
            JdbcPreparedStatement selectStatement = (JdbcPreparedStatement) connection.prepareStatement(sql);
            selectStatement.setInt(1, 1);
            ResultSet resultSet = selectStatement.executeQuery();
            printResultSet2(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void testUpdate() {
        try {
            Connection connection = DriverManager.getConnection(DB_NAME);
            int i = 6;
            Statement s = connection.createStatement();
            s.executeUpdate("insert into injTest (id, name, group_id) values (-" + i + " -4, 'userX', 'yyy'); " +
                    "delete from injTest");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static DataSource getMysqlDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(DB_NAME);
        dataSource.setUser("");
        dataSource.setPassword("");
        return dataSource;
    }

    public static void printResultSet(ResultSet rs) throws SQLException {
        while (rs.next()) {
            System.out.println("name = " + rs.getString("col1") + ", id= " +
                    rs.getInt("col2"));
        }
    }
    public static void printResultSet2(ResultSet rs) throws SQLException {
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

    public static int getRandColIndex() {
        Random rand = new Random();
        return rand.nextInt(100);
    }

    public static int getOutOfRangeRandColIndex() {
        Random rand = new Random();
        return rand.nextInt(10000) + 101;
    }
}
