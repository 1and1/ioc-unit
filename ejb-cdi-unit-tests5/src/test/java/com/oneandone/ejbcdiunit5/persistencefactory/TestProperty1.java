package com.oneandone.ejbcdiunit5.persistencefactory;

import com.oneandone.ejbcdiunit.entities.TestEntity1;
import com.oneandone.ejbcdiunit.persistence.TestPersistenceFactory;
import com.oneandone.ejbcdiunit5.JUnit5Extension;
import org.h2.jdbc.JdbcSQLException;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.transaction.*;
import java.sql.SQLException;

/**
 * Shows how TestPersistenceFactory allows it to override hibernate-properties using system-properties.
 *
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@AdditionalClasses({ TestPersistenceFactory.class, TestEntity1.class })
public class TestProperty1 extends PersistenceFactoryTestBase {

    @Inject
    EntityManager entityManager;

    @BeforeAll
    public static void setSchema() {
        System.setProperty("hibernate.default_schema", "schema");
        System.setProperty("hibernate.connection.url",
                "jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_DELAY=0;INIT=create schema if not exists schema;LOCK_MODE=0");
    }

    @AfterAll
    public static void clearSchema() {
        System.clearProperty("hibernate.default_schema");
        System.clearProperty("hibernate.connection.url");
    }

    @AfterEach
    public void checkSchema() {
        entityManager.createNativeQuery("select * from schema.test_entity_1").getResultList();
    }

    @Test
    public void test() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        entityManager.persist(new TestEntity1());
        userTransaction.commit();
    }

    @Test
    public void doesFlushBeforeNativeQuery() throws Exception {
        Assertions.assertThrows(PersistenceException.class, () -> super.doesFlushBeforeNativeQuery());
    }

    @Test
    public void checkUserTransactionAndDataSource()
            throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException, SQLException {
        Assertions.assertThrows(JdbcSQLException.class, () -> super.checkUserTransactionAndDataSource());
    }
}
