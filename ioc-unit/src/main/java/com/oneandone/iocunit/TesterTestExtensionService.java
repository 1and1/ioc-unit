package com.oneandone.iocunit;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.oneandone.cdi.weldstarter.spi.TestExtensionService;
import com.oneandone.iocunit.analyzer.annotations.ProducesAlternative;
import com.oneandone.iocunit.scopes.IocUnitRequestScopeInterceptor;

/**
 * @author aschoerk
 */
public class TesterTestExtensionService implements TestExtensionService {
    @Override
    public List<Class<?>> testClasses() {
        return Arrays.asList(ProducesAlternative.class, IocUnitRequestScopeInterceptor.class);
    }

    @Override
    public Collection<? extends Class<?>> excludeFromIndexScan() {
        return Arrays.asList(
                IocUnitRequestScopeInterceptor.class);
    }
}
