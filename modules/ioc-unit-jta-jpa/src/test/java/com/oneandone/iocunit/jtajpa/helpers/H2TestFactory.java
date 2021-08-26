package com.oneandone.iocunit.jtajpa;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;

/**
 * @author aschoerk
 */
class H2TestFactory extends JtaEntityManagerFactoryBase {
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
