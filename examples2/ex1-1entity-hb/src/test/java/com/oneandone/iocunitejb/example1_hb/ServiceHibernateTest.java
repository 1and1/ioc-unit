package com.oneandone.iocunitejb.example1_hb;

import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jpa.HibernatePersistenceFactory;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses({ HibernatePersistenceFactory.class})
public class ServiceHibernateTest extends TestBase {
}
