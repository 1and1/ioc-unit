package com.oneandone.iocunit.jta.narayana;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import com.oneandone.iocunit.jta.narayana.entities.TestEntity1;

/**
 * @author aschoerk
 */
@ApplicationScoped
@Transactional(value = Transactional.TxType.REQUIRES_NEW)
public class SutClass {
    @Inject
    EntityManager entityManager;

    public void testDefaultCall() {
        entityManager.persist(new TestEntity1());
    }

    @Transactional(value = Transactional.TxType.REQUIRED)
    public void testRequiredCall() {
        entityManager.persist(new TestEntity1());
    }
}
