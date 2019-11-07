package com.oneandone.iocunit.jpa;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class XmlLessInitializingPersistenceFactory
        extends XmlLessPersistenceFactory implements DataSourceInitializing {

    @Override
    protected DataSource doInFirstConnection(DataSource ds) {
        try (Connection conn = ds.getConnection()) {
            try(Statement stmt = conn.createStatement()) {
                if (clearDb())
                    stmt.execute("drop all objects");
                String initialSchemaName = initialSchemaName();
                if (initialSchemaName != null) {
                    stmt.execute("create schema if not exists " + initialSchemaName);
                }
                for (String s: initStatements()) {
                    stmt.execute(s);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ds;
    }

    @Produces
    @Override
    public DataSource produceDataSource() {
        return super.produceDataSource();
    }

    @Produces
    @Override
    public EntityManager produceEntityManager() {
        return super.produceEntityManager();
    }

}
