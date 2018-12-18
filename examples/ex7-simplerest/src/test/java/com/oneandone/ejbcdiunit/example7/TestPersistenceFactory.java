package com.oneandone.ejbcdiunit.example7;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;

import com.oneandone.cdi.tester.ejb.persistence.PersistenceFactory;

/**
 * @author aschoerk
 */
public class TestPersistenceFactory extends PersistenceFactory {
    @Override
    protected String getPersistenceUnitName() {
        return "test";
    }

    @Produces
    EntityManager produceEm() {
        return super.produceEntityManager();
    }
}
