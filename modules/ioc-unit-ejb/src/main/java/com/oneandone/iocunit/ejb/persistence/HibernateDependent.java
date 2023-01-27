package com.oneandone.iocunit.ejb.persistence;

import java.util.HashMap;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.spi.PersistenceUnitInfo;

import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;

/**
 * @author aschoerk
 */
public class HibernateDependent {
    static public  EntityManagerFactory createFromPersistenceUnit(PersistenceUnitInfo pu, HashMap<String, Object> properties) {
        try {
            return new EntityManagerFactoryBuilderImpl(new PersistenceUnitInfoDescriptor(pu), properties).build();
        } catch (Throwable thw) {
            throw new RuntimeException(thw);
        }
    }
}
