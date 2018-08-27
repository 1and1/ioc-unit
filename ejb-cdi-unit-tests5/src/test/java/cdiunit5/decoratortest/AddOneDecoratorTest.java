package cdiunit5.decoratortest;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.oneandone.ejbcdiunit5.JUnit5Extension;

/**
 * Created by pcasaes on 30/03/17.
 */
@ExtendWith(JUnit5Extension.class)
@AdditionalClasses({
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
