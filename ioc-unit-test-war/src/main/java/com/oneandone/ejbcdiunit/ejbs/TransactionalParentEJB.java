package com.oneandone.ejbcdiunit.ejbs;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;

import com.oneandone.ejbcdiunit.entities.TestEntity1;

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
