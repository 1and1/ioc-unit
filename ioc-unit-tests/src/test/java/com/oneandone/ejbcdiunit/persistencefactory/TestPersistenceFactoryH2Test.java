package com.oneandone.ejbcdiunit.persistencefactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.cdi.tester.CdiUnit2Runner;
import com.oneandone.cdi.tester.ejb.persistence.TestPersistenceFactory;
import com.oneandone.ejbcdiunit.entities.TestEntity1;

/**
 * @author aschoerk
 */
@RunWith(CdiUnit2Runner.class)
@TestClasses({ TestPersistenceFactory.class, TestEntity1.class })
public class TestPersistenceFactoryH2Test extends PersistenceFactoryTestBase {

    @BeforeClass
    public static void beforeTestPersistenceFactoryH2Test() {
        System.setProperty("hibernate.connection.url",
                "jdbc:h2:mem:test;MODE=MySQL;DB_CLOSE_ON_EXIT=TRUE;MVCC=TRUE;" +
                        "INIT=create schema if not exists testschema;DB_CLOSE_DELAY=0;LOCK_MODE=0;LOCK_TIMEOUT=10000");
        System.setProperty("hibernate.default_schema", "testschema");
        System.setProperty("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");
    }


    @AfterClass
    public static void afterTestPersistenceFactoryH2Test() {
        System.clearProperty("hibernate.connection.url");
        System.clearProperty("hibernate.default_schema");
        System.clearProperty("hibernate.ejb.naming_strategy");
    }

    protected String getSchema() {
        return "testschema.";
    }

    @Override
    public void doesFlushBeforeNativeQuery() throws Exception {

    }
}
