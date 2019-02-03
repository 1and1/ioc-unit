package com.oneandone.iocunitejb.example8;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;

import com.oneandone.iocunit.ejb.persistence.PersistenceFactory;

/**
 * @author aschoerk
 */
class Test8PersistenceFactory extends PersistenceFactory {
    @Override
    protected String getPersistenceUnitName() {
        return "test";
    }

    @Produces
    EntityManager produceEm() {
        return super.produceEntityManager();
    }
}
