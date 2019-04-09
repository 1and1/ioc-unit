package iocunit.ejbresource;

import javax.enterprise.inject.Produces;
import javax.sql.DataSource;

import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.ejb.persistence.TestPersistenceFactory;

import iocunit.ejbresource.two_different_resources.DSQual;

/**
 * @author aschoerk
 */
@TestClasses({TestPersistenceFactory.class})
public class Test2Datasources {

    public static class Datasource2PersistenceFactory extends TestPersistenceFactory {

        @Override
        protected String getFilenamePrefix() {
            return "ds2";
        }

        @Produces
        @DSQual
        DataSource produceDataSource2() {
            return createDataSource();
        }
    }


}
