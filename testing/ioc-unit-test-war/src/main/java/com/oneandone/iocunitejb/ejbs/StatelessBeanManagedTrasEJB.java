package com.oneandone.iocunitejb.ejbs;

import jakarta.annotation.Resource;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

import org.slf4j.Logger;

import com.oneandone.iocunitejb.entities.TestEntity1;

/**
 * @author aschoerk
 */
@Stateless
@TransactionManagement(value = TransactionManagementType.BEAN)
public class StatelessBeanManagedTrasEJB {

    @Inject
    Logger logger;

    @Resource
    SessionContext sessionContext;

    private int publicInteger = 200;

    public void method1() {
        logger.info("StatelessEJB: methodCallUsingSessionContext called");
    }


    @Inject
    EntityManager entityManager;

    @Inject
    UserTransaction userTransaction;

    public TestEntity1 insertWithoutTra(TestEntity1 testEntity1) {
        logger.info("output public variable {}", publicInteger);
        entityManager.persist(testEntity1);
        entityManager.flush();
        return testEntity1;
    }

    public TestEntity1 insertNewInTra(TestEntity1 testEntity1) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        try {
            logger.info("output public variable {}", publicInteger);
            entityManager.persist(testEntity1);
            return testEntity1;
        } finally {
            userTransaction.commit();
        }
    }


}
