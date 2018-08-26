package com.oneandone.ejbcdiunit5.persistencefactory;

import com.oneandone.ejbcdiunit.persistence.SinglePersistenceFactory;
import com.oneandone.ejbcdiunit5.JUnit5Extension;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@AdditionalClasses({ PFH2Test.TestDbSinglePersistenceFactory.class })
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
