package com.oneandone.iocunit.jpa.hibernate5;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.Session;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.spi.SessionImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.jpa.jpa.ConnectionDelegate;
import com.oneandone.iocunit.jpa.jpa.JpaProvider;
import com.oneandone.iocunit.jpa.jpa.PersistenceFactory;


/**
 * Manages the singleton used to mock JMS in CDI-Unit using rabbitmq.
 *
 * @author aschoerk
 */
@Singleton
public class JpaProviderImpl implements JpaProvider {

    private Logger logger = LoggerFactory.getLogger("JmsFactory");


    public ConnectionDelegate bySession(SessionImplementor sessionImplementor) {
        Connection connection = null;
        Object jdbcConnectionAccess = null;
        try {
            try {
                Method method = SessionImplementor.class.getMethod("connection");
                connection = (Connection) method.invoke(sessionImplementor);
                connection.setAutoCommit(false);

            } catch (NoSuchMethodException e) {
                try {
                    Method method = SessionImplementor.class.getMethod("getJdbcConnectionAccess");
                    jdbcConnectionAccess = method.invoke(sessionImplementor);
                    connection = ((JdbcConnectionAccess) jdbcConnectionAccess).obtainConnection();
                } catch (NoSuchMethodException | SQLException e1) {
                    throw new RuntimeException(e1);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            ConnectionDelegate result = new ConnectionDelegate(connection, null, false);
            result.setJpaInfo(jdbcConnectionAccess);
            result.setJpaProviderProvider(this);
            return result;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public ConnectionDelegate connectionBy(PersistenceFactory persistenceFactory) {
        try {
            EntityManager tmp = persistenceFactory.getTransactional(false);
            Session session = tmp.unwrap(Session.class);
            SessionImplementor sessionImplementor = (SessionImplementor) session;
            return bySession(sessionImplementor);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean close(final ConnectionDelegate connection) {
        if(connection.getJpaInfo() == null)
            return false;
        try {
            Method method = connection.getJpaInfo().getClass().getMethod("releaseConnection", Connection.class);
            method.invoke(connection.getJpaInfo(), connection);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public EntityManagerFactory createEntityManagerFactory(PersistenceFactory persistenceFactory, HashMap<String, Object> properties) {
        return new Hibernate5XmlLessPersistenceFactory().createEntityManagerFactoryWOPersistenceXml(persistenceFactory, properties);
    }


}