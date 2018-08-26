package com.oneandone.ejbcdiunit5.persistencefactory;

import com.oneandone.ejbcdiunit5.JUnit5Extension;
import com.oneandone.ejbcdiunit5.helpers.J2eeSimTest1Factory;
import com.oneandone.ejbcdiunit5.helpers.TestResources;
import org.jglue.cdiunit.ActivatedAlternatives;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@ActivatedAlternatives({ TestResources.class })
@AdditionalClasses({ J2eeSimTest1Factory.class })
public class PFMySqlTest extends PersistenceFactoryTestBase {

}
