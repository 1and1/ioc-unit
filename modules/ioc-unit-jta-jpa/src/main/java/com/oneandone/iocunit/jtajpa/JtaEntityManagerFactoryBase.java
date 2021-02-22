package com.oneandone.iocunit.jtajpa;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.iocunit.jtajpa.internal.EntityManagerDelegate;
import com.oneandone.iocunit.jtajpa.internal.EntityManagerFactoryFactory;

/**
 * @author aschoerk
 */
public abstract class JtaEntityManagerFactoryBase {
    @Inject
    EntityManagerFactoryFactory entityManagerFactoryFactory;
    private CreationalContexts creationalContexts;

    public String getPersistenceUnitName() {
        return "test";
    }

    public EntityManager produceEntityManager() {
        try {
            return new EntityManagerDelegate(entityManagerFactoryFactory, getPersistenceUnitName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
