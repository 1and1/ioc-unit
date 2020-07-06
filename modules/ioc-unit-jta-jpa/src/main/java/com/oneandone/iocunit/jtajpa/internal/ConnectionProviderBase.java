package com.oneandone.iocunit.jtajpa.internal;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.UnknownUnwrapTypeException;

/**
 * @author aschoerk
 */
abstract public class ConnectionProviderBase  implements ConnectionProvider {
    private static final long serialVersionUID = -3325878198195794589L;

    @Override
    public void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class aClass) {
        return getClass().isAssignableFrom(aClass);
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        if(isUnwrappableAs(aClass)) {
            return (T) this;
        }

        throw new UnknownUnwrapTypeException(aClass);
    }
}
