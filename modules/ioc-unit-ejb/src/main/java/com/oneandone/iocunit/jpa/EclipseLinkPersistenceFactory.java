package com.oneandone.iocunit.jpa;

import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import javax.sql.DataSource;

/**
 * @author aschoerk
 */
public class EclipseLinkPersistenceFactory extends XmlLessPersistenceFactory {

    @Override
    protected Provider getRecommendedProvider() {
        return Provider.ECLIPSELINK;
    }

    @Produces
    @Default
    @Override
    public EntityManager produceEntityManager() {
        return super.produceEntityManager();
    }

    /**
     * create a jdbc-Datasource using the same driver url user and password as the entityManager
     *
     * @return a jdbc-Datasource using the same driver url user and password as the entityManager
     */
    @Produces
    @Default
    @Override
    public DataSource produceDataSource() {
        return super.produceDataSource();
    }
}
