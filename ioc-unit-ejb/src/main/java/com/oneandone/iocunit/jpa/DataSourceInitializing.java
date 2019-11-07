package com.oneandone.iocunit.jpa;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

/**
 * @author aschoerk
 */
public interface DataSourceInitializing {
    /**
     * if true, first removes all objects from the Db before first connection
     * @return true if db is to be cleared in the beginning.
     */
    default boolean clearDb() {
        return true;
    }

    /**
     * Create Schema if necessary. Default is dbo.
     *
     * @return Name of a schema to be created before first connection. null,
     * if no schema needs to be created.
     */
    default String initialSchemaName() {
        return null;
    }

    /**
     * Execute statements before first connection.
     *
     * @return statements to be executed before first connection,
     * after potential clearing and schema-creation.
     */
    default List<String> initStatements() {
        return Collections.EMPTY_LIST;
    }

    default DataSource doInFirstConnection(DataSource ds) {
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

}
