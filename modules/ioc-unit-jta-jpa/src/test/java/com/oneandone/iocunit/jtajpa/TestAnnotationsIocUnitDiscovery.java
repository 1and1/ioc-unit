package com.oneandone.iocunit.jtajpa;

import javax.transaction.Transactional;

import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jtajpa.beans.MainBean;
import com.oneandone.iocunit.jtajpa.beans.ReqNewBean;
import com.oneandone.iocunit.jtajpa.helpers.TestEntity;
import com.oneandone.iocunit.jtajpa.internal.EntityManagerFactoryFactory;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@SutClasses({MainBean.class, ReqNewBean.class, TestEntity.class})
@TestClasses({EntityManagerFactoryFactory.class})
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class TestAnnotationsIocUnitDiscovery extends TestAnnotationsBase {
}
