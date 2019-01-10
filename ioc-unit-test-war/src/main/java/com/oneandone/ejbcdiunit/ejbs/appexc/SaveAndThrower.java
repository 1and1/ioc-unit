package com.oneandone.ejbcdiunit.ejbs.appexc;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.oneandone.ejbcdiunit.entities.TestEntity1;

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
