package ejbcdiunit2.first;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.oneandone.cdi.tester.CdiUnit2Runner;


/**
 * @author aschoerk
 */
@RunWith(CdiUnit2Runner.class)
// @TestClasses({ MockitoExtension.class})
@ApplicationScoped
public class MockTest {
    @Mock
    @Produces
    private Classes.I mockA;

    @Inject
    private Instance<Classes.I> a;

    /**
     * Test that we can use the test alternative annotation to specify that a mock is used
     */
    @Test
    public void testTestAlternative() {
        Classes.I a1 = a.get();
        mockA.call();
        assertNotNull(mockA);
        Mockito.verify(mockA, Mockito.times(1)).call();
        assertEquals(mockA.toString(), a1.toString());
        Mockito.verify(a1, Mockito.times(1)).call();
    }

}
