package iocunit.ejbresource;

import javax.inject.Inject;
import javax.persistence.EntityManager;

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

    @Test
    public void canWorkNativeInParallelWith3PersistenceContexts()  {
        sutClass.workNative();
    }

    @Test
    public void canWorkInParallelWith3PersistenceContextsAndFindsCorrectEntities()  {
        sutClass.workWithEntities();
    }
}
