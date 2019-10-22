package com.oneandone.ejbcdiunit5.ejb;

import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.ejb.XmlLessPersistenceFactory;
import com.oneandone.iocunit.ejb.trainterceptors.EjbTransactional;
import com.oneandone.iocunitejb.entities.TestEntity1;

/**
 * @author aschoerk
 */
@ExtendWith(IocJUnit5Extension.class)
@SutClasses({ XmlLessPersistenceFactory.class, TestEntity1.class })
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

    @Test
    public void testRequired2() {
        Assertions.assertThrows(TransactionRequiredException.class, () -> testClassNoTraManagement.call());
    }

    @Test
    public void testRequired3() {
        Assertions.assertThrows(TransactionRequiredException.class, () -> testClassNoEjbTransactional.call());
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
