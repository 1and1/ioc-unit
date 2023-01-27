package com.oneandone.iocunitejb.ejbs;

import jakarta.annotation.Resource;
import jakarta.ejb.SessionContext;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import org.slf4j.Logger;

import com.oneandone.iocunitejb.entities.TestEntity1;

/**
 * @author aschoerk
 */

@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class TransactionalParentEJB {
    @Inject
    Logger logger;

    @Resource
    SessionContext sessionContext;

    @Inject
    EntityManager entityManager;

    public TestEntity1 saveInCurrentTransactionDefaultTraAttribute(TestEntity1 testEntity1) {
        entityManager.persist(testEntity1);
        return testEntity1;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public TestEntity1 saveInCurrentTransactionRequiredTraAttribute(TestEntity1 testEntity1) {
        entityManager.persist(testEntity1);
        return testEntity1;
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public TestEntity1 saveInCurrentTransactionNeverTraAttribute(TestEntity1 testEntity1) {
        entityManager.persist(testEntity1);
        return testEntity1;
    }
}
