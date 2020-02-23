package com.oneandone.iocunit.jpa;

import java.util.HashMap;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import com.oneandone.iocunit.jpa.jpa.JpaProvider;
import com.oneandone.iocunit.jpa.jpa.PersistenceFactory;

/**
 * @author aschoerk
 */
public class XmlLessPersistenceFactory extends PersistenceFactory implements PersistenceFactoryIntf {
    @Inject
    JpaProvider jpaProvider;

    @Override
    public String getPersistenceUnitName() {
        return null;
    }

    @Override
    protected EntityManagerFactory createEntityManagerFactory() {
        return jpaProvider.createEntityManagerFactory(this, getProperties());
    }

    public XmlLessPersistenceFactory() {

    }

    public HashMap<String, Object> getProperties() {
        return new HashMap<>();
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
