package com.oneandone.cdi.tester;

import java.util.Arrays;
import java.util.Collection;

import com.oneandone.cdi.weldstarter.spi.TestExtensionService;

/**
 * @author aschoerk
 */
public class TesterTestExtensionService implements TestExtensionService {
    @Override
    public Collection<Class<?>> testClasses() {
        return Arrays.asList(ProducesAlternative.class);
    }
}
