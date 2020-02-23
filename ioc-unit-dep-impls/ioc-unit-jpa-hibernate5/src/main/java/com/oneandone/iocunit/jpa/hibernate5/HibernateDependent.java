package com.oneandone.iocunit.jpa.hibernate5;

import java.util.HashMap;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;

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
