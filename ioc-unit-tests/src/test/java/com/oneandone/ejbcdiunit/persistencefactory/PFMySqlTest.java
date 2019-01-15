package com.oneandone.ejbcdiunit.persistencefactory;


import org.junit.runner.RunWith;

import com.oneandone.iocunit.analyzer.annotations.EnabledAlternatives;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.ejbcdiunit.helpers.J2eeSimTest1Factory;
import com.oneandone.ejbcdiunit.helpers.TestResources;

/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@EnabledAlternatives({ TestResources.class })
@TestClasses({ J2eeSimTest1Factory.class })
public class PFMySqlTest extends PersistenceFactoryTestBase {

}
