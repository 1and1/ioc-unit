package com.oneandone.iocunitejb.example1_hb;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.ejb.persistence.JdbcSqlConverter;
import com.oneandone.iocunit.jpa.HibernatePersistenceFactory;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses({ HibernatePersistenceFactory.class,
        ServiceHibernateXmlLessJdbcSqlConverterLinkTest.LocalJdbcSqlConverter.class})
public class ServiceHibernateXmlLessJdbcSqlConverterLinkTest extends TestBase {
    @After
    public void checkCount() {
        Assert.assertTrue(localJdbcSqlConverter.getCallsCount() > 0);
    }
    @Ignore
    @Test
    public void canServiceReturnFive() {
        super.canServiceReturnFive();
    }

    @Inject
    LocalJdbcSqlConverter localJdbcSqlConverter;

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

}
