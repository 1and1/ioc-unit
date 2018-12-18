package com.oneandone.cdi.tester.contexts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.tester.contexts.internal.InConversationInterceptor;
import com.oneandone.cdi.tester.contexts.internal.InRequestInterceptor;
import com.oneandone.cdi.tester.contexts.internal.InSessionInterceptor;
import com.oneandone.cdi.tester.contexts.internal.InitialListenerProducer;
import com.oneandone.cdi.tester.contexts.servlet.MockHttpServletRequestImpl;
import com.oneandone.cdi.tester.contexts.servlet.MockHttpServletResponseImpl;
import com.oneandone.cdi.tester.contexts.servlet.MockHttpSessionImpl;
import com.oneandone.cdi.tester.contexts.servlet.MockServletContextImpl;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;

/**
 * @author aschoerk
 */
public class ContextsTestExtensionService implements TestExtensionService {
    Logger log = LoggerFactory.getLogger("ContextsTestExtensionService");

    @Override
    public Collection<Class<?>> testClasses() {
        HashSet<Class<?>> result = new HashSet<Class<?>>() {
            private static final long serialVersionUID = -1661631254833065243L;

            {
                add(ContextController.class);
                add(InRequestInterceptor.class);
                add(InSessionInterceptor.class);
                add(InConversationInterceptor.class);
                // add(WeldSEBeanRegistrant.class);
                // ProducerConfigExtension.class,
                add(MockServletContextImpl.class);
                add(MockHttpSessionImpl.class);
                add(MockHttpServletRequestImpl.class);
                add(MockHttpServletResponseImpl.class);
                add(InitialListenerProducer.class);
            }
        };
        return result;
    }

    @Override
    public void preStartupAction(final WeldSetupClass weldSetup) {
        List<String> seq = new ArrayList<String>() {
            private static final long serialVersionUID = -1661631254833065243L;

            {
                add(InRequestInterceptor.class.getName());
                add(InSessionInterceptor.class.getName());
                add(InConversationInterceptor.class.getName());
            }
        };
        if (log.isDebugEnabled())
            weldSetup.getEnabledInterceptors().forEach(a -> log.debug("Interceptor: {}", a));
        weldSetup.setEnabledInterceptors(weldSetup.getEnabledInterceptors()
                .stream()
                .sorted((a, b) -> ((Integer) seq.indexOf(a.getValue())).compareTo(seq.indexOf(b.getValue())))
                .collect(Collectors.toList()));
        if (log.isDebugEnabled()) {
            log.debug("After sort of Interceptors");
            weldSetup.getEnabledInterceptors().forEach(a -> log.debug("Interceptor: {}", a));
        }
    }
}
