package com.oneandone.ejbcdiunit.persistencefactory;

import static javax.ejb.TransactionAttributeType.MANDATORY;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.TransactionRequiredException;
import javax.sql.DataSource;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.entities.TestEntity1;
import com.oneandone.ejbcdiunit.persistence.PersistenceFactory;
import com.oneandone.ejbcdiunit.persistence.TestClosure;
import com.oneandone.ejbcdiunit.persistence.TestTransaction;


/**
 * @author aschoerk
 */
public abstract class PersistenceFactoryTestBase {

    private static Logger logger = LoggerFactory.getLogger("logger");

    @Inject
    PersistenceFactory persistenceFactory;

    @Inject
    EntityManager em;

    @Inject
    UserTransaction userTransaction;

    @Inject
    DataSource dataSource;

    protected String getStringAttributeNativeName() {
        return "string_attribute";
    }

    protected String getIntAttributeNativeName() {
        return "int_attribute";
    }

    protected String getSchema() {
        return "";
    }

    @Test
    public void canInsertInMandatoryTransaction() throws Exception {
        try (TestTransaction resource1 = persistenceFactory.transaction(REQUIRED)) {
            try (TestTransaction resource2 = persistenceFactory.transaction(MANDATORY)) {
                TestEntity1 entity1 = new TestEntity1();
                em.persist(entity1);
            }
        }
        List<TestEntity1> result = em.createQuery("select te from TestEntity1 te", TestEntity1.class).getResultList();
        assertThat(result.size(), is(1));
    }

    @Test
    public void canInsertInMandatoryTransactionByClosure() throws Exception {
        persistenceFactory.transaction(REQUIRED, new TestClosure() {
            @Override
            public void execute() throws Exception {
                try (TestTransaction resource2 = persistenceFactory.transaction(MANDATORY)) {
                    TestEntity1 entity1 = new TestEntity1();
                    em.persist(entity1);
                }
            }
        });
        List<TestEntity1> result = em.createQuery("select te from TestEntity1 te", TestEntity1.class).getResultList();
        assertThat(result.size(), is(1));
    }

    @Test
    public void canInsertInEmbeddedTransaction() throws Exception {
        try (TestTransaction resource1 = persistenceFactory.transaction(REQUIRED)) {
            try (TestTransaction resource2 = persistenceFactory.transaction(REQUIRED)) {
                TestEntity1 entity1 = new TestEntity1();
                em.persist(entity1);
            }
        }
        List<TestEntity1> result = em.createQuery("select te from TestEntity1 te", TestEntity1.class).getResultList();
        assertThat(result.size(), is(1));
    }

    @Test
    public void canInsertInTransaction() throws Exception {
        try (TestTransaction resource = persistenceFactory.transaction(REQUIRED)) {
            TestEntity1 entity1 = new TestEntity1();
            em.persist(entity1);
        }
        List<TestEntity1> result = em.createQuery("select te from TestEntity1 te", TestEntity1.class).getResultList();
        assertThat(result.size(), is(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void requiresNewDoesInsertInOtherEm() throws Exception {
        TestEntity1 entity1 = new TestEntity1();
        try (TestTransaction resource = persistenceFactory.transaction(REQUIRES_NEW)) {
            entity1.setIntAttribute(10);
            em.persist(entity1);
        }
        userTransaction.begin();
        entity1.setIntAttribute(0);
        em.refresh(entity1);
        userTransaction.commit();
        // entity not managed leads to illegal argument exception
    }

    @Test(expected = PersistenceException.class)
    public void requiresNewDoesInsertInSameEm() throws Exception {
        userTransaction.begin();
        TestEntity1 entity1 = new TestEntity1();
        try (TestTransaction resource = persistenceFactory.transaction(REQUIRED)) {
            entity1.setIntAttribute(10);
            em.persist(entity1);
        }
        entity1.setIntAttribute(0);
        em.refresh(entity1); // fails because not saved yet.
        assertThat(entity1.getIntAttribute(), is(10));
        userTransaction.commit();
    }

    @Test(expected = TransactionRequiredException.class)
    public void canNotInsertInMandatoyTransaction() throws Exception {
        try (TestTransaction resource = persistenceFactory.transaction(MANDATORY)) {
            TestEntity1 entity1 = new TestEntity1();
            em.persist(entity1);
        }
        List<TestEntity1> result = em.createQuery("select te from TestEntity1 te", TestEntity1.class).getResultList();
        assertThat(result.size(), is(1));
    }

    @Test
    public void testTraRolledBack() throws Exception {
        boolean noException = false;
        try (TestTransaction resource = persistenceFactory.transaction(REQUIRED)) {
            boolean seenRollback = false;

            try (TestTransaction resource2 = persistenceFactory.transaction(REQUIRES_NEW)) {
                TestEntity1 entity1 = new TestEntity1();
                entity1.setIntAttribute(10);
                em.persist(entity1);
                entity1.setIntAttribute(20);
                resource2.setRollbackOnly();
            }
            noException = true;

            List<TestEntity1> result = em.createQuery("select te from TestEntity1 te", TestEntity1.class).getResultList();
            assertThat(result.size(), is(0));
        }
        assertThat(noException, is(true));
    }

    @Test
    public void doesCommitChangeEvenWhenAlreadyFlushed() throws Exception {
        try (TestTransaction resource = persistenceFactory.transaction(REQUIRED)) {

            try (TestTransaction resource2 = persistenceFactory.transaction(REQUIRES_NEW)) {
                TestEntity1 entity1 = new TestEntity1();
                entity1.setIntAttribute(10);
                em.persist(entity1);
                em.flush();
                logger.info("test");
                entity1.setIntAttribute(20);
            }
            List<TestEntity1> result =
                    em.createQuery("select te from TestEntity1 te", TestEntity1.class).getResultList();
            assertThat(result.size(), is(1));
            assertThat(result.get(0).getIntAttribute(), is(20)); //

        }
    }

    @Test
    public void doesCommitChangeEvenWhenASecondTimeFlushed() throws Exception {
        try (TestTransaction resource = persistenceFactory.transaction(REQUIRED)) {

            try (TestTransaction resource2 = persistenceFactory.transaction(REQUIRES_NEW)) {
                TestEntity1 entity1 = new TestEntity1();
                entity1.setIntAttribute(10);
                em.persist(entity1);
                em.flush();
                logger.info("test");
                entity1.setIntAttribute(20);
                em.flush();
                entity1.setIntAttribute(30);
            }
            List<TestEntity1> result =
                    em.createQuery("select te from TestEntity1 te", TestEntity1.class).getResultList();
            assertThat(result.size(), is(1));
            assertThat(result.get(0).getIntAttribute(), is(30)); //

        }
    }

    @Test
    public void canQueryPersistedEntityInTransaction() throws Exception {
        try (TestTransaction resource = persistenceFactory.transaction(REQUIRED)) {
            TestEntity1 entity1 = new TestEntity1();
            entity1.setIntAttribute(10);
            em.persist(entity1);
            List<TestEntity1> result =
                    em.createQuery("select te from TestEntity1 te", TestEntity1.class).getResultList();
            assertThat(result.size(), is(1));
            assertThat(result.get(0).getIntAttribute(), is(10)); //
            assertThat(result.get(0), is(entity1)); // Object-Identity is preserved
        }
    }


    @Test
    public void canQueryPersistedChangedEntityInTransaction() throws Exception {
        try (TestTransaction resource = persistenceFactory.transaction(REQUIRED)) {
            TestEntity1 entity1 = new TestEntity1();
            entity1.setIntAttribute(10);
            em.persist(entity1);
            entity1.setIntAttribute(20);
            List<TestEntity1> result =
                    em.createQuery("select te from TestEntity1 te", TestEntity1.class).getResultList();
            assertThat(result.size(), is(1));
            assertThat(result.get(0).getIntAttribute(), is(20)); // got dirty Object back
        }
    }

    @Test
    public void canQueryCreatedEntityInEmbeddedTransaction() throws Exception {
        try (TestTransaction resource = persistenceFactory.transaction(REQUIRED)) {
            TestEntity1 entity1 = new TestEntity1();
            entity1.setIntAttribute(10);
            em.persist(entity1);
            List<TestEntity1> entities1 =
                    em.createQuery("select te from TestEntity1 te", TestEntity1.class).getResultList();
            assertThat(entities1.size(), is(1));
            assertThat(entities1.get(0), is(entity1));
            entity1.setIntAttribute(20);
            try (TestTransaction resource2 = persistenceFactory.transaction(REQUIRES_NEW)) {
                List<TestEntity1> entities2 =
                        em.createQuery("select te from TestEntity1 te", TestEntity1.class).getResultList();
                assertThat(entities2.size(), is(0));
            }
            em.refresh(entity1);
            assertThat(entity1.getIntAttribute(), is(10)); // refresh overwrote value, no flush happened
        }
    }

    @Test
    public void doesFlushBeforeQuery() throws Exception {
        try (TestTransaction resource = persistenceFactory.transaction(REQUIRED)) {
            TestEntity1 entity1 = new TestEntity1();
            entity1.setIntAttribute(10);
            em.persist(entity1);
            entity1.setIntAttribute(20);
            List<TestEntity1> entities1 =
                    em.createQuery("select te from TestEntity1 te", TestEntity1.class).getResultList();
            assertThat(entities1.size(), is(1));
            assertThat(entities1.get(0), is(entity1));
            em.refresh(entity1);
            assertThat(entity1.getIntAttribute(), is(20)); // new value, because of flush before query
        }
    }

    @Test
    public void doesFlushBeforeNativeQuery() throws Exception {
        try (TestTransaction resource = persistenceFactory.transaction(REQUIRED)) {
            TestEntity1 entity1 = new TestEntity1();
            entity1.setIntAttribute(10);
            em.persist(entity1);
            entity1.setIntAttribute(20);
            List entities1 =
                    em.createNativeQuery("select te.* from test_entity_1 te", TestEntity1.class).getResultList();
            assertThat(entities1.size(), is(1));
            assertThat(entities1.get(0), is(entity1));
            em.refresh(entity1);
            assertThat(entity1.getIntAttribute(), is(20)); // new value, because of flush before query
        }
    }

    @Test
    public void checkForUpdate() throws Exception {

        EntityManager em1 = persistenceFactory.produceEntityManager();
        EntityManager em2 = persistenceFactory.produceEntityManager();
        TestEntity1 entity1;
        try (TestTransaction resource = persistenceFactory.transaction(REQUIRED)) {
            entity1 = new TestEntity1();
            entity1.setIntAttribute(10);
            em.persist(entity1);
        }

        try (TestTransaction resource = persistenceFactory.transaction(REQUIRED)) {
            TestEntity1 res1 =
                    em1.createQuery("select e from TestEntity1 e where e.id = :id", TestEntity1.class)
                            .setParameter("id", entity1.getId())
                            .setLockMode(LockModeType.PESSIMISTIC_WRITE).getSingleResult();
            TestEntity1 res2 =
                    em2.createQuery("select e from TestEntity1 e where e.id = :id", TestEntity1.class)
                            .setParameter("id", entity1.getId())
                            .setLockMode(LockModeType.PESSIMISTIC_WRITE).getSingleResult();
            assertThat(res1.getId(), is(res2.getId()));
        }
    }

    @Test
    public void checkForUpdateMultiThreaded() throws Exception {

        TestEntity1 entity1;
        try (TestTransaction resource = persistenceFactory.transaction(REQUIRED)) {
            entity1 = new TestEntity1();
            entity1.setIntAttribute(10);
            em.persist(entity1);
        }

        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        List<Callable<Object>> tasks = new ArrayList<>();
        List<Future<Object>> futures = new ArrayList<>();

        Set<Integer> intAttributes = new HashSet<>();

        for (int i = 0; i < 3; i++) {
            Callable<Object> callable = new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    TestEntity1 resOrg =
                            em.createQuery("select e from TestEntity1 e where e.id = :id", TestEntity1.class)
                                    .setParameter("id", entity1.getId()).getSingleResult();
                    logger.info("Got testentity1 before Transaction not for update id: {} int: {}", resOrg.getId(), resOrg.getIntAttribute());
                    try (TestTransaction resource = persistenceFactory.transaction(REQUIRED)) {
                        TestEntity1 res1 =
                                em.createQuery("select e from TestEntity1 e where e.id = :id", TestEntity1.class)
                                        .setParameter("id", entity1.getId())
                                        .setLockMode(LockModeType.PESSIMISTIC_WRITE).getSingleResult();
                        res1.setIntAttribute(res1.getIntAttribute() + 1);
                        assertFalse(intAttributes.contains(res1.getIntAttribute()));
                        intAttributes.add(res1.getIntAttribute());
                        logger.info("Got and changed testentity1 for update id: {} int: {}", res1.getId(), res1.getIntAttribute());

                        return res1;
                    } catch (Throwable e) {
                        logger.info("Exception", e);
                        return null;
                    }
                }
            };

            futures.add(threadPool.submit(callable));
        }

        for (Future<Object> future : futures) {
            while (!future.isDone()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new IllegalStateException("task interrupted", e);
                }
            }
        }
        assertThat(intAttributes, contains(entity1.getIntAttribute() + 1, entity1.getIntAttribute() + 2, entity1.getIntAttribute() + 3));
        logger.info("Shutdown threadpool");
        threadPool.shutdown();
    }


    @Test
    public void checkUserTransactionAndDataSource() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException, SQLException {
        try (Connection dummyconn = dataSource.getConnection()) {
            userTransaction.begin();
            userTransaction.rollback();
            userTransaction.begin();
            TestEntity1 testEntity1 = new TestEntity1();
            testEntity1.setIntAttribute(111);
            testEntity1.setStringAttribute("string");
            em.persist(testEntity1);
            userTransaction.commit();
            userTransaction.begin();
            testEntity1 = new TestEntity1();
            testEntity1.setIntAttribute(112);
            testEntity1.setStringAttribute("string");
            em.persist(testEntity1);
            em.flush();
            Long res = em.createQuery("select count(e) from TestEntity1 e", Long.class).getSingleResult();
            Assert.assertThat(res, is(2L));
            // userTransaction.rollback();
            // userTransaction.begin();
            res = em.createQuery("select count(e) from TestEntity1 e", Long.class).getSingleResult();
            Assert.assertThat(res, is(2L));

            try (Connection conn = dataSource.getConnection()) {

                try (PreparedStatement stmt = conn.prepareStatement("insert into " + getSchema() +
                        "test_entity_1 (id," +
                        getStringAttributeNativeName() + ", " + getIntAttributeNativeName() +
                        ") values (111,'sss', 114)")) {
                    Assert.assertThat(stmt.executeUpdate(), is(1));
                }
            }
            res = em.createQuery("select count(e) from TestEntity1 e", Long.class).getSingleResult();
            Assert.assertThat(res, is(3L));
        }

    }



}
