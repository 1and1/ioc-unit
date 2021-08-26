package com.oneandone.iocunit.jtajpa;

import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.transaction.Transactional;

import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jtajpa.helpers.TestEntity;
import com.oneandone.iocunit.jtajpa.internal.EntityManagerFactoryFactory;

import com.oneandone.iocunit.jtajpa.beans.MainBean;
import com.oneandone.iocunit.jtajpa.beans.ReqNewBean;

/**
 * @author aschoerk
 */
@ExtendWith(IocJUnit5Extension.class)
@SutClasses({MainBean.class, ReqNewBean.class, TestEntity.class})
@TestClasses({EntityManagerFactoryFactory.class})
@Transactional(Transactional.TxType.NOT_SUPPORTED)
public class TestJtaJpaAnnotationsIocUnitDiscovery extends TestJtaJpaAnnotationsBase {
}
