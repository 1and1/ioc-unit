package iocunit.ejbresource.simple;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author aschoerk
 */
public class SutClassWithQualifiedDatasource {

    @Resource(name = "datasourcename")
    DataSource dataSource;

    public void createTableT() {
        try(Connection conn = dataSource.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("create table t (str varchar)");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
