package com.oneandone.iocunit.jpa;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.sql.DataSource;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class XmlLessInitializingPersistenceFactory
        extends XmlLessPersistenceFactory implements DataSourceInitializing {

    @Produces
    @Override
    public DataSource produceDataSource() {
        return super.produceDataSource();
    }

    @Produces
    @Override
    @Default
    public EntityManager produceEntityManager() {
        return super.produceEntityManager();
    }

    @Override
    public DataSource doInFirstConnection(final DataSource ds) {
        return doInFirstConnectionH2(ds);
    }
}
