package com.oneandone.ejbcdiunit.persistencefactory;

import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.EjbUnitRunner;
import com.oneandone.ejbcdiunit.helpers.J2eeSimTest1Factory;
import com.oneandone.ejbcdiunit.helpers.TestResources;

/**
 * @author aschoerk
 */
@RunWith(EjbUnitRunner.class)
@ActivatedAlternatives({ TestResources.class })
@AdditionalClasses({ J2eeSimTest1Factory.class })
public class PFMySqlTest extends PersistenceFactoryTestBase {

}
