package ejbcdiunit2.cdiunit.bases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jglue.cdiunit.ProducesAlternative;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.ejbcdiunit2.runner.EjbCdiUnit2Runner;

import cdiunit.AImplementation1;
import cdiunit.AInterface;


/**
 * @author aschoerk
 */
@RunWith(EjbCdiUnit2Runner.class)
@TestClasses({ AImplementation1.class })
public class MockTest {
    @Mock
    @ProducesAlternative
    @Produces
    private AInterface mockA;

    @Inject
    private Instance<AInterface> a;

    /**
     * Test that we can use the test alternative annotation to specify that a mock is used
     */
    @Test
    public void testTestAlternative() {
        AInterface a1 = a.get();
        assertNotNull(mockA);
        assertEquals(mockA, a1);
    }

}
