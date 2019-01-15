package cdiunit5.decoratortest;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.iocunit.analyzer.annotations.SutClasses;
import com.oneandone.cdi.tester.JUnit5Extension;

/**
 * Created by pcasaes on 30/03/17.
 */
@ExtendWith(JUnit5Extension.class)
@SutClasses({
        DecoratedImpl.class,
        AddOneDecorator.class
})
public class AddOneDecoratorTest {

    @Inject
    private DecoratedInterface decorated;

    @Test
    public void testAddOne() {
        Assertions.assertEquals(1, decorated.calculate());
    }
}
