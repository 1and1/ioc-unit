package net.oneandone.ejbcdiunit.purecdi;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

/**
 * @author aschoerk
 */
public class AltProdCorrectedByRemovingOriginalTest extends AltProdTest {

    // remove CdiHelperBean, then no creation is triggered
    @Override
    public Collection<String> getBeanClasses() {
        return Arrays.asList(CdiBean1.class.getName(),
                TestResources.class.getName());
    }

    @Override
    @Test
    public void test() {
        super.test();
    }
}
