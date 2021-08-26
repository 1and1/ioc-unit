package com.oneandone.iocunit.jtajpa;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;

/**
 * @author aschoerk
 */
class Q2Factory extends JtaEntityManagerFactoryBase {
    @Override
    public String getPersistenceUnitName() {
        return "q2";
    }

    @Override
    @Q2
    @Produces
    public EntityManager produceEntityManager() {
        return super.produceEntityManager();
    }
}
