package com.oneandone.iocunitejb.example1;

import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.jpa.HibernatePersistenceFactory;

/**
 * @author aschoerk
 */
@TestClasses({HibernatePersistenceFactory.class})
public class ServiceHibernateTest extends TestBase {
}
