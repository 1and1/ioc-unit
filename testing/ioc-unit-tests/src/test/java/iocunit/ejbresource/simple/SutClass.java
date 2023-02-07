package iocunit.ejbresource.simple;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.inject.Inject;
import javax.sql.DataSource;

/**
 * @author aschoerk
 */
public class SutClass {
    @Inject
    DataSource dataSource;

    public void createTableT() throws SQLException {
        createTable("T");
    }

    private void createTable(String name) throws SQLException {
        try(Connection conn = dataSource.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("create table " + name + " (str varchar)");
            }
        }
    }

    public void createTableS() throws SQLException {
        createTable("S");
    }
}
