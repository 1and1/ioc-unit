package com.oneandone.ejbcdiunit.cfganalyzer;

import java.io.IOException;
import java.util.Set;

import com.oneandone.cdi.testanalyzer.extensions.TestScopeExtension;
import com.oneandone.cdi.tester.ProducerConfigExtension;
import com.oneandone.cdi.tester.ProducesAlternative;
import com.oneandone.cdi.tester.contexts.internal.InConversationInterceptor;
import com.oneandone.cdi.tester.contexts.internal.InRequestInterceptor;
import com.oneandone.cdi.tester.contexts.internal.InSessionInterceptor;
import com.oneandone.cdi.tester.contexts.internal.InitialListenerProducer;
import com.oneandone.cdi.tester.contexts.internal.jsf.ViewScopeExtension;
import com.oneandone.cdi.tester.contexts.internal.servlet.ServletObjectsProducer;
import com.oneandone.cdi.tester.contexts.servlet.MockHttpServletRequestImpl;
import com.oneandone.cdi.tester.contexts.servlet.MockHttpServletResponseImpl;
import com.oneandone.cdi.tester.contexts.servlet.MockHttpSessionImpl;
import com.oneandone.cdi.tester.contexts.servlet.MockServletContextImpl;
import com.oneandone.cdi.tester.ejb.AsynchronousMethodInterceptor;
import com.oneandone.cdi.tester.ejb.TransactionalInterceptor;
import com.oneandone.cdi.tester.ejb.resourcesimulators.SimulatedUserTransaction;
import com.oneandone.cdi.tester.mocks.EasyMockExtension;
import com.oneandone.cdi.tester.mocks.MockitoExtension;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.ejbcdiunit.CdiTestConfig;

/**
 * @author aschoerk
 */
public class TestConfigInitializer {

    private final CdiTestConfig testConfig;
    private final Set<Class<?>> classesToIgnore;
    private Set<Class<?>> classesToProcess;

    public TestConfigInitializer(CdiTestConfig testConfig,
            Set<Class<?>> classesToProcess,
            Set<Class<?>> classesToIgnore) {
        this.testConfig = testConfig;
        this.classesToProcess = classesToProcess;
        this.classesToIgnore = classesToIgnore;
    }

    public void initForAnalyzer() throws IOException {
        Class<?> testClass = testConfig.getTestClass();
        testConfig.getDiscoveredClasses().add(testClass.getName());
        classesToIgnore.addAll(new MockedClassesFinder().findMockedClassesOfTest(testClass));
        classesToIgnore.addAll(testConfig.getExcludedClasses());
        classesToProcess.add(testClass);
        new ClasspathSetPopulator().invoke(testConfig.getClasspathEntries());


        testConfig.getExtensions().add(
                new TestScopeExtension(testConfig.getTestClass()));
        if (testConfig.getTestMethod() != null) {
            testConfig.getExtensions()
                    .add(new ProducerConfigExtension(testConfig.getTestMethod()));
        }

        try {
            Class.forName("javax.faces.view.ViewScoped");
            testConfig.getExtensions().add(new ViewScopeExtension());
        } catch (ClassNotFoundException e) {

        }

        try {
            Class.forName("javax.servlet.http.HttpServletRequest");
            classesToProcess.add(InitialListenerProducer.class);
            classesToProcess.add(InRequestInterceptor.class);
            classesToProcess.add(InSessionInterceptor.class);
            classesToProcess.add(InConversationInterceptor.class);
            classesToProcess.add(MockServletContextImpl.class);
            classesToProcess.add(MockHttpSessionImpl.class);
            classesToProcess.add(MockHttpServletRequestImpl.class);
            classesToProcess.add(MockHttpServletResponseImpl.class);


            // If this is an old version of weld then add the producers
            try {
                Class.forName("org.jboss.weld.bean.AbstractSyntheticBean");
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                classesToProcess.add(ServletObjectsProducer.class);
            }

        } catch (ClassNotFoundException e) {}

        // Add Interceptors here, to make sure the sequence is handled right
        classesToProcess.add(AsynchronousMethodInterceptor.class);
        if (testConfig.getWeldVersion().charAt(0) - '2' >= 1) {
            classesToProcess.add(SimulatedUserTransaction.class);
        }
        classesToProcess.add(TransactionalInterceptor.class);
        testConfig.getEnabledAlternativeStereotypes().add(
                ProducesAlternative.class.getName());

        try {
            Class.forName("org.mockito.Mock");
            testConfig.getExtensions().add(new MockitoExtension());
        } catch (ClassNotFoundException e) {

        }

        try {
            Class.forName("org.easymock.EasyMockRunner");
            testConfig.getExtensions().add(new EasyMockExtension());
        } catch (ClassNotFoundException e) {

        }

        testConfig.getExtensions().add(WeldSetupClass.getWeldStarter().createExtension("org.jboss.weld.environment.se.WeldSEBeanRegistrant"));

    }
}
