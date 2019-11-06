package com.oneandone.iocunitejb.example7;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;

import com.oneandone.iocunit.ejb.persistence.XmlAwarePersistenceFactory;

/**
 * @author aschoerk
 */
class Test7PersistenceFactory extends XmlAwarePersistenceFactory {
    @Override
    protected String getPersistenceUnitName() {
        return "test";
    }

    @Produces
    EntityManager produceEm() {
        return super.produceEntityManager();
    }
}
