package com.oneandone.ejbcdiunit.persistencefactory;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.h2.jdbc.JdbcSQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.cdi.tester.CdiUnit2Runner;
import com.oneandone.cdi.tester.ejb.persistence.TestPersistenceFactory;
import com.oneandone.ejbcdiunit.entities.TestEntity1;

/**
 * Shows how TestPersistenceFactory allows it to override hibernate-properties using system-properties.
 *
 * @author aschoerk
 */
@RunWith(CdiUnit2Runner.class)
@TestClasses({ TestPersistenceFactory.class, TestEntity1.class })
public class TestProperty1 extends PersistenceFactoryTestBase {

    @Inject
    EntityManager entityManager;

    @BeforeClass
    public static void setSchema() {
        System.setProperty("hibernate.default_schema", "schema");
        System.setProperty("hibernate.connection.url",
                "jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=0;INIT=create schema if not exists schema;LOCK_MODE=0");
    }

    @AfterClass
    public static void clearSchema() {
        System.clearProperty("hibernate.default_schema");
        System.clearProperty("hibernate.connection.url");
    }

    @After
    public void checkSchema() {
        entityManager.createNativeQuery("select * from schema.test_entity_1").getResultList();
    }

    @Test
    public void test() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        entityManager.persist(new TestEntity1());
        userTransaction.commit();
    }

    @Test(expected = PersistenceException.class)
    public void doesFlushBeforeNativeQuery() throws Exception {
        super.doesFlushBeforeNativeQuery();
    }

    @Test(expected = JdbcSQLException.class)
    public void checkUserTransactionAndDataSource()
            throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException, SQLException {
        super.checkUserTransactionAndDataSource();
    }
}
