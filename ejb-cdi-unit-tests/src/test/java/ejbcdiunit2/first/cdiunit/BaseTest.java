package ejbcdiunit2.first.cdiunit;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.weld.environment.se.WeldSEBeanRegistrant;

import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.cdiunit.internal.servlet.MockHttpServletRequestImpl;
import com.oneandone.cdiunit.internal.servlet.MockHttpServletResponseImpl;
import com.oneandone.cdiunit.internal.servlet.MockHttpSessionImpl;
import com.oneandone.cdiunit.internal.servlet.MockServletContextImpl;
import com.oneandone.ejbcdiunit.ContextControllerEjbCdiUnit;
import com.oneandone.ejbcdiunit.cdiunit.internal.InConversationInterceptor;
import com.oneandone.ejbcdiunit.internal.EjbCdiUnitInitialListenerProducer;
import com.oneandone.ejbcdiunit.internal.InRequestInterceptorEjbCdiUnit;
import com.oneandone.ejbcdiunit.internal.InSessionInterceptorEjbCdiUnit;
import com.oneandone.ejbcdiunit.internal.jsf.EjbUnitViewScopeExtension;

@TestClasses({ ESupportClass.class, ScopedFactory.class,
        // added in cdiunit
        EjbUnitViewScopeExtension.class,
        ContextControllerEjbCdiUnit.class,
        InRequestInterceptorEjbCdiUnit.class,
        InSessionInterceptorEjbCdiUnit.class,
        InConversationInterceptor.class,
        WeldSEBeanRegistrant.class,
        // ProducerConfigExtension.class,
        MockServletContextImpl.class,
        MockHttpSessionImpl.class,
        MockHttpServletRequestImpl.class,
        MockHttpServletResponseImpl.class,
        EjbCdiUnitInitialListenerProducer.class
})
public class BaseTest {
    @Inject
    private BeanManager beanManager;

    public BeanManager getBeanManager() {
        return beanManager;
    }
}
