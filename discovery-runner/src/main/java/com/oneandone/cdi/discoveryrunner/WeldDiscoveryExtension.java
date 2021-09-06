package com.oneandone.cdi.discoveryrunner;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import java.lang.reflect.Constructor;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstanceFactory;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
import org.junit.jupiter.api.extension.TestInstantiationException;

import com.oneandone.cdi.discoveryrunner.internal.AnnotationInterpreter;
import com.oneandone.cdi.discoveryrunner.internal.WeldDiscoveryCdiExtension;
import com.oneandone.cdi.discoveryrunner.internal.WeldInfo;
import com.oneandone.cdi.discoveryrunner.naming.CdiUnitContext;

/**
 * @author aschoerk
 */
public class WeldDiscoveryExtension implements BeforeAllCallback, AfterAllCallback, TestInstanceFactory {
    Weld weld;
    WeldContainer container;
    WeldInfo weldInfo;
    private CreationalContexts creationalContexts;
    private InitialContext initialContext;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        weldInfo = new WeldInfo();
        AnnotationInterpreter.prepareWeldInfo(context.getTestClass().get(), weldInfo);
        if(determineTestLifecycle(context).equals(PER_CLASS)) {
            initWeld();
        }
    }

    @Override
    public void afterAll(final ExtensionContext extensionContext) throws Exception {
        exitWeld();
    }

    @Override
    public Object createTestInstance(final TestInstanceFactoryContext testInstanceFactoryContext, final ExtensionContext extensionContext)
            throws TestInstantiationException {
        try {
            if(!testInstanceFactoryContext.getOuterInstance().isPresent()) {
                Object test = this.createTestInstance(testInstanceFactoryContext.getTestClass());
                return test;
            }
            else {
                final Object outerInstance = testInstanceFactoryContext.getOuterInstance().get();
                Constructor<?> c = testInstanceFactoryContext.getTestClass().getDeclaredConstructor(outerInstance.getClass());
                c.setAccessible(true);
                return c.newInstance(outerInstance);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initWeld() throws NamingException {
        if(weld != null) {
            try {
                exitWeld();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        weld = new Weld()
                .addExtension(new WeldDiscoveryCdiExtension(weldInfo))
                .alternatives(weldInfo.getAlternatives().toArray(new Class[weldInfo.getAlternatives().size()]))
                .beanClasses(weldInfo.getToAdd().toArray(new Class[weldInfo.getToAdd().size()]));
        this.initialContext = new InitialContext();
        container = weld.initialize();
        final BeanManager beanManager = container.getBeanManager();
        initialContext.rebind("java:comp/BeanManager", beanManager);
        this.creationalContexts = new CreationalContexts(beanManager);

    }


    private void exitWeld() throws Exception {
        if(weld != null) {
            creationalContexts.close();
            initialContext.close();
            CdiUnitContext.init();
            weld.shutdown();
            weld = null;
            container = null;
        }
    }

    private Object createTestInstance(Class<?> clazz) throws Exception {
        System.setProperty("java.naming.factory.initial", "com.oneandone.cdi.discoveryrunner.naming.CdiTesterContextFactory");
        initWeld();

        Object test = CDI.current().select(clazz).get();
        return test;
    }


    private TestInstance.Lifecycle determineTestLifecycle(ExtensionContext ec) {
        // check the test for import org.junit.jupiter.api.TestInstance annotation
        TestInstance annotation = ec.getRequiredTestClass().getAnnotation(TestInstance.class);
        if(annotation != null) {
            return annotation.value();
        }
        else {
            return TestInstance.Lifecycle.PER_METHOD;
        }
    }
}
