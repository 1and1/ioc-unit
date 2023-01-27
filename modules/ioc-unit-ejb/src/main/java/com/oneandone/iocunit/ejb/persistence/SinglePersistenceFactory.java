package com.oneandone.iocunit.ejb.persistence;

import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;

import com.oneandone.iocunit.jpa.XmlAwarePersistenceFactory;

/**
 * Convenience class for tests using only one persistence context in tests named: "testdb".
 *
 * @author aschoerk
 */
public abstract class SinglePersistenceFactory extends XmlAwarePersistenceFactory {

    @Override
    public String getPersistenceUnitName() {
        return "testdb";
    }

    /**
     * need to override this to produce EntityManagers from this PersistenceFactory otherwise it won't be accepted as
     * Producer.
     *
     *      &#64;Produces
     *      public abstract EntityManager newEm() {
     *           produceEntityManager()
     *      }
     *
     * @return an EntityManager injectable; (normally a delegate which propagates it's methods to the correct
     * entitymanager of the thread and transaction.
     */
    @Produces
    public abstract EntityManager newEm();
}
