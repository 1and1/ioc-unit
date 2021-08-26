package com.oneandone.iocunit.jtajpa;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.cdi.discoveryrunner.WeldDiscoveryRunner;

@RunWith(WeldDiscoveryRunner.class)
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
