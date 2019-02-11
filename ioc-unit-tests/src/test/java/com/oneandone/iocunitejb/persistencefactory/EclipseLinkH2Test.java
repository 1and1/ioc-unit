package com.oneandone.iocunitejb.persistencefactory;

import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.ejb.persistence.EclipseLinkPersistenceFactory;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses(EclipseLinkPersistenceFactory.class)
public class EclipseLinkH2Test extends PersistenceFactoryTestBase {



}
