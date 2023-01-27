package com.oneandone.iocunitejb.ejbs.appexc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.ejb.EJBException;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

import com.oneandone.iocunitejb.ejbs.appexc.exceptions.AppExcExampleInheritedNoRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.AppExcExampleInheritedRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.AppExcExampleNotInheritedNoRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.AppExcExampleNotInheritedRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.AppRTExcExampleInheritedNoRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.AppRTExcExampleInheritedRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.AppRTExcExampleNotInheritedNoRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.AppRTExcExampleNotInheritedRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.DerivedAppExcExampleInheritedNoRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.DerivedAppExcExampleInheritedRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.DerivedAppExcExampleNotInheritedNoRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.DerivedAppExcExampleNotInheritedRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.DerivedAppRTExcExampleInheritedNoRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.DerivedAppRTExcExampleInheritedRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.DerivedAppRTExcExampleNotInheritedNoRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.DerivedAppRTExcExampleNotInheritedRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.declared.notrtex.DeclaredAppExcExampleInheritedNoRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.declared.notrtex.DeclaredAppExcExampleInheritedRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.declared.notrtex.DeclaredAppExcExampleInheritedRollbackDefault;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.declared.notrtex.DeclaredAppExcExampleNotInheritedNoRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.declared.notrtex.DeclaredAppExcExampleNotInheritedRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.declared.notrtex.DeclaredAppExcExampleNotInheritedRollbackDefault;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.declared.rtex.DeclaredAppRtExcExampleInheritedNoRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.declared.rtex.DeclaredAppRtExcExampleInheritedRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.declared.rtex.DeclaredAppRtExcExampleInheritedRollbackDefault;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.declared.rtex.DeclaredAppRtExcExampleNotInheritedNoRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.declared.rtex.DeclaredAppRtExcExampleNotInheritedRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.declared.rtex.DeclaredAppRtExcExampleNotInheritedRollbackDefault;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.declared.rtex.DerivedFromDeclaredAppRtExcExampleInheritedNoRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.declared.rtex.DerivedFromDeclaredAppRtExcExampleInheritedRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.declared.rtex.DerivedFromDeclaredAppRtExcExampleInheritedRollbackDefault;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.declared.rtex.DerivedFromDeclaredAppRtExcExampleNotInheritedNoRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.declared.rtex.DerivedFromDeclaredAppRtExcExampleNotInheritedRollback;
import com.oneandone.iocunitejb.ejbs.appexc.exceptions.declared.rtex.DerivedFromDeclaredAppRtExcExampleNotInheritedRollbackDefault;

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

    @Resource
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
                assertThat(thw, is(exc));
            }
        }
        try {
            userTransaction.commit();
            if (expected == orgCount) {
                throw new RuntimeException("Expected RollbackException");
            }
        } catch (RollbackException rbx) {
            if (expected > orgCount) {
                throw new RuntimeException("expected Transaction not to fail");
            }
        } catch (AssertionError aserr) {
            throw aserr;
        } catch (Throwable thw) {
            throw new RuntimeException("expected no expception to be catched anymore");
        }
        assertThat(countEntities(), is(expected));

    }
    
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

    
    public void testDeclaredAppExcInCurrentTra() throws Throwable {
        clearEntity();
        callAndCheckInCurrentTra(new DeclaredAppExcExampleInheritedRollback("test"), 0L);
        callAndCheckInCurrentTra(new DeclaredAppExcExampleNotInheritedRollback("test"), 0L);
        callAndCheckInCurrentTra(new DeclaredAppExcExampleInheritedNoRollback("test"), 1L);
        callAndCheckInCurrentTra(new DeclaredAppExcExampleNotInheritedNoRollback("test"), 2L);
        callAndCheckInCurrentTra(new DeclaredAppExcExampleInheritedRollbackDefault("test"), 3L);
        callAndCheckInCurrentTra(new DeclaredAppExcExampleNotInheritedRollbackDefault("test"), 4L);
    }

    
    public void testDeclaredAppRtExcInCurrentTra() throws Throwable {
        clearEntity();
        callAndCheckInCurrentTra(new DeclaredAppRtExcExampleInheritedRollback("test"), 0L);
        callAndCheckInCurrentTra(new DeclaredAppRtExcExampleNotInheritedRollback("test"), 0L);
        callAndCheckInCurrentTra(new DeclaredAppRtExcExampleInheritedNoRollback("test"), 1L);
        callAndCheckInCurrentTra(new DeclaredAppRtExcExampleNotInheritedNoRollback("test"), 2L);
        callAndCheckInCurrentTra(new DeclaredAppRtExcExampleInheritedRollbackDefault("test"), 3L);
        callAndCheckInCurrentTra(new DeclaredAppRtExcExampleNotInheritedRollbackDefault("test"), 4L);
        callAndCheckInCurrentTra(new DerivedFromDeclaredAppRtExcExampleInheritedRollback("test"), 4L);
        callAndCheckInCurrentTra(new DerivedFromDeclaredAppRtExcExampleNotInheritedRollback("test"), 4L, true);
        callAndCheckInCurrentTra(new DerivedFromDeclaredAppRtExcExampleInheritedNoRollback("test"), 5L);
        callAndCheckInCurrentTra(new DerivedFromDeclaredAppRtExcExampleNotInheritedNoRollback("test"), 5L, true);
        callAndCheckInCurrentTra(new DerivedFromDeclaredAppRtExcExampleInheritedRollbackDefault("test"), 6L);
        callAndCheckInCurrentTra(new DerivedFromDeclaredAppRtExcExampleNotInheritedRollbackDefault("test"), 6L, true);
    }

    
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
