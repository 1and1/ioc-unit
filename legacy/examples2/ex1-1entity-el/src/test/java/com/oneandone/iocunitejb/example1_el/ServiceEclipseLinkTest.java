package com.oneandone.iocunitejb.example1_el;

import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jpa.EclipseLinkPersistenceFactory;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@TestClasses({ EclipseLinkPersistenceFactory.class})
public class ServiceEclipseLinkTest extends TestBase {
}
