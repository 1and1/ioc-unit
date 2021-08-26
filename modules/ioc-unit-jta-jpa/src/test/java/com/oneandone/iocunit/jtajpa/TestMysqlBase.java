package com.oneandone.iocunit.jtajpa;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.MariaDBContainer;

import com.arjuna.ats.arjuna.coordinator.TxControl;
import com.oneandone.iocunit.jtajpa.helpers.Q1;
import com.oneandone.iocunit.jtajpa.helpers.Q2;
import com.oneandone.iocunit.jtajpa.helpers.TestEntity;
import com.oneandone.iocunit.jtajpa.helpers.TestEntityH2;

/**
 * @author aschoerk
 */

abstract class TestMysqlBase {

    @Q1
    @Inject
    EntityManager entityManagerQ1;
    @Q2
    @Inject
    EntityManager entityManagerQ2;

    @PersistenceContext(unitName = "test")
    EntityManager entityManagerH2;

    @Inject
    UserTransaction userTransaction;
    private TestContainer container;

    {
        TxControl.setDefaultTimeout(1000000);
    }

    @Before
    public void beforeTestJtaJpa() {
        container = new TestContainer(new MariaDBContainer());
        container.start();
    }

    @Test
    public void testStartingWithoutTransaction() throws HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {
        TestEntity o1 = entityManagerQ1.find(TestEntity.class, 0L);
        userTransaction.begin();
        TestEntity o = new TestEntity();
        entityManagerQ1.persist(o);
        userTransaction.commit();
        TestEntity o2 = entityManagerQ1.find(TestEntity.class, o.getId());
        Assert.assertTrue(o.getId() == o2.getId());
        // entityManager.close();
        userTransaction.begin();
        TestEntity o3 = new TestEntity();
        entityManagerQ1.persist(o3);
        userTransaction.commit();
    }

    @Test
    public void testH2() throws HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {
        userTransaction.begin();
        TestEntityH2 oh2 = new TestEntityH2();
        oh2.setEntityName("testentityh2");
        entityManagerH2.persist(oh2);
        Assert.assertNotNull(entityManagerH2.find(TestEntityH2.class, oh2.getId()));
        userTransaction.commit();
        Assert.assertNotNull(entityManagerH2.find(TestEntityH2.class, oh2.getId()));
    }

    @Test
    public void testStartingWithTransaction() throws HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {
        userTransaction.begin();
        TestEntity o1 = entityManagerQ1.find(TestEntity.class, 0L);
        TestEntity o = new TestEntity();
        entityManagerQ1.persist(o);
        userTransaction.commit();
        TestEntity o2 = entityManagerQ1.find(TestEntity.class, o.getId());
        Assert.assertTrue(o.getId() == o2.getId());
        // entityManager.close();
        userTransaction.begin();
        TestEntity o3 = new TestEntity();
        entityManagerQ1.persist(o3);
        userTransaction.commit();
    }

    @Test
    public void testWithThreeConnections() throws Exception {
        userTransaction.begin();
        TestEntity oq1 = new TestEntity();
        oq1.setEntityName("testentityq1");
        entityManagerQ1.persist(oq1);
        TestEntity oq2 = new TestEntity();
        oq2.setEntityName("testentityq2");
        entityManagerQ2.persist(oq2);
        TestEntityH2 oh2 = new TestEntityH2();
        oh2.setEntityName("testentityh2");
        entityManagerH2.persist(oh2);
        Assert.assertNotNull(entityManagerQ1.find(TestEntity.class, oq1.getId()));
        Assert.assertNotNull(entityManagerQ2.find(TestEntity.class, oq2.getId()));
        Assert.assertNotNull(entityManagerH2.find(TestEntityH2.class, oh2.getId()));
        // Assert.assertNull(testEntityQ2);
        // Assert.assertNull(entityManagerQ1.find(TestEntity.class, oq2.getId()));
        userTransaction.commit();
        Assert.assertNotNull(entityManagerH2.find(TestEntityH2.class, oh2.getId()));
        Assert.assertNull(entityManagerH2.find(TestEntity.class, oq1.getId()));
        Assert.assertNull(entityManagerH2.find(TestEntity.class, oq2.getId()));
        Assert.assertNotNull(entityManagerQ2.find(TestEntity.class, oq1.getId()));
        Assert.assertNull(entityManagerQ2.find(TestEntityH2.class, oh2.getId()));
        Assert.assertNotNull(entityManagerQ1.find(TestEntity.class, oq2.getId()));
        Assert.assertNull(entityManagerQ1.find(TestEntityH2.class, oh2.getId()));
    }
}
