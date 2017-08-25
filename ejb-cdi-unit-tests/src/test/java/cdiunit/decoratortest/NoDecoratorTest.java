package cdiunit.decoratortest;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.ejbcdiunit.EjbUnitRunner;

/**
 * Created by pcasaes on 30/03/17.
 */
@RunWith(EjbUnitRunner.class)
@AdditionalClasses({
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
