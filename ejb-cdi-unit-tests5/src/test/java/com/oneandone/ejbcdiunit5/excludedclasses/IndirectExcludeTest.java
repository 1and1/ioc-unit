package com.oneandone.ejbcdiunit5.excludedclasses;

import com.oneandone.ejbcdiunit5.JUnit5Extension;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author aschoerk
 */
@ExtendWith(JUnit5Extension.class)
@AdditionalClasses({ IndirectExcluding.class })
public class IndirectExcludeTest {

    @Test
    public void test() {
        Assertions.assertThrows(RuntimeException.class,() -> { throw new RuntimeException("test should not start"); } );
    }
}
