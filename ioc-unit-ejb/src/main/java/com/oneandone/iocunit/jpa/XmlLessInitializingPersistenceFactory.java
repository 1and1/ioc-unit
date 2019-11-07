package com.oneandone.iocunit.jpa;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

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
    public EntityManager produceEntityManager() {
        return super.produceEntityManager();
    }

}
