package com.oneandone.iocunit.jpa.jpa;

import java.util.HashMap;

import javax.persistence.EntityManagerFactory;

/**
 * @author aschoerk
 */
public interface JpaProvider {
    ConnectionDelegate connectionBy(PersistenceFactory persistenceFactory);
    boolean close(ConnectionDelegate connection);

    EntityManagerFactory createEntityManagerFactory(PersistenceFactory persistenceFactory, HashMap<String, Object> properties);
}
