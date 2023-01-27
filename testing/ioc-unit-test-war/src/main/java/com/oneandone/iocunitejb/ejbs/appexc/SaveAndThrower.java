package com.oneandone.iocunitejb.ejbs.appexc;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import com.oneandone.iocunitejb.entities.TestEntity1;

/**
 * @author aschoerk
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class SaveAndThrower {
    @Inject
    EntityManager em;

    public void saveAndThrowInCurrentTra(Throwable thw) throws Throwable {
        em.persist(new TestEntity1());
        throw thw;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveAndThrowInNewTra(Throwable thw) throws Throwable {
        em.persist(new TestEntity1());
        throw thw;
    }
}
