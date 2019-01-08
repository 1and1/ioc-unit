package com.oneandone.cdi.mockstester;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.MalformedURLException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.resources.spi.ScheduledExecutorServiceFactory;
import org.junit.jupiter.api.Test;

import com.oneandone.cdi.testanalyzer.annotations.EnabledAlternatives;
import com.oneandone.cdi.testanalyzer.annotations.SutClasses;

/**
 * @author aschoerk
 */
public class AlternativeTest extends TestsBase {

    @Alternative
    static class InnerAlternative extends DummyBean {

    }

    static class InjectingAlternativeIn2 {
        @Inject
        DummyBean dummyBean;
    }

    @SutClasses(InjectingAlternativeIn2.class)
    static public class BeanUsingAlternative2 {
        @PostConstruct
        public void check() {
            assertNotNull(injectingAlternative);
        }
        @Inject
        InjectingAlternativeIn2 injectingAlternative;

        public InjectingAlternativeIn2 getInjectingAlternative() {
            return injectingAlternative;
        }
    }

    @Test
    public void canInjectAlternativeClassByInitialConfig() throws MalformedURLException {
        testClass(BeanUsingAlternative2.class);
        // InjectingAlternative is available as static inner class of testclass
        // and necessary. InnerAlternative is necessary to be enabled so that dummyBean can
        // be filled.
        enabledAlternatives(InnerAlternative.class);
        configureAndStart();
        assertNotNull(selectGet(BeanUsingAlternative2.class).getInjectingAlternative());
        assertNotNull(selectGet(InjectingAlternativeIn2.class).dummyBean);
        assertNotNull(selectGet(InnerAlternative.class));
        assertEquals(selectGet(ConfigCreatorTest.DummyBean.class).getClass(), InnerAlternative.class);
    }

    @Test
    public void canInjectAlternativeClass2NotEnabled() throws MalformedURLException {
        testClass(BeanUsingAlternative2.class);
        initialClasses(DummyBean.class);
        configureAndStart();
        assertNotNull(selectGet(BeanUsingAlternative2.class).getInjectingAlternative());
        assertNotNull(selectGet(InjectingAlternativeIn2.class).dummyBean);
        assertNotNull(selectGet(DummyBean.class));
        assertEquals(selectGet(DummyBean.class).getClass(), ConfigCreatorTest.DummyBean.class);
    }



}
