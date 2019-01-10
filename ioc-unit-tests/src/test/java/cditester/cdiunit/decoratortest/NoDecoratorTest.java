package cditester.cdiunit.decoratortest;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.cdi.tester.CdiUnit2Runner;

/**
 * Created by pcasaes on 30/03/17.
 */
@RunWith(CdiUnit2Runner.class)
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
