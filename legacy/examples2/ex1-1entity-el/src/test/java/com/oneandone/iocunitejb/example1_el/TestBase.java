package com.oneandone.iocunitejb.example1_el;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutPackages;
import com.oneandone.iocunit.ejb.persistence.PersistenceFactory;
import com.oneandone.iocunit.ejb.persistence.TestClosure;
import com.oneandone.iocunit.ejb.persistence.TestTransactionException;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutPackages(Service.class)
public abstract class TestBase {
    @Inject
    ServiceIntf sut;

    @Inject
    EntityManager entityManager;

    @Test
    public void canServiceReturnFive() {
        assertThat(sut.returnFive(), is(5));
    }

    @Test
    public void canServiceInsertEntity1() {
        long id = sut.newEntity1(1, "test1");
        assertThat(id, is(1L));
        List<com.oneandone.iocunitejb.example1_el.ExampleEntity1> resultList = entityManager.createQuery("Select e from ExampleEntity1 e", com.oneandone.iocunitejb.example1_el.ExampleEntity1.class).getResultList();
        assertThat(resultList.size(), is(1));
        com.oneandone.iocunitejb.example1_el.ExampleEntity1 entity1 = resultList.iterator().next();
        assertThat(entity1.getIntValue(), is(1));
        assertThat(entity1.getStringValue(), is("test1"));
    }

    @Test
    public void canReadEntity1AfterInsertion() {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ids.add(sut.newEntity1(i, "string: " + i));
        }
        // fetch the 6th inserted entity.
        assertThat(sut.getStringValueFor(ids.get(5)), is("string: 5"));
    }

    @Inject
    UserTransaction userTransaction;

    @Test
    public void canReadTestDataUsingService() throws SystemException, NotSupportedException {
        userTransaction.begin();
        List<com.oneandone.iocunitejb.example1_el.ExampleEntity1> entities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final com.oneandone.iocunitejb.example1_el.ExampleEntity1 entity1 = new com.oneandone.iocunitejb.example1_el.ExampleEntity1(i, "string: " + i);
            entities.add(entity1);
            entityManager.persist(entity1);
        }
        assertThat(sut.getStringValueFor(entities.get(5).getId()), is("string: 5"));
        userTransaction.rollback();
    }

    @Inject
    PersistenceFactory persistenceFactory;

    @Test(expected = TestTransactionException.class)
    public void cantReadTestDataUsingServiceInRequiredNew() throws SystemException, NotSupportedException {
        userTransaction.begin();
        List<com.oneandone.iocunitejb.example1_el.ExampleEntity1> entities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final com.oneandone.iocunitejb.example1_el.ExampleEntity1 entity1 = new com.oneandone.iocunitejb.example1_el.ExampleEntity1(i, "string: " + i);
            entities.add(entity1);
            entityManager.persist(entity1);
        }
        persistenceFactory.transaction(TransactionAttributeType.REQUIRES_NEW, new TestClosure() {
            @Override
            public void execute() throws Exception {
                sut.getStringValueFor(entities.get(5).getId());
            }
        });
        userTransaction.rollback();
    }

    @Test
    public void canReadCommittedTestDataUsingServiceInRequiredNew()
            throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        userTransaction.begin();
        List<com.oneandone.iocunitejb.example1_el.ExampleEntity1> entities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final com.oneandone.iocunitejb.example1_el.ExampleEntity1 entity1 = new ExampleEntity1(i, "string: " + i);
            entities.add(entity1);
            entityManager.persist(entity1);
        }
        userTransaction.commit();
        persistenceFactory.transaction(TransactionAttributeType.REQUIRES_NEW, new TestClosure() {
            @Override
            public void execute() throws Exception {
                assertThat(sut.getStringValueFor(entities.get(5).getId()), is("string: 5"));
            }
        });

    }
}