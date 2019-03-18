package usf.edu.seclab.h2plus;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.sql.*;
import org.h2.jdbc.JdbcPreparedStatement;
import org.h2.jdbc.JdbcSQLSyntaxErrorException;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.sql.*;
import java.util.*;

public class PerformanceTestUtils {

    private static final int TOTAL_EXPR_COUNT = 102;

    private static Set<String> mColumnSet = new HashSet<>(Arrays.asList("COL0", "COL1", "COL2", "COL3", "COL4", "COL5", "COL6", "COL7", "COL8",
            "COL9", "COL10", "COL11", "COL12", "COL13", "COL14", "COL15", "COL16", "COL17", "COL18",
            "COL19", "COL20", "COL21", "COL22", "COL23", "COL24", "COL25", "COL26", "COL27", "COL28",
            "COL29", "COL30", "COL31", "COL32", "COL33", "COL34", "COL35", "COL36", "COL37", "COL38",
            "COL39", "COL40", "COL41", "COL42", "COL43", "COL44", "COL45", "COL46", "COL47", "COL48",
            "COL49", "COL50", "COL51", "COL52", "COL53", "COL54", "COL55", "COL56", "COL57", "COL58",
            "COL59", "COL60", "COL61", "COL62", "COL63", "COL64", "COL65", "COL66", "COL67", "COL68",
            "COL69", "COL70", "COL71", "COL72", "COL73", "COL74", "COL75", "COL76", "COL77", "COL78",
            "COL79", "COL80", "COL81", "COL82", "COL83", "COL84", "COL85", "COL86", "COL87", "COL88",
            "COL89", "COL90", "COL91", "COL92", "COL93", "COL94", "COL95", "COL96", "COL97", "COL98",
            "COL99", "COL100"));

    public static void testPreparedStatementsStandard(Connection connection) throws SQLException {
        List<Long> longs = new ArrayList<>();

        JdbcPreparedStatement selectStatement = (JdbcPreparedStatement)
                connection.prepareStatement("select * from injTest where group_id='20' order by id asc");

        for (int i = 0; i < TOTAL_EXPR_COUNT; i++) {
            long curr = getMicroSec();

            ResultSet rs = selectStatement.executeQuery();

            long diff = getMicroSec() - curr;
            longs.add(diff);
        }

        printTiming(longs);
    }

    public static void testStatementStatic(Connection connection) throws SQLException {
        List<Long> longs = new ArrayList<>();
        String id = "col2";

        for (int i = 0; i < TOTAL_EXPR_COUNT; i++) {
            long curr = getMicroSec();

            if (!mColumnSet.contains(id.toLowerCase()) && !mColumnSet.contains(id.toUpperCase()))
                throw new SQLException("Column not in the list");

            JdbcPreparedStatement selectStatement = (JdbcPreparedStatement)
                    connection.prepareStatement("select * from testtable where col2 < 100 order by " + id + " asc");
            ResultSet resultSet = selectStatement.executeQuery();

            long diff = getMicroSec() - curr;
            longs.add(diff);
        }

        printTiming(longs);
    }

    public static void testStatementStaticRand(Connection connection) throws SQLException {
        List<Long> longs = new ArrayList<>();

        for (int i = 0; i < TOTAL_EXPR_COUNT; i++) {
            long curr = getMicroSec();
            int colIndex = TestUtils.getRandColIndex();
            String id = "col" + colIndex;
            if (!mColumnSet.contains(id.toLowerCase()) && !mColumnSet.contains(id.toUpperCase()))
                throw new SQLException("Column not in the list");

            JdbcPreparedStatement selectStatement = (JdbcPreparedStatement)
                    connection.prepareStatement("select * from testtable where col2 < 100 order by " + id + " asc");
            ResultSet resultSet = selectStatement.executeQuery();

            long diff = getMicroSec() - curr;
            longs.add(diff);
        }

        printTiming(longs);
    }

    public static void testPreparedDynamicRand(Connection connection) throws SQLException {
        List<Long> longs = new ArrayList<>();
        PreparedStatement preparedStatement =
                connection.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE COLUMN_NAME = ?");

        for (int i = 0; i < TOTAL_EXPR_COUNT; i++) {
            long curr = getMicroSec();

            int colIndex = TestUtils.getRandColIndex();
            String id = "col" + colIndex;

            if (!checkColNames(preparedStatement, id))
                throw new SQLException("Column not in the list");

            JdbcPreparedStatement selectStatement = (JdbcPreparedStatement)
                    connection.prepareStatement("select * from testtable where col2 < 100 order by " + id + " asc");
            ResultSet resultSet = selectStatement.executeQuery();

            long diff = getMicroSec() - curr;
            longs.add(diff);
        }

        printTiming(longs);
    }

    public static void testPreparedDynamicRandBad(Connection connection) throws SQLException {
        List<Long> longs = new ArrayList<>();
        PreparedStatement preparedStatement =
                connection.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE COLUMN_NAME = ?");

        for (int i = 0; i < TOTAL_EXPR_COUNT; i++) {
            long curr = getMicroSec();

            int colIndex = TestUtils.getOutOfRangeRandColIndex();
            String id = "col" + colIndex;

            if (!checkColNames(preparedStatement, id))
                id = "col0";

            JdbcPreparedStatement selectStatement = (JdbcPreparedStatement)
                    connection.prepareStatement("select * from testtable where col2 < 100 order by " + id + " asc");
            ResultSet resultSet = selectStatement.executeQuery();

            long diff = getMicroSec() - curr;
            longs.add(diff);
        }

        printTiming(longs);
    }


    public static void testPreparedDynamic(Connection connection) throws SQLException {
        List<Long> longs = new ArrayList<>();
        String id = "col2";

        PreparedStatement preparedStatement =
                connection.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE COLUMN_NAME = ?");

        for (int i = 0; i < TOTAL_EXPR_COUNT; i++) {
            long curr = getMicroSec();

            if (!checkColNames(preparedStatement, id))
                throw new SQLException("Column not in the list");

            JdbcPreparedStatement selectStatement = (JdbcPreparedStatement)
                    connection.prepareStatement("select * from testtable where col2 < 100 order by " + id + " asc");
            ResultSet resultSet = selectStatement.executeQuery();

            long diff = getMicroSec() - curr;
            longs.add(diff);
        }

        printTiming(longs);
    }


    private static boolean checkColNames(PreparedStatement preparedStatement, String colName) throws SQLException {
        preparedStatement.setString(1, colName.toUpperCase());
        ResultSet rs = preparedStatement.executeQuery();
        return rs.next();
    }

    public static void testPreparedStatementsStandardSetInt(Connection connection) throws SQLException {
        List<Long> longs = new ArrayList<>();

        JdbcPreparedStatement selectStatement = (JdbcPreparedStatement)
                connection.prepareStatement("select * from testtable where col2 < 100 order by ? asc");

        for (int i = 0; i < TOTAL_EXPR_COUNT; i++) {
            long curr = getMicroSec();

            int colIndex = TestUtils.getRandColIndex();

            selectStatement.setInt(1, colIndex);
            ResultSet rs = selectStatement.executeQuery();

            long diff = getMicroSec() - curr;
            longs.add(diff);
        }

        printTiming(longs);
    }

    public static void testStatementStaticRandBad(Connection connection) throws SQLException {
        List<Long> longs = new ArrayList<>();

        for (int i = 0; i < TOTAL_EXPR_COUNT; i++) {
            long curr = getMicroSec();
            int colIndex = TestUtils.getOutOfRangeRandColIndex();
            String id = "col" + colIndex;
            if (!mColumnSet.contains(id.toLowerCase()) && !mColumnSet.contains(id.toUpperCase()))
                id = "col0";

            JdbcPreparedStatement selectStatement = (JdbcPreparedStatement)
                    connection.prepareStatement("select * from testtable where col2 < 100 order by " + id + " asc");
            ResultSet resultSet = selectStatement.executeQuery();

            long diff = getMicroSec() - curr;
            longs.add(diff);
        }

        printTiming(longs);
    }


    public static void testPreparedStatementsStandardSetIntBad(Connection connection) throws SQLException {
        List<Long> longs = new ArrayList<>();

        JdbcPreparedStatement selectStatement = (JdbcPreparedStatement)
                connection.prepareStatement("select * from testtable where col2 < 100 order by ? asc");

        for (int i = 0; i < TOTAL_EXPR_COUNT; i++) {
            long curr = getMicroSec();

            int colIndex = TestUtils.getOutOfRangeRandColIndex();

            selectStatement.setInt(1, colIndex);

            try {
                ResultSet rs = selectStatement.executeQuery();
            } catch (JdbcSQLSyntaxErrorException e) {
                e.printStackTrace();
            }

            long diff = getMicroSec() - curr;
            longs.add(diff);
        }

        printTiming(longs);
    }

    public static void testPreparedStatementsPlus(Connection connection) throws SQLException {
        List<Long> longs = new ArrayList<>();

        JdbcPreparedStatement selectStatement = (JdbcPreparedStatement)
                connection.prepareStatement("select * from testtable where col2 < 100 order by ? asc");

        for (int i = 0; i < TOTAL_EXPR_COUNT; i++) {
            long curr = getMicroSec();

            int colIndex = TestUtils.getRandColIndex();

            selectStatement.setColumnName(1, "col" + colIndex);
            selectStatement.executeQuery();

            long diff = getMicroSec() - curr;
            longs.add(diff);
        }

        printTiming(longs);
    }

    // Modify ad hoc metods to do the same thing.
    public static void testPreparedStatementsPlusBad(Connection connection) throws SQLException {
        List<Long> longs = new ArrayList<>();

        JdbcPreparedStatement selectStatement = (JdbcPreparedStatement)
                connection.prepareStatement("select * from testtable where col2 < 100 order by ? asc");

        for (int i = 0; i < TOTAL_EXPR_COUNT; i++) {
            long curr = getMicroSec();

            int colIndex = TestUtils.getOutOfRangeRandColIndex();

            selectStatement.setColumnName(1, "col" + colIndex);
            selectStatement.executeQuery();

            long diff = getMicroSec() - curr;
            longs.add(diff);
        }

        printTiming(longs);
    }


    public static void testColConcatWithManuelCheck(Connection connection) throws SQLException {
        List<Long> longs = new ArrayList<>();
        String columnName = "id";

        for (int i = 0; i < TOTAL_EXPR_COUNT; i++) {
            long curr = getMicroSec();
            String sql = "select * from injTest where group_id='20' order by " + columnName;

            if (!checkColNames(null, columnName)) return;
            Statement s = connection.createStatement();
            s.executeQuery(sql);

            long diff = getMicroSec() - curr;
            longs.add(diff);
        }

        printTiming(longs);
    }

    public static void testInjectionQDSL(Connection connection) throws SQLException {

        List<Long> longs = new ArrayList<>();
        SQLTemplates templates = new H2Templates();
        Configuration configuration = new Configuration(templates);
        SQLQueryFactory queryFactory = new SQLQueryFactory(configuration, TestUtils.getMysqlDataSource());

        PathBuilder<String> p = new PathBuilder<String>(String.class, "testtable");
        NumberPath<Integer> col0 = p.getNumber("col0", Integer.class);
        NumberPath<Integer> col1 = p.getNumber("col1", Integer.class);
        NumberPath<Integer> col2 = p.getNumber("col2", Integer.class);
        NumberPath<Integer> col3 = p.getNumber("col3", Integer.class);
        NumberPath<Integer> col4 = p.getNumber("col4", Integer.class);
        NumberPath<Integer> col5 = p.getNumber("col5", Integer.class);
        NumberPath<Integer> col6 = p.getNumber("col6", Integer.class);
        NumberPath<Integer> col7 = p.getNumber("col7", Integer.class);
        NumberPath<Integer> col8 = p.getNumber("col8", Integer.class);
        NumberPath<Integer> col9 = p.getNumber("col9", Integer.class);
        NumberPath<Integer> col10 = p.getNumber("col10", Integer.class);
        NumberPath<Integer> col11 = p.getNumber("col11", Integer.class);
        NumberPath<Integer> col12 = p.getNumber("col12", Integer.class);
        NumberPath<Integer> col13 = p.getNumber("col13", Integer.class);
        NumberPath<Integer> col14 = p.getNumber("col14", Integer.class);
        NumberPath<Integer> col15 = p.getNumber("col15", Integer.class);
        NumberPath<Integer> col16 = p.getNumber("col16", Integer.class);
        NumberPath<Integer> col17 = p.getNumber("col17", Integer.class);
        NumberPath<Integer> col18 = p.getNumber("col18", Integer.class);
        NumberPath<Integer> col19 = p.getNumber("col19", Integer.class);
        NumberPath<Integer> col20 = p.getNumber("col20", Integer.class);
        NumberPath<Integer> col21 = p.getNumber("col21", Integer.class);
        NumberPath<Integer> col22 = p.getNumber("col22", Integer.class);
        NumberPath<Integer> col23 = p.getNumber("col23", Integer.class);
        NumberPath<Integer> col24 = p.getNumber("col24", Integer.class);
        NumberPath<Integer> col25 = p.getNumber("col25", Integer.class);
        NumberPath<Integer> col26 = p.getNumber("col26", Integer.class);
        NumberPath<Integer> col27 = p.getNumber("col27", Integer.class);
        NumberPath<Integer> col28 = p.getNumber("col28", Integer.class);
        NumberPath<Integer> col29 = p.getNumber("col29", Integer.class);
        NumberPath<Integer> col30 = p.getNumber("col30", Integer.class);
        NumberPath<Integer> col31 = p.getNumber("col31", Integer.class);
        NumberPath<Integer> col32 = p.getNumber("col32", Integer.class);
        NumberPath<Integer> col33 = p.getNumber("col33", Integer.class);
        NumberPath<Integer> col34 = p.getNumber("col34", Integer.class);
        NumberPath<Integer> col35 = p.getNumber("col35", Integer.class);
        NumberPath<Integer> col36 = p.getNumber("col36", Integer.class);
        NumberPath<Integer> col37 = p.getNumber("col37", Integer.class);
        NumberPath<Integer> col38 = p.getNumber("col38", Integer.class);
        NumberPath<Integer> col39 = p.getNumber("col39", Integer.class);
        NumberPath<Integer> col40 = p.getNumber("col40", Integer.class);
        NumberPath<Integer> col41 = p.getNumber("col41", Integer.class);
        NumberPath<Integer> col42 = p.getNumber("col42", Integer.class);
        NumberPath<Integer> col43 = p.getNumber("col43", Integer.class);
        NumberPath<Integer> col44 = p.getNumber("col44", Integer.class);
        NumberPath<Integer> col45 = p.getNumber("col45", Integer.class);
        NumberPath<Integer> col46 = p.getNumber("col46", Integer.class);
        NumberPath<Integer> col47 = p.getNumber("col47", Integer.class);
        NumberPath<Integer> col48 = p.getNumber("col48", Integer.class);
        NumberPath<Integer> col49 = p.getNumber("col49", Integer.class);
        NumberPath<Integer> col50 = p.getNumber("col50", Integer.class);
        NumberPath<Integer> col51 = p.getNumber("col51", Integer.class);
        NumberPath<Integer> col52 = p.getNumber("col52", Integer.class);
        NumberPath<Integer> col53 = p.getNumber("col53", Integer.class);
        NumberPath<Integer> col54 = p.getNumber("col54", Integer.class);
        NumberPath<Integer> col55 = p.getNumber("col55", Integer.class);
        NumberPath<Integer> col56 = p.getNumber("col56", Integer.class);
        NumberPath<Integer> col57 = p.getNumber("col57", Integer.class);
        NumberPath<Integer> col58 = p.getNumber("col58", Integer.class);
        NumberPath<Integer> col59 = p.getNumber("col59", Integer.class);
        NumberPath<Integer> col60 = p.getNumber("col60", Integer.class);
        NumberPath<Integer> col61 = p.getNumber("col61", Integer.class);
        NumberPath<Integer> col62 = p.getNumber("col62", Integer.class);
        NumberPath<Integer> col63 = p.getNumber("col63", Integer.class);
        NumberPath<Integer> col64 = p.getNumber("col64", Integer.class);
        NumberPath<Integer> col65 = p.getNumber("col65", Integer.class);
        NumberPath<Integer> col66 = p.getNumber("col66", Integer.class);
        NumberPath<Integer> col67 = p.getNumber("col67", Integer.class);
        NumberPath<Integer> col68 = p.getNumber("col68", Integer.class);
        NumberPath<Integer> col69 = p.getNumber("col69", Integer.class);
        NumberPath<Integer> col70 = p.getNumber("col70", Integer.class);
        NumberPath<Integer> col71 = p.getNumber("col71", Integer.class);
        NumberPath<Integer> col72 = p.getNumber("col72", Integer.class);
        NumberPath<Integer> col73 = p.getNumber("col73", Integer.class);
        NumberPath<Integer> col74 = p.getNumber("col74", Integer.class);
        NumberPath<Integer> col75 = p.getNumber("col75", Integer.class);
        NumberPath<Integer> col76 = p.getNumber("col76", Integer.class);
        NumberPath<Integer> col77 = p.getNumber("col77", Integer.class);
        NumberPath<Integer> col78 = p.getNumber("col78", Integer.class);
        NumberPath<Integer> col79 = p.getNumber("col79", Integer.class);
        NumberPath<Integer> col80 = p.getNumber("col80", Integer.class);
        NumberPath<Integer> col81 = p.getNumber("col81", Integer.class);
        NumberPath<Integer> col82 = p.getNumber("col82", Integer.class);
        NumberPath<Integer> col83 = p.getNumber("col83", Integer.class);
        NumberPath<Integer> col84 = p.getNumber("col84", Integer.class);
        NumberPath<Integer> col85 = p.getNumber("col85", Integer.class);
        NumberPath<Integer> col86 = p.getNumber("col86", Integer.class);
        NumberPath<Integer> col87 = p.getNumber("col87", Integer.class);
        NumberPath<Integer> col88 = p.getNumber("col88", Integer.class);
        NumberPath<Integer> col89 = p.getNumber("col89", Integer.class);
        NumberPath<Integer> col90 = p.getNumber("col90", Integer.class);
        NumberPath<Integer> col91 = p.getNumber("col91", Integer.class);
        NumberPath<Integer> col92 = p.getNumber("col92", Integer.class);
        NumberPath<Integer> col93 = p.getNumber("col93", Integer.class);
        NumberPath<Integer> col94 = p.getNumber("col94", Integer.class);
        NumberPath<Integer> col95 = p.getNumber("col95", Integer.class);
        NumberPath<Integer> col96 = p.getNumber("col96", Integer.class);
        NumberPath<Integer> col97 = p.getNumber("col97", Integer.class);
        NumberPath<Integer> col98 = p.getNumber("col98", Integer.class);
        NumberPath<Integer> col99 = p.getNumber("col99", Integer.class);


        for (int i = 0; i < TOTAL_EXPR_COUNT; i++) {
            long curr = getMicroSec();

            NumberPath<Integer> colX = p.getNumber("col" + TestUtils.getRandColIndex(), Integer.class);

            SQLQuery<Tuple> where = queryFactory.select(col0, col1, col2, col3, col4, col5, col6, col7, col8, col9,
                    col10, col11, col12, col13, col14, col15, col16, col17, col18, col19,
                    col20, col21, col22, col23, col24, col25, col26, col27, col28, col29,
                    col30, col31, col32, col33, col34, col35, col36, col37, col38, col39,
                    col40, col41, col42, col43, col44, col45, col46, col47, col48, col49,
                    col50, col51, col52, col53, col54, col55, col56, col57, col58, col59,
                    col60, col61, col62, col63, col64, col65, col66, col67, col68, col69,
                    col70, col71, col72, col73, col74, col75, col76, col77, col78, col79,
                    col80, col81, col82, col83, col84, col85, col86, col87, col88, col89,
                    col90, col91, col92, col93, col94, col95, col96, col97, col98, col99
            ).from(p).where(col2.loe(100));
            where.orderBy(new OrderSpecifier(com.querydsl.core.types.Order.ASC, colX)).fetch();

            long diff = getMicroSec() - curr;
            longs.add(diff);
        }

        printTiming(longs);
    }


    private static void printTiming(List<Long> longs) {
        for (Long l : longs) {
            System.out.println(l);
        }
    }

    private static long getMicroSec() {
        return System.nanoTime() / 1000;
    }
}
