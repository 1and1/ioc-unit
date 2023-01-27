package com.oneandone.iocunit.jtajpa.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.PassivationCapable;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.transaction.Status;
import jakarta.transaction.SystemException;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.UserTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arjuna.ats.arjuna.coordinator.TxControl;
import com.oneandone.cdi.weldstarter.CreationalContexts;

/**
 * @author aschoerk
 */
@ApplicationScoped
public class EntityManagerFactoryFactory implements PassivationCapable {
    // used during EntityManagerFactory-Creation to let PersistenceXmlConnectionProvider know what the current PuName is.
    public static ThreadLocal<String> currentPuName = new ThreadLocal<>();
    public static ThreadLocal<EntityManagerWrapper> traLessEntityManagers = new ThreadLocal<>();
    static Logger logger = LoggerFactory.getLogger(EntityManagerFactoryFactory.class);
    Map<String, EntityManagerFactory> factories = new ConcurrentHashMap<>();

    @Inject
    UserTransaction userTransaction;

    @Inject
    BeanManager beanManager;

    CreationalContexts creationalContexts;

    {
        TxControl.setDefaultTimeout(1200);  // after 20 Minutes end transaction, Debugging should be possible
    }

    EntityManager getTraLessEM(String puName) {
        return getEntityManager(puName, false);
    }

    @PostConstruct
    public void postConstruct() {
        creationalContexts = new CreationalContexts(beanManager);
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
    }

    /*
    void dispose(@Disposes EntityManagerWrapper emWrapper) {
        emWrapper.clrEntityManagers();
    }

    @TransactionScoped
    @Produces
    EntityManagerWrapper transactionalEntityManagerWrapper() {
        return new EntityManagerWrapper();
    }

     */

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
            return ewrapper.getEntityManager(factory, persistenceUnitName);
        }
        else {
            if(entityManagerWrapper == null) {
                entityManagerWrapper = new EntityManagerWrapper();
                traLessEntityManagers.set(entityManagerWrapper);
            }

            return entityManagerWrapper.getEntityManager(factory, persistenceUnitName);
        }
    }

    @Override
    public String getId() {
        return getClass().getName() + "_" + this.hashCode();
    }

}
