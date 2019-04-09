package iocunit.ejbresource;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutPackagesDeep;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;

import iocunit.ejbresource.em.Entity3;
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

    @Test
    public void canWorkNativeInParallelWith3PersistenceContexts()  {
        sutClass.workNative();
    }

    @Test
    public void canWorkInParallelWith3PersistenceContextsAndFindsCorrectEntities()  {
        sutClass.workWithEntities();
    }
}
