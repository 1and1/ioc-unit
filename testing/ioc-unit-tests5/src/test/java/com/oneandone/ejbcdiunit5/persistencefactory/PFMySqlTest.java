package com.oneandone.ejbcdiunit5.persistencefactory;

import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.analyzer.annotations.EnabledAlternatives;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;
import com.oneandone.iocunit.IocJUnit5Extension;
import com.oneandone.ejbcdiunit5.helpers.J2eeSimTest1Factory;
import com.oneandone.ejbcdiunit5.helpers.TestResources;

/**
 * @author aschoerk
 */
@ExtendWith(IocJUnit5Extension.class)
@EnabledAlternatives({ TestResources.class })
@TestClasses({ J2eeSimTest1Factory.class })
public class PFMySqlTest extends PersistenceFactoryTestBase {

}
