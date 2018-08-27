package com.oneandone.ejbcdiunit5.excludedclasses;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.ejbcdiunit5.ExpectedStartupException;
import com.oneandone.ejbcdiunit5.JUnit5Extension;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@AdditionalClasses({ IndirectExcluding.class })
public class IndirectExcludeTest {

    @Test
    @ExpectedStartupException(RuntimeException.class)
    public void test() {
        throw new RuntimeException("should be hidden by StartupException");
    }
}
