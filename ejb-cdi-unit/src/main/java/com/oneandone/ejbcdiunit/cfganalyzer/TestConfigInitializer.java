package com.oneandone.ejbcdiunit.cfganalyzer;

import static com.oneandone.ejbcdiunit.cfganalyzer.CdiMetaDataCreator.createMetadata;

import java.io.IOException;
import java.util.Set;

import org.jboss.weld.environment.se.WeldSEBeanRegistrant;
import org.jglue.cdiunit.ProducesAlternative;

import com.oneandone.cdiunit.internal.easymock.EasyMockExtension;
import com.oneandone.cdiunit.internal.mockito.MockitoExtension;
import com.oneandone.cdiunit.internal.servlet.MockHttpServletRequestImpl;
import com.oneandone.cdiunit.internal.servlet.MockHttpServletResponseImpl;
import com.oneandone.cdiunit.internal.servlet.MockHttpSessionImpl;
import com.oneandone.cdiunit.internal.servlet.MockServletContextImpl;
import com.oneandone.ejbcdiunit.CdiTestConfig;
import com.oneandone.ejbcdiunit.cdiunit.internal.InConversationInterceptor;
import com.oneandone.ejbcdiunit.cdiunit.internal.TestScopeExtension;
import com.oneandone.ejbcdiunit.internal.AsynchronousMethodInterceptor;
import com.oneandone.ejbcdiunit.internal.EjbCdiUnitInitialListenerProducer;
import com.oneandone.ejbcdiunit.internal.InRequestInterceptorEjbCdiUnit;
import com.oneandone.ejbcdiunit.internal.InSessionInterceptorEjbCdiUnit;
import com.oneandone.ejbcdiunit.internal.ProducerConfigExtension;
import com.oneandone.ejbcdiunit.internal.TransactionalInterceptor;
import com.oneandone.ejbcdiunit.internal.jsf.EjbUnitViewScopeExtension;
import com.oneandone.ejbcdiunit.internal.servlet.ServletObjectsProducerEjbCdiUnit;
import com.oneandone.ejbcdiunit.resourcesimulators.SimulatedUserTransaction;

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

    protected void initForAnalyzer() throws IOException {
        Class<?> testClass = testConfig.getTestClass();
        testConfig.getDiscoveredClasses().add(testClass.getName());
        classesToIgnore.addAll(new MockedClassesFinder().findMockedClassesOfTest(testClass));
        classesToIgnore.addAll(testConfig.getExcludedClasses());
        classesToProcess.add(testClass);
        new ClasspathSetPopulator().invoke(testConfig.getClasspathEntries());


        testConfig.getExtensions().add(createMetadata(new TestScopeExtension(testConfig.getTestClass()), TestScopeExtension.class.getName()));
        if (testConfig.getTestMethod() != null) {
            testConfig.getExtensions()
                    .add(createMetadata(new ProducerConfigExtension(testConfig.getTestMethod()), ProducerConfigExtension.class.getName()));
        }

        try {
            Class.forName("javax.faces.view.ViewScoped");
            testConfig.getExtensions().add(createMetadata(new EjbUnitViewScopeExtension(), EjbUnitViewScopeExtension.class.getName()));
        } catch (ClassNotFoundException e) {

        }

        try {
            Class.forName("javax.servlet.http.HttpServletRequest");
            classesToProcess.add(EjbCdiUnitInitialListenerProducer.class);
            classesToProcess.add(InRequestInterceptorEjbCdiUnit.class);
            classesToProcess.add(InSessionInterceptorEjbCdiUnit.class);
            classesToProcess.add(InConversationInterceptor.class);
            classesToProcess.add(MockServletContextImpl.class);
            classesToProcess.add(MockHttpSessionImpl.class);
            classesToProcess.add(MockHttpServletRequestImpl.class);
            classesToProcess.add(MockHttpServletResponseImpl.class);


            // If this is an old version of weld then add the producers
            try {
                Class.forName("org.jboss.weld.bean.AbstractSyntheticBean");
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                classesToProcess.add(ServletObjectsProducerEjbCdiUnit.class);
            }

        } catch (ClassNotFoundException e) {}

        // Add Interceptors here, to make sure the sequence is handled right
        classesToProcess.add(AsynchronousMethodInterceptor.class);
        if (testConfig.weldVersion.charAt(0) - '2' >= 1) {
            classesToProcess.add(SimulatedUserTransaction.class);
        }
        classesToProcess.add(TransactionalInterceptor.class);
        testConfig.getEnabledAlternativeStereotypes().add(
                createMetadata(ProducesAlternative.class.getName(), ProducesAlternative.class.getName()));
        try {
            Class.forName("org.mockito.Mock");
            testConfig.getExtensions().add(createMetadata(new MockitoExtension(), MockitoExtension.class.getName()));
        } catch (ClassNotFoundException e) {

        }

        try {
            Class.forName("org.easymock.EasyMockRunner");
            testConfig.getExtensions().add(createMetadata(new EasyMockExtension(), EasyMockExtension.class.getName()));
        } catch (ClassNotFoundException e) {

        }

        testConfig.getExtensions().add(createMetadata(new WeldSEBeanRegistrant(), WeldSEBeanRegistrant.class.getName()));

    }
}
