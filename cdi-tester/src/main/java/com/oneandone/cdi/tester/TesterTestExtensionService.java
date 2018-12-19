package com.oneandone.cdi.tester;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.oneandone.cdi.weldstarter.spi.TestExtensionService;

/**
 * @author aschoerk
 */
public class TesterTestExtensionService implements TestExtensionService {
    @Override
    public List<Class<?>> testClasses() {
        return Arrays.asList(ProducesAlternative.class);
    }
}
