package com.oneandone.ejbcdiunit.ejbs.appexc;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.junit.Assert;
import org.junit.Test;

import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.AppExcExampleInheritedNoRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.AppExcExampleInheritedRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.AppExcExampleNotInheritedNoRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.AppExcExampleNotInheritedRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.AppRTExcExampleInheritedNoRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.AppRTExcExampleInheritedRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.AppRTExcExampleNotInheritedNoRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.AppRTExcExampleNotInheritedRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.DerivedAppExcExampleInheritedNoRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.DerivedAppExcExampleInheritedRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.DerivedAppExcExampleNotInheritedNoRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.DerivedAppExcExampleNotInheritedRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.DerivedAppRTExcExampleInheritedNoRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.DerivedAppRTExcExampleInheritedRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.DerivedAppRTExcExampleNotInheritedNoRollback;
import com.oneandone.ejbcdiunit.ejbs.appexc.exceptions.DerivedAppRTExcExampleNotInheritedRollback;

/**
 * @author aschoerk
 */
public class TestBaseClass {
    @Inject
    protected EntityManager em;

    @EJB
    protected SaveAndThrower saveAndThrower;

    @EJB
    protected SaveAndThrowCaller saveAndThrowCaller;

    @Inject
    protected UserTransaction userTransaction;

    private Long countEntities() {
        Long result;
        try {
            userTransaction.begin();
            result = em.createQuery("select count(e) from TestEntity1 e", Long.class).getSingleResult();
            userTransaction.commit();
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    protected void callAndCheckInCurrentTra(Throwable exc, Long expected) throws Throwable {
        callAndCheckInCurrentTra(exc, expected, false);
    }

    protected void callAndCheckInCurrentTra(Throwable exc, Long expected, boolean expectEjbexception) throws Throwable {
        Long orgCount = countEntities();
        userTransaction.begin();
        try {
            saveAndThrower.saveAndThrowInCurrentTra(exc);
        } catch (Throwable thw) {
            if (expectEjbexception) {
                assertThat(thw, instanceOf(EJBException.class));
                assertThat(thw.getCause(), is(exc));
            } else {
                Assert.assertThat(thw, is(exc));
            }
        }
        try {
            userTransaction.commit();
            if (expected == orgCount) {
                fail("Expected RollbackException");
            }
        } catch (RollbackException rbx) {
            if (expected > orgCount) {
                fail("expected Transaction not to fail");
            }
        } catch (AssertionError aserr) {
            throw aserr;
        } catch (Throwable thw) {
            fail("expected no expception to be catched anymore");
        }
        Assert.assertThat(countEntities(), is(expected));

    }

    @Test
    public void testAppExcInCurrentTra() throws Throwable {
        clearEntity();
        callAndCheckInCurrentTra(new AppExcExampleInheritedRollback("test"), 0L);
        callAndCheckInCurrentTra(new AppExcExampleNotInheritedRollback("test"), 0L);
        callAndCheckInCurrentTra(new AppExcExampleInheritedNoRollback("test"), 1L);
        callAndCheckInCurrentTra(new AppExcExampleNotInheritedNoRollback("test"), 2L);
        callAndCheckInCurrentTra(new DerivedAppExcExampleInheritedRollback("test"), 2L);
        callAndCheckInCurrentTra(new DerivedAppExcExampleNotInheritedRollback("test"), 3L);
        callAndCheckInCurrentTra(new DerivedAppExcExampleInheritedNoRollback("test"), 4L);
        callAndCheckInCurrentTra(new DerivedAppExcExampleNotInheritedNoRollback("test"), 5L);
    }

    @Test
    public void testAppRTExcInCurrentTra() throws Throwable {
        clearEntity();
        callAndCheckInCurrentTra(new AppRTExcExampleInheritedRollback("test"), 0L);
        callAndCheckInCurrentTra(new AppRTExcExampleNotInheritedRollback("test"), 0L);
        callAndCheckInCurrentTra(new AppRTExcExampleInheritedNoRollback("test"), 1L);
        callAndCheckInCurrentTra(new AppRTExcExampleNotInheritedNoRollback("test"), 2L);
        callAndCheckInCurrentTra(new DerivedAppRTExcExampleInheritedRollback("test"), 2L);
        callAndCheckInCurrentTra(new DerivedAppRTExcExampleNotInheritedRollback("test"), 2L, true);
        callAndCheckInCurrentTra(new DerivedAppRTExcExampleInheritedNoRollback("test"), 3L);
        callAndCheckInCurrentTra(new DerivedAppRTExcExampleNotInheritedNoRollback("test"), 3L, true);
    }

    @Test
    public void testAppExcInRequired() throws Throwable {
        clearEntity();
        saveAndThrowCaller.callInRequired(new AppExcExampleInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInRequired(new AppExcExampleNotInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInRequired(new AppExcExampleInheritedNoRollback("test"), 1L);
        saveAndThrowCaller.callInRequired(new AppExcExampleNotInheritedNoRollback("test"), 2L);
        saveAndThrowCaller.callInRequired(new DerivedAppExcExampleInheritedRollback("test"), 2L);
        saveAndThrowCaller.callInRequired(new DerivedAppExcExampleNotInheritedRollback("test"), 3L);
        saveAndThrowCaller.callInRequired(new DerivedAppExcExampleInheritedNoRollback("test"), 4L);
        saveAndThrowCaller.callInRequired(new DerivedAppExcExampleNotInheritedNoRollback("test"), 5L);
    }

    @Test
    public void testAppRTExcInRequired() throws Throwable {
        clearEntity();
        saveAndThrowCaller.callInRequired(new AppRTExcExampleInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInRequired(new AppRTExcExampleNotInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInRequired(new AppRTExcExampleInheritedNoRollback("test"), 1L);
        saveAndThrowCaller.callInRequired(new AppRTExcExampleNotInheritedNoRollback("test"), 2L);
        saveAndThrowCaller.callInRequired(new DerivedAppRTExcExampleInheritedRollback("test"), 2L);
        saveAndThrowCaller.callInRequired(new DerivedAppRTExcExampleNotInheritedRollback("test"), 2L, true);
        saveAndThrowCaller.callInRequired(new DerivedAppRTExcExampleInheritedNoRollback("test"), 3L);
        saveAndThrowCaller.callInRequired(new DerivedAppRTExcExampleNotInheritedNoRollback("test"), 3L, true);
    }

    @Test
    public void testAppExcInRequiresNew() throws Throwable {
        clearEntity();
        saveAndThrowCaller.callInRequiresNew(new AppExcExampleInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInRequiresNew(new AppExcExampleNotInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInRequiresNew(new AppExcExampleInheritedNoRollback("test"), 1L);
        saveAndThrowCaller.callInRequiresNew(new AppExcExampleNotInheritedNoRollback("test"), 2L);
        saveAndThrowCaller.callInRequiresNew(new DerivedAppExcExampleInheritedRollback("test"), 2L);
        saveAndThrowCaller.callInRequiresNew(new DerivedAppExcExampleNotInheritedRollback("test"), 3L);
        saveAndThrowCaller.callInRequiresNew(new DerivedAppExcExampleInheritedNoRollback("test"), 4L);
        saveAndThrowCaller.callInRequiresNew(new DerivedAppExcExampleNotInheritedNoRollback("test"), 5L);
    }

    @Test
    public void testAppRTExcInRequiresNew() throws Throwable {
        clearEntity();
        saveAndThrowCaller.callInRequiresNew(new AppRTExcExampleInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInRequiresNew(new AppRTExcExampleNotInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInRequiresNew(new AppRTExcExampleInheritedNoRollback("test"), 1L);
        saveAndThrowCaller.callInRequiresNew(new AppRTExcExampleNotInheritedNoRollback("test"), 2L);
        saveAndThrowCaller.callInRequiresNew(new DerivedAppRTExcExampleInheritedRollback("test"), 2L);
        saveAndThrowCaller.callInRequiresNew(new DerivedAppRTExcExampleNotInheritedRollback("test"), 2L, true);
        saveAndThrowCaller.callInRequiresNew(new DerivedAppRTExcExampleInheritedNoRollback("test"), 3L);
        saveAndThrowCaller.callInRequiresNew(new DerivedAppRTExcExampleNotInheritedNoRollback("test"), 3L, true);
    }

    @Test
    public void testAppExcInSupports() throws Throwable {
        clearEntity();
        saveAndThrowCaller.callInSupports(new AppExcExampleInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInSupports(new AppExcExampleNotInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInSupports(new AppExcExampleInheritedNoRollback("test"), 1L);
        saveAndThrowCaller.callInSupports(new AppExcExampleNotInheritedNoRollback("test"), 2L);
        saveAndThrowCaller.callInSupports(new DerivedAppExcExampleInheritedRollback("test"), 2L);
        saveAndThrowCaller.callInSupports(new DerivedAppExcExampleNotInheritedRollback("test"), 3L);
        saveAndThrowCaller.callInSupports(new DerivedAppExcExampleInheritedNoRollback("test"), 4L);
        saveAndThrowCaller.callInSupports(new DerivedAppExcExampleNotInheritedNoRollback("test"), 5L);
    }

    @Test
    public void testAppRTExcInSupports() throws Throwable {
        clearEntity();
        saveAndThrowCaller.callInSupports(new AppRTExcExampleInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInSupports(new AppRTExcExampleNotInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInSupports(new AppRTExcExampleInheritedNoRollback("test"), 1L);
        saveAndThrowCaller.callInSupports(new AppRTExcExampleNotInheritedNoRollback("test"), 2L);
        saveAndThrowCaller.callInSupports(new DerivedAppRTExcExampleInheritedRollback("test"), 2L);
        saveAndThrowCaller.callInSupports(new DerivedAppRTExcExampleNotInheritedRollback("test"), 2L, true);
        saveAndThrowCaller.callInSupports(new DerivedAppRTExcExampleInheritedNoRollback("test"), 3L);
        saveAndThrowCaller.callInSupports(new DerivedAppRTExcExampleNotInheritedNoRollback("test"), 3L, true);
    }

    @Test
    public void testAppExcInNotSupported() throws Throwable {
        clearEntity();
        saveAndThrowCaller.callInNotSupported(new AppExcExampleInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInNotSupported(new AppExcExampleNotInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInNotSupported(new AppExcExampleInheritedNoRollback("test"), 1L);
        saveAndThrowCaller.callInNotSupported(new AppExcExampleNotInheritedNoRollback("test"), 2L);
        saveAndThrowCaller.callInNotSupported(new DerivedAppExcExampleInheritedRollback("test"), 2L);
        saveAndThrowCaller.callInNotSupported(new DerivedAppExcExampleNotInheritedRollback("test"), 3L);
        saveAndThrowCaller.callInNotSupported(new DerivedAppExcExampleInheritedNoRollback("test"), 4L);
        saveAndThrowCaller.callInNotSupported(new DerivedAppExcExampleNotInheritedNoRollback("test"), 5L);
    }

    @Test
    public void testAppRTExcInNotSupported() throws Throwable {
        clearEntity();
        saveAndThrowCaller.callInNotSupported(new AppRTExcExampleInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInNotSupported(new AppRTExcExampleNotInheritedRollback("test"), 0L);
        saveAndThrowCaller.callInNotSupported(new AppRTExcExampleInheritedNoRollback("test"), 1L);
        saveAndThrowCaller.callInNotSupported(new AppRTExcExampleNotInheritedNoRollback("test"), 2L);
        saveAndThrowCaller.callInNotSupported(new DerivedAppRTExcExampleInheritedRollback("test"), 2L);
        saveAndThrowCaller.callInNotSupported(new DerivedAppRTExcExampleNotInheritedRollback("test"), 2L, true);
        saveAndThrowCaller.callInNotSupported(new DerivedAppRTExcExampleInheritedNoRollback("test"), 3L);
        saveAndThrowCaller.callInNotSupported(new DerivedAppRTExcExampleNotInheritedNoRollback("test"), 3L, true);
    }

    protected void clearEntity()
            throws NotSupportedException, SystemException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
        userTransaction.begin();
        em.createQuery("delete from TestEntity1 e").executeUpdate();
        userTransaction.commit();
    }
}
