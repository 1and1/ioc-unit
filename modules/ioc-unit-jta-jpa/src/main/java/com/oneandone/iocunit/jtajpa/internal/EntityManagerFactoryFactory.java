package com.oneandone.iocunit.jtajpa.internal;

import java.io.Serializable;
import java.util.HashMap;
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

import com.arjuna.ats.arjuna.coordinator.TxControl;
import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionImple;
import com.oneandone.cdi.weldstarter.CreationalContexts;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class EntityManagerFactoryFactory {
    // used during EntityManagerFactory-Creation to let PersistenceXmlConnectionProvider know what the current PuName is.
    public static ThreadLocal<String> currentPuName = new ThreadLocal<>();
    public static ThreadLocal<EntityManagerWrapper> traLessEntityManagers = new ThreadLocal<>();
    static Logger logger = LoggerFactory.getLogger(EntityManagerFactoryFactory.class);
    Map<String, EntityManagerFactory> factories = new ConcurrentHashMap<>();
    CreationalContexts creationalContexts;
    Map<String, EntityManager> traLess = new ConcurrentHashMap<>();
    @Inject
    private UserTransaction userTransaction;

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

    EntityManager getTraLessEM(String puName) {
        long threadId = Thread.currentThread().getId();
        String key = threadId + "__" + puName;
        EntityManager res = traLess.get(key);
        if(res == null || !res.isOpen()) {
            res = getEntityManager(puName, false);
            traLess.put(key, res);
        }
        return res;
    }

    @PreDestroy
    public void preDestroy() {
        try {
            if(userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                userTransaction.rollback();
            }
        } catch (SystemException e) {
            logger.error("Disposing UserTransaction in preDestroy delivered", e);
        }
        currentPuName.set(null);
        for (EntityManager em : traLess.values()) {
            em.close();
        }
    }

    void dispose(@Disposes EntityManagerFactoryFactory.EntityManagerWrapper emWrapper) {
        emWrapper.clrEntityManagers();
    }

    @TransactionScoped
    @Produces
    EntityManagerFactoryFactory.EntityManagerWrapper transactionalEntityManagerWrapper() {
        return new EntityManagerWrapper();
    }

    public EntityManager getEntityManager(final String persistenceUnitName, boolean transactional) {
        EntityManagerFactory factory = factories.get(persistenceUnitName);
        if(factory == null) {
            String prevPuName = currentPuName.get();
            try {
                currentPuName.set(persistenceUnitName);
                // when JPA uses PersistenceXmlConnectionProvider to create EntityManagerFactory
                // fetch the persistenceUnitName from ThreadLocal, to know which PersistenceUnit to use.
                factories.put(persistenceUnitName, Persistence.createEntityManagerFactory(persistenceUnitName));
                factory = factories.get(persistenceUnitName);
            } finally {
                currentPuName.set(prevPuName);
            }
        }
        EntityManagerWrapper entityManagerWrapper = traLessEntityManagers.get();
        if(transactional) {
            if(entityManagerWrapper != null) {
                entityManagerWrapper.clrEntityManagers();
                traLessEntityManagers.set(null);
            }
            EntityManagerWrapper ewrapper = ((EntityManagerWrapper)
                                                     creationalContexts.create(EntityManagerWrapper.class, TransactionScoped.class));
            return ewrapper.getEntityManager(factory);
        }
        else {
            if(entityManagerWrapper == null) {
                entityManagerWrapper = new EntityManagerWrapper();
                traLessEntityManagers.set(entityManagerWrapper);
            }

            return entityManagerWrapper.getEntityManager(factory);
        }
    }

    public static class EntityManagerWrapper implements Serializable {
        private static final long serialVersionUID = -7441007325030843990L;
        private Map<EntityManagerFactory, EntityManager> entityManagers = new HashMap<>();
        private Transaction transaction;

        public EntityManagerWrapper() {
            TransactionImple.getTransaction();
        }

        public EntityManager getEntityManager(EntityManagerFactory factory) {
            EntityManager result = entityManagers.get(factory);
            if(result == null) {
                result = factory.createEntityManager();
                entityManagers.put(factory, result);
            }
            return result;
        }

        public Transaction getTransaction() {
            return transaction;
        }

        public void clrEntityManagers() {
            entityManagers.entrySet().forEach(e -> e.getValue().close());
            entityManagers.clear();
            this.transaction = null;
        }
    }
}
