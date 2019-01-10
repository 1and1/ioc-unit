package com.oneandone.ejbcdiunit.ejbs;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.slf4j.Logger;

import com.oneandone.ejbcdiunit.entities.TestEntity1;

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
