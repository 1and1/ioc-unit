package com.oneandone.ejbcdiunit5.persistencefactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jpa.XmlLessPersistenceFactory;
import com.oneandone.iocunitejb.entities.TestEntity1;

/**
 * @author aschoerk
 */
@ExtendWith(IocJUnit5Extension.class)
@TestClasses({ TestPersistenceFactoryH2Test.PersistenceFactory.class, TestEntity1.class })
public class TestPersistenceFactoryH2Test extends PersistenceFactoryTestBase {

    @ApplicationScoped
    public static class PersistenceFactory extends XmlLessPersistenceFactory {

        public PersistenceFactory() {
            addProperty("hibernate.connection.url",
                    "jdbc:h2:mem:testIntercepted;MODE=MySQL;DB_CLOSE_ON_EXIT=TRUE;" +
                    "INIT=create schema if not exists testschema;DB_CLOSE_DELAY=0;LOCK_MODE=0;LOCK_TIMEOUT=10000");
            addProperty("hibernate.default_schema", "testschema");
            addProperty("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");
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

    protected String getSchema() {
        return "testschema.";
    }

    @Override
    public void doesFlushBeforeNativeQuery() throws Exception {

    }
}
