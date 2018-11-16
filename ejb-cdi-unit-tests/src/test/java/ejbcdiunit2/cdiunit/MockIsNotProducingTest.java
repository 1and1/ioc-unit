package ejbcdiunit2.cdiunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;
import javax.inject.Provider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.ejbcdiunit.EjbUnitRunner;

import cdiunit.AImplementation1;
import cdiunit.AInterface;


/**
 * @author aschoerk
 */
@RunWith(EjbUnitRunner.class)
@TestClasses({ AImplementation1.class })
public class MockIsNotProducingTest {
    @Mock
    private AInterface mockA;

    @Inject
    private Provider<AInterface> a;

    /**
     * Test that we can use the test alternative annotation to specify that a mock is used
     */
    @Test
    public void testTestAlternative() {
        AInterface a1 = a.get();
        assertEquals(a1.getClass(), AImplementation1.class);
        assertNotNull(mockA);
    }

}
