package cdiunit5;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.ejbcdiunit5.JUnit5Extension;

@ExtendWith(JUnit5Extension.class)
@AdditionalClasses(AImplementation3.StereotypeAlternative.class)
public class TestAlternativeStereotype {
    @Inject
    private AImplementation1 impl1;

    @Inject
    private AImplementation3 impl3;

    @Inject
    private AInterface impl;

    @Test
    public void testAlternativeSelected() {

        Assertions.assertTrue(impl instanceof AImplementation3, "Should have been impl3");
    }


}
