package com.oneandone.iocunit.jtajpa.helpers;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;

import com.oneandone.iocunit.jtajpa.JtaEntityManagerFactoryBase;

/**
 * @author aschoerk
 */
public class Q1Factory extends JtaEntityManagerFactoryBase {
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
