package com.oneandone.ejbcdiunit5.persistencefactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.ejb.persistence.TestPersistenceFactory;
import com.oneandone.iocunitejb.entities.TestEntity1;

/**
 * @author aschoerk
 */
@ExtendWith(IocJUnit5Extension.class)
@TestClasses({ TestPersistenceFactoryH2Test.TestDbSinglePersistenceFactory.class, TestEntity1.class })
public class TestPersistenceFactoryH2Test extends PersistenceFactoryTestBase {

    @ApplicationScoped
    public static class TestDbSinglePersistenceFactory extends TestPersistenceFactory {


        @Override
        protected String getSchema() {
            return "testschema";
        }

        @Override
        public boolean dropAllObjects() {
            return false;
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
