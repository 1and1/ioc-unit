package com.oneandone.ejbcdiunit.example1_5;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.ejbcdiunit.persistence.PersistenceFactory;
import com.oneandone.ejbcdiunit.persistence.TestClosure;
import com.oneandone.ejbcdiunit.persistence.TestPersistenceFactory;
import com.oneandone.ejbcdiunit.persistence.TestTransactionException;
import com.oneandone.ejbcdiunit5.JUnit5Extension;


/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@AdditionalClasses({ Service.class, TestPersistenceFactory.class })
public class ServiceTest5 {
    @Inject
    ServiceIntf sut;

    @Inject
    EntityManager entityManager;

    @BeforeEach
    void beforeEach() throws SystemException, NotSupportedException {
        userTransaction.begin();
        ArrayList<Object> entities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final com.oneandone.ejbcdiunit.example1_5.Entity1 entity1 = new com.oneandone.ejbcdiunit.example1_5.Entity1(i, "string: " + i);
            entities.add(entity1);
            entityManager.persist(entity1);
        }
        userTransaction.rollback();
    }

    @AfterEach
    void afterEach() throws SystemException {
        final int status = userTransaction.getStatus();
        if (status != Status.STATUS_NO_TRANSACTION && status != Status.STATUS_ROLLEDBACK
                && status != Status.STATUS_COMMITTED) {
            userTransaction.rollback();
        }
    }

    @Test
    public void canServiceReturnFive() {
        assertThat(sut.returnFive(), is(5));
    }

    @Nested
    class whenTryToInitEachNew {
        @Test
        public void canServiceInsertEntity1() {
            long id = sut.newEntity1(1, "test1");
            assertThat(id, is(11L)); // beforeEach already inserted 10 records
            List<com.oneandone.ejbcdiunit.example1_5.Entity1> resultList =
                    entityManager.createQuery("Select e from Entity1 e", com.oneandone.ejbcdiunit.example1_5.Entity1.class).getResultList();
            assertThat(resultList.size(), is(1));
            com.oneandone.ejbcdiunit.example1_5.Entity1 entity1 = resultList.iterator().next();
            assertThat(entity1.getIntValue(), is(1));
            assertThat(entity1.getStringValue(), is("test1"));
        }
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

    @Inject
    PersistenceFactory persistenceFactory;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    public class WhenSomeEntitiesInDbExist {
        List<com.oneandone.ejbcdiunit.example1_5.Entity1> entities;

        @BeforeEach
        void beforeEach() throws SystemException, NotSupportedException {
            userTransaction.begin();
            entities = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                final com.oneandone.ejbcdiunit.example1_5.Entity1 entity1 = new Entity1(i, "string: " + i);
                entities.add(entity1);
                entityManager.persist(entity1);
            }
        }

        @Test
        public void canReadTestDataUsingService() throws SystemException, NotSupportedException {
            assertThat(sut.getStringValueFor(entities.get(5).getId()), is("string: 5"));
            userTransaction.rollback();
        }

        @Test
        public void cantReadTestDataUsingServiceInRequiredNew() {
            assertThrows(TestTransactionException.class, () -> {
                persistenceFactory.transaction(TransactionAttributeType.REQUIRES_NEW, new TestClosure() {
                    @Override
                    public void execute() throws Exception {
                        sut.getStringValueFor(entities.get(5).getId());
                    }
                });
                userTransaction.rollback();
            });
        }

        @Test
        public void canReadCommittedTestDataUsingServiceInRequiredNew()
                throws SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {

            userTransaction.commit();
            persistenceFactory.transaction(TransactionAttributeType.REQUIRES_NEW, new TestClosure() {
                @Override
                public void execute() throws Exception {
                    assertThat(sut.getStringValueFor(entities.get(5).getId()), is("string: 5"));
                }
            });

        }
    }

}
