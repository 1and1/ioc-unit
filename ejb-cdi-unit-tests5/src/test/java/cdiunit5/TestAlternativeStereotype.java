package cdiunit5;

import com.oneandone.ejbcdiunit5.JUnit5Extension;
import junit.framework.Assert;
import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

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

        Assert.assertTrue("Should have been impl3", impl instanceof AImplementation3);
    }


}
