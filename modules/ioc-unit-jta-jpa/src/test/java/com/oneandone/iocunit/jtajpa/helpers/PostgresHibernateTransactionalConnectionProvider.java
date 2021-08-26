package com.oneandone.iocunit.jtajpa.helpers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import javax.naming.NamingException;

import org.postgresql.xa.PGXADataSource;

import com.arjuna.ats.jdbc.TransactionalDriver;
import com.oneandone.iocunit.jtajpa.internal.ConnectionProviderBase;

/**
 * @author aschoerk
 */
public class PostgresHibernateTransactionalConnectionProvider extends ConnectionProviderBase {
    public static final String DBPASSWORD = "DBPASSWORD";
    public static final String DBUSER = "DBUSER";
    public static final String DBURL = "DBURL";
    private static final long serialVersionUID = 5136867989194663732L;
    private final String url;
    private final TransactionalDriver arjunaJDBC2Driver;
    private final Properties dbProps;
    PGXADataSource dataSource;
    boolean initial = true;
    AtomicInteger connectionCount = new AtomicInteger();


    public PostgresHibernateTransactionalConnectionProvider() throws NamingException, ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        dataSource = new PGXADataSource();
        // url = "jdbc:postgresql:9.6.8:///test?TC_INITFUNCTION=com.oneandone.iocunit.jta.hibernate.PostgresHibernateTransactionalConnectionProvider::postgresInitFunction";
        url = "jdbc:postgresql:testdb";
        dataSource.setURL(System.getProperty(DBURL));
        dbProps = new Properties();
        dbProps.setProperty("user", System.getProperty(DBUSER));
        dbProps.setProperty("password", System.getProperty(DBPASSWORD));
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
