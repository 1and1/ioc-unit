package com.oneandone.ejbcdiunit5.persistencefactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.JUnit5Extension;
import com.oneandone.iocunit.ejb.persistence.SinglePersistenceFactory;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@TestClasses({ PFH2Test.TestDbSinglePersistenceFactory.class })
public class PFH2Test extends PersistenceFactoryTestBase {

    @ApplicationScoped
    public static class TestDbSinglePersistenceFactory extends SinglePersistenceFactory {

        @Produces
        @Override
        public EntityManager newEm() {
            return produceEntityManager();
        }

        /**
         * create a jdbc-Datasource using the same driver url user and password as the entityManager
         *
         * @return a jdbc-Datasource using the same driver url user and password as the entityManager
         */
        @Produces
        @Override
        public DataSource produceDataSource() {
            return super.produceDataSource();
        }
    }

}
