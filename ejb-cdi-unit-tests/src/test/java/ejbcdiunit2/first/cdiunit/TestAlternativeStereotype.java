package ejbcdiunit2.first.cdiunit;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.ejbcdiunit2.runner.EjbCdiUnit2Runner;

@RunWith(EjbCdiUnit2Runner.class)
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

        Assert.assertTrue("Should have been impl3", impl instanceof AImplementation3);
    }


}
