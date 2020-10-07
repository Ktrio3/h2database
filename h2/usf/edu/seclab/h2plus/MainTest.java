package usf.edu.seclab.h2plus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MainTest {

    public static void main(String[] args) {
//        TestUtils.fillDatabase();
//        TestUtils.prepareFillTestTable();
        testDatabase();
    }

    private static void testDatabase() {
        Connection connection = null;
        try {
            // create a database connection
            connection = DriverManager.getConnection(TestUtils.DB_NAME);

//            TestUtils.testUpdate();
//            TestUtils.testSimple();
//            TestUtils.fillDatabase();
//            SQLInjectionUtils.testInjectionBatchUpdate(connection);
//            SQLInjectionUtils.selectTest(connection);

//            PerformanceTestUtils.testPreparedStatementsStandard(connection);
// //
            // PerformanceTestUtils.testStatementStatic(connection);
            // PerformanceTestUtils.testStatementStaticRand(connection);
            // PerformanceTestUtils.testStatementStaticRandBad(connection);

            // PerformanceTestUtils.testPreparedDynamic(connection);
            // PerformanceTestUtils.testPreparedDynamicRand(connection);
            // PerformanceTestUtils.testPreparedDynamicRandBad(connection);

            // PerformanceTestUtils.testPreparedStatementsStandardSetInt(connection);
            // PerformanceTestUtils.testPreparedStatementsStandardSetIntRandom(connection);
            // PerformanceTestUtils.testPreparedStatementsStandardSetIntBad(connection);

            // PerformanceTestUtils.testPreparedStatementsPlus(connection);
            // PerformanceTestUtils.testPreparedStatementsPlusRandom(connection);
            // PerformanceTestUtils.testPreparedStatementsPlusBad(connection);

            // PerformanceTestUtils.testPreparedStatementsListPlus(connection);
            // PerformanceTestUtils.testPreparedStatementsListPlusRandom(connection);
            // PerformanceTestUtils.testPreparedStatementsListPlusBad(connection);

            // PerformanceTestUtils.testPreparedStatementsListSinglePlus(connection);
            // PerformanceTestUtils.testPreparedStatementsListSinglePlusRandom(connection);
            PerformanceTestUtils.testPreparedStatementsListSinglePlusBad(connection);

            //SecurityTestUtils.testsetColName(connection);
            //System.out.println("========");
            //SecurityTestUtils.testStringConcat(connection);
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
