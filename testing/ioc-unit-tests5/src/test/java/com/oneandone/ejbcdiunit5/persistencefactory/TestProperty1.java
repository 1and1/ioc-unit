package com.oneandone.ejbcdiunit5.persistencefactory;

import java.sql.SQLException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.h2.jdbc.JdbcSQLSyntaxErrorException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jpa.XmlLessPersistenceFactory;
import com.oneandone.iocunitejb.entities.TestEntity1;

/**
 * Shows how TestPersistenceFactory allows it to override hibernate-properties using system-properties.
 *
 * @author aschoerk
 */
@ExtendWith(IocJUnit5Extension.class)
@TestClasses(TestProperty1.PersistenceFactory.class)
@SutClasses(TestEntity1.class)
public class TestProperty1 extends PersistenceFactoryTestBase {

    @Inject
    EntityManager entityManager;

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
        Assertions.assertThrows(JdbcSQLSyntaxErrorException.class, () -> super.checkUserTransactionAndDataSource());
    }
}
