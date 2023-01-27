package com.oneandone.iocunit.jpa;

import java.util.UUID;

import jakarta.persistence.EntityManagerFactory;

import com.oneandone.iocunit.ejb.persistence.PersistenceFactory;

/**
 * @author aschoerk
 */
abstract public class XmlLessPersistenceFactoryBase extends PersistenceFactory {
    abstract protected EntityManagerFactory createEntityManagerFactory();

    String name = "PU" + UUID.randomUUID().toString();

    @Override
    public String getPersistenceUnitName() {
        return name;
    }
}
