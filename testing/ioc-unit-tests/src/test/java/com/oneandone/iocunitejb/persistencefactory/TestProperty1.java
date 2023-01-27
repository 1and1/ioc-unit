package com.oneandone.iocunitejb.persistencefactory;

import static org.junit.Assert.fail;

import java.sql.SQLException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.sql.DataSource;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jpa.XmlLessPersistenceFactory;
import com.oneandone.iocunitejb.entities.TestEntity1;

/**
 * Shows how TestPersistenceFactory allows it to override hibernate-properties using system-properties.
 *
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses({TestProperty1.PersistenceFactory.class, TestEntity1.class})
public class TestProperty1 extends PersistenceFactoryTestBase {

    @Inject
    EntityManager entityManager;

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

    public void checkUserTransactionAndDataSource()
            throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException, SQLException {
        try {
            super.checkUserTransactionAndDataSource();
            fail("Expected Exception");
        } catch (Exception e) {
            Assert.assertTrue(
                    e.getClass().getName().contains("JdbcSQLException") ||
                    e.getClass().getName().contains("JdbcSQLSyntaxErrorException"));

        }
    }

    @ApplicationScoped
    public static class PersistenceFactory extends XmlLessPersistenceFactory {
        public PersistenceFactory() {
            addProperty("hibernate.default_schema", "schema");
            addProperty("hibernate.connection.url",
                    "jdbc:h2:mem:testIntercepted;MODE=MySQL;DB_CLOSE_DELAY=0;INIT=create schema if not exists schema;LOCK_MODE=0");
        }

        @Produces
        @Override
        public EntityManager produceEntityManager() {
            return super.produceEntityManager();
        }

        @Produces
        @Override
        public DataSource produceDataSource() {
            return super.produceDataSource();
        }


    }
}
