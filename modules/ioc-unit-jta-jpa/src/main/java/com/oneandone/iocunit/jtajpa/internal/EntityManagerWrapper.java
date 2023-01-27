package com.oneandone.iocunit.jtajpa.internal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.inject.spi.PassivationCapable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.Transaction;
import jakarta.transaction.TransactionScoped;

import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionImple;

/**
 * @author aschoerk
 */
@TransactionScoped
public class EntityManagerWrapper implements Serializable, PassivationCapable {
    private static final long serialVersionUID = -7441007325030843990L;
    private Map<String, EntityManager> entityManagers = new HashMap<>();
    private Transaction transaction;

    public EntityManagerWrapper() {
        transaction = TransactionImple.getTransaction();
    }

    public EntityManager getEntityManager(EntityManagerFactory factory, String puName) {
        EntityManager result = entityManagers.get(puName);
        if(result == null) {
            result = factory.createEntityManager();
            entityManagers.put(puName, result);
        }
        return result;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    @PreDestroy
    public void clrEntityManagers() {
        try {
            entityManagers
                    .entrySet()
                    .stream().map(e -> e.getValue())
                    .filter(e -> e != null && e.isOpen())
                    .forEach(e -> {
                        e.clear();
                        e.close();
                    });
        } finally {
            entityManagers.clear();
        }
        this.transaction = null;
    }

    @Override
    public String getId() {
        return getClass().getName() + "_" + hashCode();
    }
}
