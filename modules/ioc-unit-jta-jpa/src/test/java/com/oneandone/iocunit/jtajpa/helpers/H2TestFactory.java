package com.oneandone.iocunit.jtajpa.helpers;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;

import com.oneandone.iocunit.jtajpa.JtaEntityManagerFactoryBase;
import com.oneandone.iocunit.jtajpa.PersistenceContextQualifier;

/**
 * @author aschoerk
 */
public class H2TestFactory extends JtaEntityManagerFactoryBase {
    @Override
    public String getPersistenceUnitName() {
        return "test";
    }

    @Override
    @PersistenceContextQualifier(unitName = "test")
    @Produces
    public EntityManager produceEntityManager() {
        return super.produceEntityManager();
    }
}
