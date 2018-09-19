package usf.edu.seclab.h2plus;

import org.h2.jdbc.JdbcPreparedStatement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLInjectionUtils {
    public static void testInjectionInsert(Connection connection) throws SQLException {
        String person = "injTest (id, name, group_id) values (112, 'dummy', '50'); delete from injTest;-- ";
//        String person = "injTest";
        Statement s = connection.createStatement();
        s.executeUpdate("insert into " + person + " (id, name, group_id) values (111, 'al', '50')");
    }

    /**
     * Injecting table name
     * @param connection
     * @throws SQLException
     */
    public static void testInjectionInsertStringFormat(Connection connection) throws SQLException {
        String person = "injTest (id, name, group_id) values (?, ?, (select name from person where id=155)); delete from injTest where group_id='50';-- ";
//        String person = "injTest";
        String string = String.format("insert into %s (id, name, group_id) values (?, ?, '50')", person);
        JdbcPreparedStatement selectStatement = (JdbcPreparedStatement) connection.prepareStatement(string);
        selectStatement.setInt(1, 1111);
        selectStatement.setString(2, "50");
        selectStatement.executeUpdate();
    }

    public static void testInjectionSelectCount(Connection connection) throws SQLException {
//        String tableName = "injTest; delete from injTest where group_id='50';-- ";
        String tableName = "injTest union select id from person";
//        String tableName = "injTest";
//        tableName = ESAPI.encoder().encodeForSQL(new MySQLCodec(MySQLCodec.Mode.STANDARD), tableName);
        Statement s = connection.createStatement();
        String string = "SELECT COUNT(*) FROM " + tableName;
        ResultSet rs = s.executeQuery(string);
        while (rs.next()) {
            System.out.println(rs.getInt(1));
        }
    }

    public static void testInjectionOrder(Connection connection) throws SQLException {
//        String person = "injTest (id, name, group_id) values (112, 'dummy', '50'); delete from injTest where group_id='50';-- ";
        String person = "2";
        Statement s = connection.createStatement();
        ResultSet resultSet = s.executeQuery("select * from {injTest} where group_id='70' order by " + person);
        TestUtils.printResultSet(resultSet);
    }

    public static void testInjectionDeleteOrder(Connection connection) throws SQLException {
        String colName = "id); delete from injTest;-- ";
//        String colName = "id";
        Statement s = connection.createStatement();
        s.executeUpdate("delete from injTest where id in (select top 3 id from injTest order by " + colName + " desc)");
    }


    public static void testInjectionSelect(Connection connection) throws SQLException {
        String column = "id UNION select * from injTest--";
//        String column = "id";
        Statement s = connection.createStatement();
        ResultSet resultSet = s.executeQuery("select * from injTest where {" + column + "} between 1 and 10");
        TestUtils.printResultSet(resultSet);
    }

    public static void testInjectionSelectPS(Connection connection) throws SQLException {
//        String column = "id between ? and ? UNION select * from injTest--";
        String column = "id";
        int i1 = 1;
        int i2 = 10;

        JdbcPreparedStatement selectStatement = (JdbcPreparedStatement)
                connection.prepareStatement("select * from injTest where " + column + " between ? and ?");
        selectStatement.setInt(1, i1);
        selectStatement.setInt(2, i2);
        ResultSet rs = selectStatement.executeQuery();
        TestUtils.printResultSet(rs);
    }

    /**
     * Standard Mysql codec escapes
     * Oracle does not
     * @param connection
     * @throws SQLException
     */
    public static void testInjectionDelete(Connection connection) throws SQLException {
        String table = "injTest where 1=1 or id=?--";
//        table = ESAPI.encoder().encodeForSQL(new MySQLCodec(MySQLCodec.Mode.STANDARD), table);
//        String column = "id";
        int i1 = 9999;
        String deleteSql = String.format("DELETE FROM %s WHERE id=?", table);

        JdbcPreparedStatement selectStatement = (JdbcPreparedStatement)
                connection.prepareStatement(deleteSql);
        selectStatement.setInt(1, i1);
        selectStatement.executeUpdate();
    }

    public static void testInjectionUpdate(Connection connection) throws SQLException {
        String table = "injTest";
        String paramName = "name = 'dummy', group_id";
//        table = ESAPI.encoder().encodeForSQL(new OracleCodec(), table);
//        String column = "id";
        int i1 = 10;
        String updateSql = "update " + table + " set " + paramName + " = ? where id = ?";
        JdbcPreparedStatement selectStatement = (JdbcPreparedStatement)
                connection.prepareStatement(updateSql);
        selectStatement.setInt(2, i1);
        selectStatement.setString(1, "abuzer");
        selectStatement.executeUpdate();
    }

    public static void testInjectionCreate(Connection connection) throws SQLException {
        String table = "abc] (dum int, dumd y); --";
//        String column = "id";
        Statement s = connection.createStatement();
        s.executeUpdate("create table [" + table + "] ( phoneNo int)");
//        s.executeQuery("drop table [" + table + "]");
    }

    public static void testInjectionPreparedStatementLike(Connection connection) throws SQLException {
        String param = "30";
        String prefix = "' or 1=1 --";
//        String prefix = "er1";
        JdbcPreparedStatement selectStatement = (JdbcPreparedStatement)
                connection.prepareStatement("delete from injTest where group_id=? and id like '%" + prefix + "%'");
        selectStatement.setString(1, param);
        selectStatement.executeUpdate();
//        ResultSet resultSet = selectStatement.executeQuery();
//        printResultSet(resultSet);
    }
}
