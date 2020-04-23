package iocunit.cdiunit.decoratortest;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.oneandone.iocunit.IocUnitRunner;
import com.oneandone.iocunit.analyzer.annotations.TestClasses;

/**
 * Created by pcasaes on 30/03/17.
 */
@RunWith(IocUnitRunner.class)
@TestClasses({
        DecoratedImpl.class,
        AddOneDecorator.class
})
public class AddOneDecoratorTest {

    @Inject
    private DecoratedInterface decorated;

    @Test
    public void testAddOne() {
        assertEquals(1, decorated.calculate());
    }
}
