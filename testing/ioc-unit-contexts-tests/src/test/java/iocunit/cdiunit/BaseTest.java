package iocunit.cdiunit;

import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

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
