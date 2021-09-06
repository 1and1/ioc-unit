package com.oneandone.cdi.discoveryrunner;

import java.util.Optional;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.naming.InitialContext;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Test;
import org.junit.platform.commons.support.AnnotationSupport;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import com.oneandone.cdi.discoveryrunner.internal.AnnotationInterpreter;
import com.oneandone.cdi.discoveryrunner.internal.WeldDiscoveryCdiExtension;
import com.oneandone.cdi.discoveryrunner.internal.WeldInfo;
import com.oneandone.cdi.discoveryrunner.naming.CdiUnitContext;

/**
 * JUnit 4 Runner running the Testmethod inside a weld-container starting in discoverymode.
 */
public class WeldDiscoveryRunner extends BlockJUnit4ClassRunner {
    private final Class<?> clazz;
    private final WeldInfo weldInfo;
    private FrameworkMethod frameworkMethod;
    private Throwable startupException;
    private Weld weld;
    private WeldContainer container;

    public WeldDiscoveryRunner(final Class<?> clazz) throws InitializationError {
        super(clazz);
        this.clazz = clazz;
        weldInfo = new WeldInfo();
        AnnotationInterpreter.prepareWeldInfo(clazz, weldInfo);
    }



    @Override
    protected Statement methodBlock(final FrameworkMethod frameworkMethodP) {
        Optional<Test> annotation = AnnotationSupport.findAnnotation(frameworkMethodP.getMethod(), Test.class);
        this.frameworkMethod = frameworkMethodP;
        final Statement defaultStatement = super.methodBlock(frameworkMethodP);
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {

                if(startupException != null) {
                    if(frameworkMethodP.getAnnotation(Test.class).expected() == startupException.getClass()) {
                        return;
                    }
                    throw startupException;
                }
                System.setProperty("java.naming.factory.initial", "com.oneandone.cdi.discoveryrunner.naming.CdiTesterContextFactory");
                InitialContext initialContext = new InitialContext();
                final BeanManager beanManager = CDI.current().getBeanManager();
                initialContext.rebind("java:comp/BeanManager", beanManager);
                try (CreationalContexts creationalContexts = new CreationalContexts(beanManager)) {
                    defaultStatement.evaluate();
                } finally {
                    initialContext.close();
                    CdiUnitContext.init();
                    container.shutdown();
                }
            }
        };
    }

    @Override
    protected Object createTest() throws Exception {
        weld = new Weld()
                .beanClasses(weldInfo.getToAdd().toArray(new Class<?>[weldInfo.getToAdd().size()]))
                .alternatives(weldInfo.getAlternatives().toArray(new Class<?>[weldInfo.getAlternatives().size()]))
                .addExtension(new WeldDiscoveryCdiExtension(weldInfo));
        ;
        try {
            container = weld.initialize();
            return CDI.current().select(clazz).get();
        } catch (Throwable e) {
            if(startupException == null) {
                startupException = e;
            }
            return null;
        }
    }


}
