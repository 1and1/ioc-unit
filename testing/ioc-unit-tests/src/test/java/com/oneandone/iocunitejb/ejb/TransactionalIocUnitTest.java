package com.oneandone.iocunitejb.ejb;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.SutClasspaths;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jpa.XmlLessPersistenceFactory;
import com.oneandone.iocunitejb.TestRunnerIocUnit;
import com.oneandone.iocunitejb.cdibeans.AppScopedServiceBean;
import com.oneandone.iocunitejb.entities.TestEntity1;
import com.oneandone.iocunitejb.helpers.LoggerGenerator;
import com.oneandone.iocunitejb.transactional.TransactionalTestBase;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses({XmlLessPersistenceFactory.class})
@SutClasspaths({AppScopedServiceBean.class})
@SutClasses({LoggerGenerator.class, TestRunnerIocUnit.class, TestEntity1.class})
public class TransactionalIocUnitTest extends TransactionalTestBase {
    @Test
    @Override
    public void canJustInsert() throws Exception {
        super.canJustInsert();
    }

    @Test
    @Override
    public void canInsertInspiteOfRE() throws Exception {
        super.canInsertInspiteOfRE();
    }

    @Test
    @Override
    public void canInsertInspiteOfDerivedRE() throws Exception {
        super.canInsertInspiteOfDerivedRE();
    }

    @Test
    @Override
    public void canInsertInspiteOfRollbackOn() throws Exception {
        super.canInsertInspiteOfRollbackOn();
    }

    @Test
    @Override
    public void canInsertInspiteOfRollbackOnWithDerived() throws Exception {
        super.canInsertInspiteOfRollbackOnWithDerived();
    }

    @Test
    @Override
    public void canNotInsertWithRollbackon() throws Exception {
        super.canNotInsertWithRollbackon();
    }

    @Test
    @Override
    public void canNotInsertWithRollbackonDerived() throws Exception {
        super.canNotInsertWithRollbackonDerived();
    }

    @Test
    @Override
    public void doesNotInsertIfSetRollbackOnly() throws Exception {
        super.doesNotInsertIfSetRollbackOnly();
    }

}


