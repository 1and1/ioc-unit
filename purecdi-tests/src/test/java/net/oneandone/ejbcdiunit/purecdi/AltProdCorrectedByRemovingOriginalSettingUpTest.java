package net.oneandone.ejbcdiunit.purecdi;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author aschoerk
 */
public class AltProdCorrectedByRemovingOriginalSettingUpTest extends AltProdSettingUpTest {

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
