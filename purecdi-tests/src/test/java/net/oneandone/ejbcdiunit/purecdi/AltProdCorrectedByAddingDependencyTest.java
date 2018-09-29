package net.oneandone.ejbcdiunit.purecdi;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

/**
 * @author aschoerk
 */
public class AltProdCorrectedByAddingDependencyTest extends AltProdTest {

    // remove CdiHelperBean, then no creation is triggered
    @Override
    public Collection<String> getBeanClasses() {
        return Arrays.asList(CdiBean1.class.getName(),
                CdiHelperBean.class.getName(),
                DummyClass.class.getName(),
                TestResources.class.getName());
    }

    @Override
    @Test
    public void test() {
        super.test();
    }
}
