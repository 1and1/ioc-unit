package com.oneandone.iocunit.jtajpa.helpers;

import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;

import com.oneandone.iocunit.jtajpa.JtaEntityManagerFactoryBase;

/**
 * @author aschoerk
 */
public class Q2Factory extends JtaEntityManagerFactoryBase {
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
