package com.oneandone.ejbcdiunit.persistencefactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import com.oneandone.ejbcdiunit.persistence.TestPersistenceFactory;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class TestPersistenceFactoryAlternative extends TestPersistenceFactory {
    @Override
    protected String getPersistenceUnitName() {
        return "testalternative";
    }

    @Produces
    @Default
    @Override
    public EntityManager produceEntityManager() {
        return super.produceEntityManager();
    }

    @Produces
    @Default
    @Override
    public DataSource produceDataSource() {
        return super.produceDataSource();
    }

}
