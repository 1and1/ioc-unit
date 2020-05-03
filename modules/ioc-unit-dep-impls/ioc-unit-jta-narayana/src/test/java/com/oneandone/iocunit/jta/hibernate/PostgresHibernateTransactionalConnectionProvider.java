package com.oneandone.iocunit.jta.hibernate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.naming.NamingException;

import org.postgresql.xa.PGXADataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import com.arjuna.ats.jdbc.TransactionalDriver;

/**
 * @author aschoerk
 */
public class PostgresHibernateTransactionalConnectionProvider extends ConnectionProviderBase {
    private static final long serialVersionUID = 5136867989194663732L;
    private final String url;
    private final TransactionalDriver arjunaJDBC2Driver;
    private final Properties dbProps;
    private final PostgreSQLContainer container;
    PGXADataSource dataSource;
    boolean initial = true;
    AtomicInteger connectionCount = new AtomicInteger();

    public PostgresHibernateTransactionalConnectionProvider() throws NamingException, ClassNotFoundException, SQLException {
        container = new PostgreSQLContainer();
        container.start();
        Class.forName("org.postgresql.Driver");
        dataSource = new PGXADataSource();
        // url = "jdbc:postgresql:9.6.8:///test?TC_INITFUNCTION=com.oneandone.iocunit.jta.hibernate.PostgresHibernateTransactionalConnectionProvider::postgresInitFunction";
        url = "jdbc:postgresql:testdb";
        dataSource.setURL(container.getJdbcUrl());
        dbProps = new Properties();
        dbProps.setProperty("user", container.getUsername());
        dbProps.setProperty("password", container.getPassword());
        dbProps.setProperty("ssl", "true");
        dbProps.put(TransactionalDriver.XADataSource, dataSource);
        this.arjunaJDBC2Driver = new TransactionalDriver();
    }

    public static void postgresInitFunction(Connection connection) throws SQLException {

    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection result = arjunaJDBC2Driver.connect("jdbc:arjuna:" + url, dbProps);
        connectionCount.incrementAndGet();
        return result;
    }

    @Override
    public void closeConnection(final Connection connection) throws SQLException {
        try {
            super.closeConnection(connection);
        } finally {
            int count = connectionCount.decrementAndGet();
            // if(count == 0)
            //     container.close();
        }
    }
}
