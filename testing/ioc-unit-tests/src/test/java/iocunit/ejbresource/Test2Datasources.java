package iocunit.ejbresource;

import javax.enterprise.inject.Produces;
import javax.sql.DataSource;

import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jpa.XmlLessPersistenceFactory;

import iocunit.ejbresource.two_different_resources.DSQual;

/**
 * @author aschoerk
 */
@TestClasses({XmlLessPersistenceFactory.class})
public class Test2Datasources {

    public static class Datasource2PersistenceFactory extends XmlLessPersistenceFactory {

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
