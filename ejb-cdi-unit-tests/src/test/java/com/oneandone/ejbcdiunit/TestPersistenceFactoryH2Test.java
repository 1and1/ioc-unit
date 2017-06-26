package com.oneandone.ejbcdiunit;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.persistence.TestPersistenceFactory;

/**
 * @author aschoerk
 */
@RunWith(EjbUnitRunner.class)
@AdditionalClasses({ TestPersistenceFactory.class })
public class TestPersistenceFactoryH2Test extends PersistenceFactoryTestBase {



}
