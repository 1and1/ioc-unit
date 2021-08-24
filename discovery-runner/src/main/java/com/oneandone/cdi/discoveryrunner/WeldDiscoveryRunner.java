package com.oneandone.cdi.discoveryrunner;

import static java.util.Arrays.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

import com.oneandone.cdi.discoveryrunner.annotations.EnabledAlternatives;
import com.oneandone.cdi.discoveryrunner.annotations.ExcludedClasses;
import com.oneandone.cdi.discoveryrunner.annotations.TestClasses;
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
        prepareWeldInfo(clazz, weldInfo);
    }

    public static void prepareWeldInfo(Class<?> clazzP, WeldInfo weldInfoP) {
        Collection<Class<?>> toScan = Arrays.asList(clazzP);
        List<Class<?>> didScan = new ArrayList<>();
        // weldInfoP.toAdd.add(clazzP);
        while (toScan.size() > 0) {
            Set<Class<?>> nextToScan = new HashSet<>();
            toScan.forEach(c -> {
                AnnotationSupport.findRepeatableAnnotations(c, ExcludedClasses.class)
                        .stream()
                        .flatMap(ann -> stream(ann.value()))
                        .forEach(weldInfoP.toExclude::add);
                AnnotationSupport.findRepeatableAnnotations(c, EnabledAlternatives.class)
                        .stream()
                        .flatMap(ann -> stream(ann.value()))
                        .peek(nextToScan::add)
                        .forEach(weldInfoP.alternatives::add);
                AnnotationSupport.findRepeatableAnnotations(c, TestClasses.class)
                        .stream()
                        .flatMap(ann -> stream(ann.value()))
                        .peek(nextToScan::add)
                        .forEach(weldInfoP.toAdd::add);
            });
            didScan.addAll(toScan);
            nextToScan.removeAll(didScan);
            toScan = nextToScan;
        }
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
                .beanClasses(weldInfo.toAdd.toArray(new Class<?>[weldInfo.toAdd.size()]))
                .alternatives(weldInfo.alternatives.toArray(new Class<?>[weldInfo.alternatives.size()]))
                .addExtension(new ExcludedBeansExtension(weldInfo.toExclude));
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


    public static class WeldInfo {
        Set<Class<?>> toExclude = new HashSet<>();
        Set<Class<?>> alternatives = new HashSet<>();
        Set<Class<?>> toAdd = new HashSet<>();
    }

}
