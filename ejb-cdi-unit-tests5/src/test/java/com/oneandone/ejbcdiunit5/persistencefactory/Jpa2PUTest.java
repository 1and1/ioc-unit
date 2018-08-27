package com.oneandone.ejbcdiunit5.persistencefactory;

import com.oneandone.ejbcdiunit.ClassWithTwoDifferentEntityManagers;
import com.oneandone.ejbcdiunit.cdiunit.Pu1Em;
import com.oneandone.ejbcdiunit.cdiunit.Pu2Em;
import com.oneandone.ejbcdiunit.entities.TestEntity1;
import com.oneandone.ejbcdiunit.persistence.TestTransaction;
import com.oneandone.ejbcdiunit5.JUnit5Extension;
import com.oneandone.ejbcdiunit5.helpers.J2eeSimTest1Factory;
import com.oneandone.ejbcdiunit5.helpers.J2eeSimTest2Factory;
import com.oneandone.ejbcdiunit5.helpers.TestResources;
import org.hibernate.exception.GenericJDBCException;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.transaction.*;

import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@ActivatedAlternatives({ TestResources.class })
@AdditionalClasses({ J2eeSimTest1Factory.class, J2eeSimTest2Factory.class, ClassWithTwoDifferentEntityManagers.class })
public class Jpa2PUTest {

    @Inject
    J2eeSimTest1Factory factory1;

    @Inject
    J2eeSimTest2Factory factory2;

    @Inject
    ClassWithTwoDifferentEntityManagers sut;
    @Inject
    UserTransaction userTransaction;
    private TestEntity1 testEntity1a;
    private TestEntity1 testEntity1b;

    @Produces
    @ApplicationScoped
    @Pu1Em
    EntityManager createPu1Em() {
        return factory1.produceEntityManager();
    }

    @Produces
    @ApplicationScoped
    @Pu2Em
    EntityManager createPu2Em() {
        return factory2.produceEntityManager();
    }

    Logger log = LoggerFactory.getLogger("Jpa2PUTest");

    @BeforeEach
    public void initEntities() throws Exception {
        userTransaction.begin();
        testEntity1a = new TestEntity1();
        testEntity1b = new TestEntity1();

        try (TestTransaction tra1 = factory1.transaction(TransactionAttributeType.REQUIRED);
             TestTransaction tra2 = factory2.transaction(TransactionAttributeType.REQUIRED)) {
            testEntity1a.setStringAttribute("string in entity1");
            testEntity1a.setIntAttribute(10);
            factory1.produceEntityManager().persist(testEntity1a);
            testEntity1b.setStringAttribute("string in entity2");
            testEntity1b.setIntAttribute(20);
            factory2.produceEntityManager().persist(testEntity1b);
        }

        testEntity1a.setStringAttribute(null);
        testEntity1b.setStringAttribute(null);

    }

    @AfterEach
    public void afterJpa2PUTest() throws HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException {
        userTransaction.commit();
    }

    @Test
    public void canWorkIn2Transactions() throws Exception {

        try {
            factory2.produceEntityManager().refresh(testEntity1a);
            fail("expected Illegal Argument Exception");
        } catch (IllegalArgumentException e) {
            log.info("expected IllegalArgumentException catched {}",e);
        }

        try {
            factory1.produceEntityManager().refresh(testEntity1b);
            fail("expected Illegal Argument Exception");
        } catch (IllegalArgumentException e) {
            log.info("expected IllegalArgumentException catched {}",e);
        }


        factory1.produceEntityManager().find(TestEntity1.class, testEntity1a.getId());
        factory2.produceEntityManager().find(TestEntity1.class, testEntity1b.getId());

        assertThat(testEntity1a.getIntAttribute(), is(10));
        assertThat(testEntity1b.getIntAttribute(), is(20));

    }

    @Test
    public void testEmsAreCorrectInjected() throws Exception {

        testEntity1a = sut.readEntities(true, 10); // let service read using em1
        assertThat(testEntity1a.getIntAttribute(), is(10));
        try {
            factory2.produceEntityManager().refresh(testEntity1a); // provoke error
            fail("expected Illegal Argument Exception");
        } catch (IllegalArgumentException e) { }
        assertThat(testEntity1a.getIntAttribute(), is(10));
        factory1.produceEntityManager().refresh(testEntity1a); // should be ok

        assertThat(testEntity1a.getIntAttribute(), is(10));


        testEntity1a = sut.readEntities(false, 20); // let service read using em2
        assertThat(testEntity1a.getIntAttribute(), is(20));
        try {
            factory1.produceEntityManager().refresh(testEntity1a); // provoke error
            fail("expected Illegal Argument Exception");
        } catch (IllegalArgumentException e) { }
        assertThat(testEntity1a.getIntAttribute(), is(20));
        factory2.produceEntityManager().refresh(testEntity1a); // should be ok

        assertThat(testEntity1a.getIntAttribute(), is(20));

    }

    @Test
    public void checkForUpdate() throws Exception {

        EntityManager em1 = factory1.produceEntityManager();
        EntityManager em2 = factory2.produceEntityManager();
        TestEntity1 entity1;
        try (TestTransaction resource = factory2.transaction(REQUIRES_NEW)) {
            entity1 = new TestEntity1();
            entity1.setIntAttribute(10);
            em2.persist(entity1);
        }

        try (TestTransaction resource1 = factory1.transaction(REQUIRES_NEW);
                TestTransaction resource2 = factory2.transaction(REQUIRES_NEW)) {
            TestEntity1 res1 =
                    em1.createQuery("select e from TestEntity1 e where e.id = :id", TestEntity1.class)
                            .setParameter("id", entity1.getId())
                            .setLockMode(LockModeType.PESSIMISTIC_WRITE).getSingleResult();
            try {
                TestEntity1 res2 =
                        em2.createQuery("select e from TestEntity1 e where e.id = :id", TestEntity1.class)
                                .setParameter("id", entity1.getId())
                                .setLockMode(LockModeType.PESSIMISTIC_WRITE).getSingleResult();
                fail("expected PersistenceException because of two updates");
            } catch (PersistenceException e) {

                assert (e.getCause().getClass().equals(GenericJDBCException.class));
            }
        }
    }


}
