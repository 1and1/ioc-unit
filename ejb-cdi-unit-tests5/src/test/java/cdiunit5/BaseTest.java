package cdiunit5;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.weld.environment.se.WeldSEBeanRegistrant;

import com.oneandone.cdi.testanalyzer.annotations.TestClasses;
import com.oneandone.cdi.testanalyzer.annotations.ProducesAlternative;
import com.oneandone.cdi.tester.contexts.ContextController;
import com.oneandone.cdi.tester.contexts.internal.InConversationInterceptor;
import com.oneandone.cdi.tester.contexts.internal.InRequestInterceptor;
import com.oneandone.cdi.tester.contexts.internal.InSessionInterceptor;
import com.oneandone.cdi.tester.contexts.internal.InitialListenerProducer;
import com.oneandone.cdi.tester.contexts.internal.jsf.ViewScopeExtension;
import com.oneandone.cdi.tester.contexts.servlet.MockHttpServletRequestImpl;
import com.oneandone.cdi.tester.contexts.servlet.MockHttpServletResponseImpl;
import com.oneandone.cdi.tester.contexts.servlet.MockHttpSessionImpl;
import com.oneandone.cdi.tester.contexts.servlet.MockServletContextImpl;

@TestClasses({ ScopedFactory.class
})
public class BaseTest {
    @Inject
    private BeanManager beanManager;

    public BeanManager getBeanManager() {
        return beanManager;
    }
}
