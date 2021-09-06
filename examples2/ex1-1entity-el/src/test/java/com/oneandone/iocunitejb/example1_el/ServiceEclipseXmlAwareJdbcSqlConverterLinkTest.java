package com.oneandone.iocunitejb.example1_el;

import static com.oneandone.iocunit.ejb.persistence.PersistenceFactory.Provider.ECLIPSELINK;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.ejb.persistence.JdbcSqlConverter;
import com.oneandone.iocunit.jpa.XmlAwarePersistenceFactory;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses({ ServiceEclipseXmlAwareJdbcSqlConverterLinkTest.EclipselinkPersistenceFactory.class,
        ServiceEclipseXmlAwareJdbcSqlConverterLinkTest.LocalJdbcSqlConverter.class})
public class ServiceEclipseXmlAwareJdbcSqlConverterLinkTest extends TestBase {

    @Inject
    LocalJdbcSqlConverter localJdbcSqlConverter;

    @After
    public void checkCount() {
        Assert.assertTrue(localJdbcSqlConverter.getCallsCount() > 0);
    }

    @Ignore
    @Test
    public void canServiceReturnFive() {
        super.canServiceReturnFive();
    }

    @ApplicationScoped
    public static class LocalJdbcSqlConverter implements JdbcSqlConverter {
        private int callsCount = 0;

        @Override
        public String convert(final String sql) {
            callsCount ++;
            return sql;
        }

        public int getCallsCount() {
            return callsCount;
        }
    }

    public static class EclipselinkPersistenceFactory extends XmlAwarePersistenceFactory {

        @Override
        protected Provider getRecommendedProvider() {
            return ECLIPSELINK;
        }

        @Override
        public String getPersistenceUnitName() {
            return "test";
        }

        @Produces
        @Default
        @Override
        public EntityManager produceEntityManager() {
            return super.produceEntityManager();
        }

        /**
         * create a jdbc-Datasource using the same driver url user and password as the entityManager
         *
         * @return a jdbc-Datasource using the same driver url user and password as the entityManager
         */
        @Produces
        @Default
        @Override
        public DataSource produceDataSource() {
            return super.produceDataSource();
        }
    }


}
