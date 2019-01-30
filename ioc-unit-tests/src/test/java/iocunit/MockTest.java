package iocunit;

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

import com.oneandone.iocunit.IocUnitRunner;


/**
 * @author aschoerk
 */
@RunWith(IocUnitRunner.class)
@ApplicationScoped
public class MockTest {
    @Mock
    @Produces
    private Classes.I mockA;

    @Inject
    private Instance<Classes.I> a;

    Classes.I getMockA() {
        return mockA;
    }

    /**
     * Test that we can use the test alternative annotation to specify that a mock is used
     */
    @Test
    public void testTestAlternative() {
        Classes.I a1 = a.get();
        getMockA().call();
        assertNotNull(getMockA());
        Mockito.verify(getMockA(), Mockito.times(1)).call();
        assertEquals(getMockA().toString(), a1.toString());
        Mockito.verify(a1, Mockito.times(1)).call();
    }

}
