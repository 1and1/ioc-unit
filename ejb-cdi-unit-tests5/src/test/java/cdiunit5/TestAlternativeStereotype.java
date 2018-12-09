package cdiunit5;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.cdi.tester.JUnit5Extension;

@ExtendWith(JUnit5Extension.class)
@TestClasses({ AImplementation3.class, AImplementation1.class })
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
