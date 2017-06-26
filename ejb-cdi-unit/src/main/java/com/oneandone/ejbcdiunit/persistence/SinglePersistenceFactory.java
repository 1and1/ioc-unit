package com.oneandone.ejbcdiunit.persistence;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;

/**
 * Convenience class for tests using only one persistence context in tests named: "testdb".
 *
 * @author aschoerk
 */
public abstract class SinglePersistenceFactory extends PersistenceFactory {

    @Override
    protected String getPersistenceUnitName() {
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
