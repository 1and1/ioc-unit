package com.oneandone.iocunit.jtajpa;

import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import javax.transaction.TransactionalException;
import javax.transaction.UserTransaction;

import org.junit.jupiter.api.Test;

import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jtajpa.beans.MainBean;
import com.oneandone.iocunit.jtajpa.beans.MandatoryBean;
import com.oneandone.iocunit.jtajpa.beans.NeverBean;
import com.oneandone.iocunit.jtajpa.beans.NotSuppBean;
import com.oneandone.iocunit.jtajpa.beans.ReqNewBean;
import com.oneandone.iocunit.jtajpa.beans.RequiredBean;
import com.oneandone.iocunit.jtajpa.beans.SupportsBean;

@TestClasses({RequiredBean.class, NotSuppBean.class, MandatoryBean.class, ReqNewBean.class, SupportsBean.class,
        NeverBean.class,
        TestBeanBase.TestFactory.class})
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class UserTraMixedBean extends TestBeanBase {

    @Inject
    MainBean mainBean;
    @Inject
    UserTransaction userTransaction;

    @Test
    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public void canUseUserTransactionInNotSupported() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        writing();
        userTransaction.commit();
        reading();
        userTransaction.begin();
        writing();
        userTransaction.commit();

        reading();
    }

    @Test
    @Transactional(Transactional.TxType.SUPPORTS)
    public void canUseUserTransactionInNotSupportedSupports() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        writing();
        userTransaction.commit();
        userTransaction.begin();
        writing();
        userTransaction.commit();
    }

    @Test
    @Transactional(Transactional.TxType.NEVER)
    public void canUseUserTransactionInNever() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        writing();
        userTransaction.commit();
        reading();
        userTransaction.begin();
        writing();
        userTransaction.commit();
        reading();
    }

    @Test
    @Transactional(Transactional.TxType.REQUIRED)
    public void canNotUseUserTransactionInRequired() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        assertThrows(TransactionalException.class, () -> {
            userTransaction.begin();
            userTransaction.commit();
            userTransaction.begin();
            userTransaction.commit();
        });
    }

    @Test
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void canNotUseUserTransactionInRequiresNew() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        assertThrows(TransactionalException.class, () -> {
            userTransaction.begin();
            writing();
            userTransaction.commit();
            userTransaction.begin();
            writing();
            userTransaction.commit();
        });
    }

    @Test
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void canMixInRequiresNew() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        writing();
        notSuppBean.callReqNewBean();
        writing();
        notSuppBean.callReqNewBean();
        writing();
        notSuppBean.callRequiredBean();
        writing();
        notSuppBean.callNeverBean();
    }

    @Test
    @Transactional(Transactional.TxType.NEVER)
    public void canMixInNever() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        reading();
        notSuppBean.callReqNewBean();
        reading();
        notSuppBean.callReqNewBean();
        reading();
        notSuppBean.callRequiredBean();
        reading();
        notSuppBean.callNeverBean();
        reading();
        notSuppBean.callNotSuppBean();
    }
}
