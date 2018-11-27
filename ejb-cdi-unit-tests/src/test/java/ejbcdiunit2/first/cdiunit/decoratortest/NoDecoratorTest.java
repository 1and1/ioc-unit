package ejbcdiunit2.first.cdiunit.decoratortest;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.ejbcdiunit2.runner.EjbCdiUnit2Runner;

/**
 * Created by pcasaes on 30/03/17.
 */
@RunWith(EjbCdiUnit2Runner.class)
@TestClasses({
        DecoratedImpl.class,
})
public class NoDecoratorTest {
    @Inject
    private DecoratedInterface decorated;

    @Test
    public void testZero() {
        assertEquals(0, decorated.calculate());
    }
}
