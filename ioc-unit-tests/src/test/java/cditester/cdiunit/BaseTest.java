package cditester.cdiunit;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import com.oneandone.iocunit.analyzer.annotations.TestClasses;

@TestClasses({ ScopedFactory.class
})
public class BaseTest {
    @Inject
    private BeanManager beanManager;

    public BeanManager getBeanManager() {
        return beanManager;
    }
}
