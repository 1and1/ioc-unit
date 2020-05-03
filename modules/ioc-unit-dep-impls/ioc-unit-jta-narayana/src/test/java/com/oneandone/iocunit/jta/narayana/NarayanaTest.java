package com.oneandone.iocunit.jta.narayana;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.SynchronizationType;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.arjuna.ats.jdbc.TransactionalDriver;
import com.oneandone.iocunit.IocUnitRunner;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
public class NarayanaTest {

    @Inject
    UserTransaction userTransaction;

    @Inject
    SutClass sutClass;

    EntityManagerFactory entityManagerFactory;
    @Inject
    EntityManager entityManager;

    @Produces
    EntityManager entityManager() {
        if (entityManagerFactory == null)
            entityManagerFactory = Persistence.createEntityManagerFactory("jtspgdb");
        return entityManagerFactory.createEntityManager(SynchronizationType.SYNCHRONIZED);
    }

    @Test
    public void test() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        sutClass.testDefaultCall();
        userTransaction.rollback();
        userTransaction.begin();
        sutClass.testRequiredCall();
        userTransaction.commit();
    }

    @Test
    public void test2() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        sutClass.testDefaultCall();
        userTransaction.rollback();
        userTransaction.begin();
        sutClass.testRequiredCall();
        userTransaction.commit();
    }

    @Test
    public void testJdbc() throws SQLException {
        // String transactionDriverDefinitionUrl = TransactionalDriver.arjunaDriver;
        JdbcDataSource xaDataSource = new org.h2.jdbcx.JdbcDataSource();
        xaDataSource.setUrl("jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=0");
        xaDataSource.setUser("sa");
        xaDataSource.setPassword("");
        Properties dbProps = new Properties();


        dbProps.put(TransactionalDriver.userName, "sa");

        dbProps.put(TransactionalDriver.password, "");

        dbProps.put(TransactionalDriver.XADataSource, xaDataSource);


        TransactionalDriver arjunaJDBC2Driver = new TransactionalDriver();

        try (Connection connection = arjunaJDBC2Driver.connect("jdbc:arjuna:jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=0\"", dbProps)) {
            try(Statement stmt = connection.createStatement()) {
                stmt.execute("create table a (a integer)");
                stmt.execute("insert into a (a) values (1)");
            }
        }
    }
}
