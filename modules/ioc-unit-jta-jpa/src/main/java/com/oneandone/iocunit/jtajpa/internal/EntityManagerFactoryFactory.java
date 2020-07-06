package com.oneandone.iocunit.jtajpa.internal;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.TransactionScoped;

import com.arjuna.ats.arjuna.coordinator.TxControl;
import com.oneandone.cdi.weldstarter.CreationalContexts;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class EntityManagerFactoryFactory {
    public static ThreadLocal<String> currentPuName = new ThreadLocal<>();
    static ThreadLocal<EntityManagerFactory> currentFactory = new ThreadLocal<>();
    Map<String, EntityManagerFactory> factories = new ConcurrentHashMap<>();
    CreationalContexts creationalContexts;

    {
        TxControl.setDefaultTimeout(1200);  // after 20 Minutes end transaction, Debugging should be possible
    }

    public EntityManagerFactoryFactory() {
        try {
            creationalContexts = new CreationalContexts();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public EntityManagerFactory getCurrentFactory() {
        return currentFactory.get();
    }

    void dispose(@Disposes EntityManagerFactoryFactory.EntityManagerWrapper emWrapper) {
        emWrapper.getEntityManager().close();
        emWrapper.clrEntityManager();
    }

    @TransactionScoped
    @Produces
    EntityManagerFactoryFactory.EntityManagerWrapper transactionalEntityManagerWrapper() {
        final EntityManagerFactory entityManagerFactory = getCurrentFactory();
        if(entityManagerFactory == null) {
            throw new RuntimeException("expected Factory for pu does not exist");
        }
        else {
            return new EntityManagerFactoryFactory.EntityManagerWrapper(entityManagerFactory.createEntityManager());
        }

    }

    public EntityManagerFactory getEMFactory(final String name) {
        return factories.get(name);
    }

    public EntityManagerWrapper getEntityManager(final String persistenceUnitName, boolean transactional) {
        EntityManagerFactory factory = factories.get(persistenceUnitName);
        if(factory == null) {
            String prevPuName = currentPuName.get();
            try {
                currentPuName.set(persistenceUnitName);
                factories.put(persistenceUnitName, Persistence.createEntityManagerFactory(persistenceUnitName));
                factory = factories.get(persistenceUnitName);
            } finally {
                currentPuName.set(prevPuName);
            }
        }
        if(transactional) {
            currentFactory.set(factory);
            return ((EntityManagerWrapper)
                            creationalContexts.create(EntityManagerWrapper.class, TransactionScoped.class));
        }
        else {
            return new EntityManagerWrapper(factory.createEntityManager());
        }
    }

    public static class EntityManagerWrapper implements Serializable {
        private static final long serialVersionUID = -7441007325030843990L;
        private EntityManager entityManager;

        public EntityManagerWrapper(final EntityManager entityManager) {
            this.entityManager = entityManager;
        }

        public EntityManager getEntityManager() {
            return entityManager;
        }

        public void clrEntityManager() {
            this.entityManager = null;
        }
    }
}
