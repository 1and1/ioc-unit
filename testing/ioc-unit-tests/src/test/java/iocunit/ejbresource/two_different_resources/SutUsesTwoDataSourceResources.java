package iocunit.ejbresource.two_different_resources;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.annotation.Resource;
import jakarta.enterprise.inject.Produces;
import jakarta.sql.DataSource;

/**
 * @author aschoerk
 */
public class SutUsesTwoDataSourceResources {
    @Produces
    @Resource(name = "datasourcename1")
    DataSource dataSource1;

    @Produces
    @DSQual
    @Resource(name = "datasourcename2")
    DataSource dataSource2;

    private boolean executeInDatasource(DataSource ds, String stmtsrc) {
        try (Connection conn = ds.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                return stmt.execute(stmtsrc);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean doInDb1(String cmd) {
        return executeInDatasource(dataSource1, cmd);
    }

    public boolean doInDb2(String cmd) {
        return executeInDatasource(dataSource2, cmd);
    }

}
