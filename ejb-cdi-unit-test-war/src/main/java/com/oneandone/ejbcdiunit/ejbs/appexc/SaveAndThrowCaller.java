package com.oneandone.ejbcdiunit.ejbs.appexc;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.junit.Assert;

/**
 * @author aschoerk
 */
@Singleton
@Startup
public class SaveAndThrowCaller {

    @EJB
    SaveAndThrower saveAndThrower;

    @Inject
    EntityManager em;

    private Long countEntities() {
        return em.createQuery("select count(e) from TestEntity1 e", Long.class).getSingleResult();
    }

    private void callAndCheck(Throwable exc, Long expected) throws Throwable {
        callAndCheck(exc, expected, false);
    }

    private void callAndCheck(Throwable exc, Long expected, boolean expectEjbexception) throws Throwable {
        Long orgCount = countEntities();
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
        if (expected != orgCount) {
            Assert.assertThat(countEntities(), is(expected));
        }
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void callInRequired(Throwable thw, Long expected, boolean expectEjbexception) throws Throwable {
        callAndCheck(thw, expected, expectEjbexception);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void callInRequired(Throwable thw, Long expected) throws Throwable {
        callAndCheck(thw, expected);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void callInRequiresNew(Throwable thw, Long expected, boolean expectEjbexception) throws Throwable {
        callAndCheck(thw, expected, expectEjbexception);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void callInRequiresNew(Throwable thw, Long expected) throws Throwable {
        callAndCheck(thw, expected);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void callInSupports(Throwable thw, Long expected, boolean expectEjbexception) throws Throwable {
        callAndCheck(thw, expected, expectEjbexception);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public void callInSupports(Throwable thw, Long expected) throws Throwable {
        callAndCheck(thw, expected);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void callInNotSupported(Throwable thw, Long expected, boolean expectEjbexception) throws Throwable {
        callAndCheck(thw, expected, expectEjbexception);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void callInNotSupported(Throwable thw, Long expected) throws Throwable {
        callAndCheck(thw, expected);
    }
}
