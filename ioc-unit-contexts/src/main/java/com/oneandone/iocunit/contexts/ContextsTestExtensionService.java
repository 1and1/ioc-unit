package com.oneandone.iocunit.contexts;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Default;

import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.context.ManagedConversation;
import org.jboss.weld.context.http.Http;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.contexts.internal.InConversationInterceptor;
import com.oneandone.iocunit.contexts.internal.InRequestInterceptor;
import com.oneandone.iocunit.contexts.internal.InSessionInterceptor;
import com.oneandone.iocunit.contexts.internal.InitialListenerProducer;
import com.oneandone.iocunit.contexts.servlet.MockHttpServletRequestImpl;
import com.oneandone.iocunit.contexts.servlet.MockHttpServletResponseImpl;
import com.oneandone.iocunit.contexts.servlet.MockHttpSessionImpl;
import com.oneandone.iocunit.contexts.servlet.MockServletContextImpl;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;

/**
 * @author aschoerk
 */
public class ContextsTestExtensionService implements TestExtensionService {
    Logger log = LoggerFactory.getLogger("ContextsTestExtensionService");

    @Http
    @Default
    public static class FakeConversationContextImpl implements ConversationContext {
        @Override
        public void invalidate() {

        }

        @Override
        public void activate(final String s) {

        }

        @Override
        public void activate() {

        }

        @Override
        public void setParameterName(final String s) {

        }

        @Override
        public String getParameterName() {
            return null;
        }

        @Override
        public void setConcurrentAccessTimeout(final long l) {

        }

        @Override
        public long getConcurrentAccessTimeout() {
            return 0;
        }

        @Override
        public void setDefaultTimeout(final long l) {

        }

        @Override
        public long getDefaultTimeout() {
            return 0;
        }

        @Override
        public Collection<ManagedConversation> getConversations() {
            return null;
        }

        @Override
        public ManagedConversation getConversation(final String s) {
            return null;
        }

        @Override
        public String generateConversationId() {
            return null;
        }

        @Override
        public ManagedConversation getCurrentConversation() {
            return null;
        }

        @Override
        public void deactivate() {

        }


        @Override
        public void destroy(final Contextual<?> contextual) {

        }

        @Override
        public Class<? extends Annotation> getScope() {
            return null;
        }

        @Override
        public <T> T get(final Contextual<T> contextual, final CreationalContext<T> creationalContext) {
            return null;
        }

        @Override
        public <T> T get(final Contextual<T> contextual) {
            return null;
        }

        @Override
        public boolean isActive() {
            return false;
        }
    }

    public static class FakeConversation implements Conversation {

        @Override
        public void begin() {

        }

        @Override
        public void begin(final String s) {

        }

        @Override
        public void end() {

        }

        @Override
        public String getId() {
            return null;
        }

        @Override
        public long getTimeout() {
            return 0;
        }

        @Override
        public void setTimeout(final long l) {

        }

        @Override
        public boolean isTransient() {
            return false;
        }
    }

    @Override
    public List<? extends Class<?>> fakeClasses() {
        ArrayList<Class<?>> result = new ArrayList<Class<?>>() {

            private static final long serialVersionUID = -1661631254833065243L;

            {
                add(FakeConversationContextImpl.class);
                add(FakeConversation.class);

            }
        };
        return result;
    }

    @Override
    public List<Class<?>> testClasses() {
        ArrayList<Class<?>> result = new ArrayList<Class<?>>() {
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
    @Override
    public Collection<? extends Class<?>> excludeFromIndexScan() {
        return Arrays.asList(ContextController.class);
    }
}
