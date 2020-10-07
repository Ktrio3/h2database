package usf.edu.seclab.h2plus;

import org.h2.jdbc.JdbcPreparedStatement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SecurityTestUtils {

//    (case when (select col1 from testtable where col2=65) = 30 then col1 else col2 end)

    private final static String INJ1 = "(case when (select col1 from testtable where col2=65) = 30 then col1 else col2 end)";

    private final static String INJ2 = "col1111";

    private final static String INJ3 = "col2 desc --";

    public static void testsetColName(Connection connection) throws SQLException {
        List<Long> longs = new ArrayList<>();

        JdbcPreparedStatement selectStatement = (JdbcPreparedStatement)
                connection.prepareStatement("select * from testtable where col2 < 100 order by ? asc");
        selectStatement.setColumnName(1, INJ3);
        ResultSet resultSet = selectStatement.executeQuery();
        TestUtils.printResultSet(resultSet);
    }

    public static void testStringConcat(Connection connection) throws SQLException {
        List<Long> longs = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from testtable where col2 < 100 order by " + INJ3 + " asc");
        TestUtils.printResultSet(resultSet);
    }
}
