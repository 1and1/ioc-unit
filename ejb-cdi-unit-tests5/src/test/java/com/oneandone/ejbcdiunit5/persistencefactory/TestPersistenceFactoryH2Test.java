package com.oneandone.ejbcdiunit5.persistencefactory;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.ejbcdiunit.entities.TestEntity1;
import com.oneandone.ejbcdiunit.persistence.TestPersistenceFactory;
import com.oneandone.ejbcdiunit5.JUnit5Extension;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@AdditionalClasses({ TestPersistenceFactory.class, TestEntity1.class })
public class TestPersistenceFactoryH2Test extends PersistenceFactoryTestBase {

    @BeforeAll
    public static void beforeTestPersistenceFactoryH2Test() {
        System.setProperty("hibernate.connection.url",
                "jdbc:h2:mem:testIntercepted;MODE=MySQL;DB_CLOSE_ON_EXIT=TRUE;MVCC=TRUE;" +
                        "INIT=create schema if not exists testschema;DB_CLOSE_DELAY=0;LOCK_MODE=0;LOCK_TIMEOUT=10000");
        System.setProperty("hibernate.default_schema", "testschema");
        System.setProperty("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");
    }

    boolean isHibernate5() {
        try {
            Class.forName("org.hibernate.boot.model.naming.PhysicalNamingStrategy");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    protected String getStringAttributeNativeName() {
        return isHibernate5() ? "stringAttribute" : "string_attribute";
    }

    protected String getIntAttributeNativeName() {
        return isHibernate5() ? "intAttribute" : "int_attribute";
    }

    @AfterAll
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
