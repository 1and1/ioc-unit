package cditester.cdiunit;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.cdi.tester.contexts.internal.jsf.ViewScopeExtension;

@TestClasses({ ScopedFactory.class,
        // added in cdiunit
        ViewScopeExtension.class
})
public class BaseTest {
    @Inject
    private BeanManager beanManager;

    public BeanManager getBeanManager() {
        return beanManager;
    }
}
