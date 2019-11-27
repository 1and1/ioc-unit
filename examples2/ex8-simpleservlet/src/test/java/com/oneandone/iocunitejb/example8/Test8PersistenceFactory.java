package com.oneandone.iocunitejb.example8;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;

import com.oneandone.iocunit.jpa.XmlAwarePersistenceFactory;

/**
 * @author aschoerk
 */
class Test8PersistenceFactory extends XmlAwarePersistenceFactory {
    @Override
    public String getPersistenceUnitName() {
        return "test";
    }

    @Produces
    EntityManager produceEm() {
        return super.produceEntityManager();
    }
}
