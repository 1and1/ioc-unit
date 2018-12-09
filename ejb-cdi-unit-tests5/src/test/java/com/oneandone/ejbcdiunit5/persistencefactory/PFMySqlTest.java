package com.oneandone.ejbcdiunit5.persistencefactory;

import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.cdi.testanalyzer.annotations.EnabledAlternatives;
import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.cdi.tester.JUnit5Extension;
import com.oneandone.ejbcdiunit5.helpers.J2eeSimTest1Factory;
import com.oneandone.ejbcdiunit5.helpers.TestResources;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@EnabledAlternatives({ TestResources.class })
@TestClasses({ J2eeSimTest1Factory.class })
public class PFMySqlTest extends PersistenceFactoryTestBase {

}
