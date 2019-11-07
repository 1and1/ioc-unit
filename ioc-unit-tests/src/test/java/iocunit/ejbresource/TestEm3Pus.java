package iocunit.ejbresource;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutPackagesDeep;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;

import iocunit.ejbresource.em.Entity3;
import iocunit.ejbresource.em.PUQual1;
import iocunit.ejbresource.em.PUQual2;
import iocunit.ejbresource.em.SutClass;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses(EmTestResources.class)
@SutPackagesDeep(Entity3.class)
public class TestEm3Pus {
    @Inject
    SutClass sutClass;

    @PUQual1
    @Inject
    EntityManager em1;

    @PUQual2
    @Inject
    EntityManager em2;

    @Inject
    EntityManager em3;

    @Inject
    UserTransaction userTransaction;

    @Before
    public void before() throws HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException, NotSupportedException {
        userTransaction.begin();
        em1.createNativeQuery("drop all objects").executeUpdate();
        em2.createNativeQuery("drop all objects").executeUpdate();
        em3.createNativeQuery("drop all objects").executeUpdate();
        userTransaction.commit();
    }

    @Test
    public void canWorkNativeInParallelWith3PersistenceContexts()  {
        sutClass.workNative();
    }

    @Test
    public void canWorkInParallelWith3PersistenceContextsAndFindsCorrectEntities()  {
        sutClass.workWithEntities();
    }
}
