package com.oneandone.iocunit.jtajpa;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;

/**
 * @author aschoerk
 */
class Q1Factory extends JtaEntityManagerFactoryBase {
    @Override
    public String getPersistenceUnitName() {
        return "q1";
    }

    @Override
    @Q1
    @Produces
    public EntityManager produceEntityManager() {
        return super.produceEntityManager();
    }
}
