package com.oneandone.ejbcdiunit.ejb;

import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.EjbUnitRunner;
import com.oneandone.ejbcdiunit.entities.TestEntity1;
import com.oneandone.ejbcdiunit.internal.EjbTransactional;
import com.oneandone.ejbcdiunit.persistence.TestPersistenceFactory;

/**
 * @author aschoerk
 */
@RunWith(EjbUnitRunner.class)
@AdditionalClasses({ TestPersistenceFactory.class, TestEntity1.class })
public class NonEjbTransactionTest {

    @Inject
    TestClass testClass;
    @Inject
    TestClassNoTraManagement testClassNoTraManagement;
    @Inject
    TestClassNoEjbTransactional testClassNoEjbTransactional;

    @Test
    public void testRequired() {
        testClass.call();
    }

    @Test(expected = TransactionRequiredException.class)
    public void testRequired2() {
        testClassNoTraManagement.call();
    }

    @Test(expected = TransactionRequiredException.class)
    public void testRequired3() {
        testClassNoEjbTransactional.call();
    }

    @EjbTransactional
    @TransactionManagement(TransactionManagementType.CONTAINER)
    public static class TestClass {
        @Inject
        EntityManager em;

        public void call() {
            em.persist(new TestEntity1());
        }
    }

    @EjbTransactional
    public static class TestClassNoTraManagement {
        @Inject
        EntityManager em;

        public void call() {
            em.persist(new TestEntity1());
        }
    }

    @TransactionManagement(TransactionManagementType.CONTAINER)
    public static class TestClassNoEjbTransactional {
        @Inject
        EntityManager em;

        public void call() {
            em.persist(new TestEntity1());
        }
    }

}
