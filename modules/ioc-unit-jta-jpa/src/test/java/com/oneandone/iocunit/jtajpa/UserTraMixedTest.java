package com.oneandone.iocunit.jtajpa;

import jakarta.inject.Inject;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;

@RunWith(IocUnitRunner.class)
public class UserTraMixedTest {

    @Inject
    private UserTraMixedBean userTraMixedBean;


    @Test
    public void canUseUserTransactionInNotSupported() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTraMixedBean.canUseUserTransactionInNotSupported();
    }

    @Test
    public void canUseUserTransactionInNotSupportedSupports() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTraMixedBean.canUseUserTransactionInNotSupportedSupports();
    }

    @Test
    public void canUseUserTransactionInNever() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTraMixedBean.canUseUserTransactionInNever();
    }

    @Test
    public void canNotUseUserTransactionInRequired() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTraMixedBean.canNotUseUserTransactionInRequired();
    }

    @Test
    public void canNotUseUserTransactionInRequiresNew() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTraMixedBean.canNotUseUserTransactionInRequiresNew();
    }

    @Test
    public void canMixInRequiresNew() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTraMixedBean.canMixInRequiresNew();
    }

    @Test
    public void canMixInNever() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTraMixedBean.canMixInNever();
    }
}
