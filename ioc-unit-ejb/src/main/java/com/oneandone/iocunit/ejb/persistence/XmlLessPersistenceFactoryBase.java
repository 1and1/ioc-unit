package com.oneandone.iocunit.ejb.persistence;

import java.util.UUID;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 * @author aschoerk
 */
abstract public class XmlLessPersistenceFactoryBase extends PersistenceFactory {
    abstract protected EntityManagerFactory createEntityManagerFactory();

    String name = "PU" + UUID.randomUUID().toString();

    @Override
    protected String getPersistenceUnitName() {
        return name;
    }

    @Override
    protected BasicDataSource createBasicDataSource() {
        return super.createBasicDataSource();
    }
}
