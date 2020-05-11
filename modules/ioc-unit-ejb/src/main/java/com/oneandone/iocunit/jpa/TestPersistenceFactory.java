package com.oneandone.iocunit.jpa;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

/**
 * @author aschoerk
 */
public class TestPersistenceFactory extends XmlAwarePersistenceFactory {

    @Override
    public String getPersistenceUnitName() {
        return "test";
    }

    @Produces
    @Override
    public EntityManager produceEntityManager() {
        return super.produceEntityManager();
    }

    @Produces
    @Override
    public DataSource produceDataSource() {
        return super.produceDataSource();
    }
}
