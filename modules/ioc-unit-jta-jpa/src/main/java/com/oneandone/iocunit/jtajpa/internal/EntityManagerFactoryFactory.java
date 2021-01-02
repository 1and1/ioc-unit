package com.oneandone.iocunit.jtajpa.internal;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionScoped;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arjuna.ats.arjuna.StateManager;
import com.arjuna.ats.arjuna.coordinator.TxControl;
import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionImple;
import com.oneandone.cdi.weldstarter.CreationalContexts;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class EntityManagerFactoryFactory {
    static Logger logger = LoggerFactory.getLogger(EntityManagerFactoryFactory.class);
    public static ThreadLocal<String> currentPuName = new ThreadLocal<>();
    static ThreadLocal<EntityManagerFactory> currentFactory = new ThreadLocal<>();
    Map<String, EntityManagerFactory> factories = new ConcurrentHashMap<>();

    @Inject
    UserTransaction userTransaction;

    CreationalContexts creationalContexts;

    {
        TxControl.setDefaultTimeout(1200);  // after 20 Minutes end transaction, Debugging should be possible
    }
    
    Map<String, EntityManager> traLess = new ConcurrentHashMap<>();
    
    EntityManager getTraLessEM(String puName) {
        long threadId = Thread.currentThread().getId();
        String key = threadId + "__" + puName;
        EntityManager res = traLess.get(key);
        if (res == null || !res.isOpen()) {
            res = getEntityManager(puName, false).getEntityManager();
            traLess.put(key, res);
        }
        return res;
    }

    @PreDestroy
    public void preDestroy() {
        try {
            if ( userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION)
                userTransaction.rollback();
        } catch (SystemException e) {
            logger.error("Disposing UserTransaction in preDestroy delivered",e);
        }
        currentPuName.set(null);
        currentFactory.set(null);
        for (EntityManager em: traLess.values()) {
            em.close();
        }
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
            return new EntityManagerFactoryFactory.EntityManagerWrapper(entityManagerFactory.createEntityManager(),
                    TransactionImple.getTransaction());
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
        private Transaction transaction;

        public EntityManagerWrapper(final EntityManager entityManager) {
            this.entityManager = entityManager;
        }
        public EntityManagerWrapper(final EntityManager entityManager, Transaction transaction) {
            this.entityManager = entityManager;
            this.transaction = transaction;
        }

        public EntityManager getEntityManager() {
            return entityManager;
        }

        public Transaction getTransaction() {
            return transaction;
        }

        public void clrEntityManager() {
            this.entityManager = null; this.transaction = null;
        }
    }
}
