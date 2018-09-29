package com.oneandone.ejbcdiunit.cfganalyzer;

import java.lang.reflect.Method;

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
public class CdiUnitAnalyzer extends TestConfigAnalyzer {

    @Override
    protected void init(Class<?> testClass, CdiTestConfig config) {
        super.init(testClass, config);
    }

    @Override
    protected void initContainerSpecific(Class<?> testClass, Method testMethod) {

        testConfig.getExtensions().add(createMetadata(new TestScopeExtension(testClass), TestScopeExtension.class.getName()));
        if (testMethod != null) {
            testConfig.getExtensions().add(createMetadata(new ProducerConfigExtension(testMethod), ProducerConfigExtension.class.getName()));
        }

        try {
            Class.forName("javax.faces.view.ViewScoped");
            testConfig.getExtensions().add(createMetadata(new EjbUnitViewScopeExtension(), EjbUnitViewScopeExtension.class.getName()));
        } catch (ClassNotFoundException e) {

        }

        try {
            Class.forName("javax.servlet.http.HttpServletRequest");
            getClassesToProcess().add(EjbCdiUnitInitialListenerProducer.class);
            getClassesToProcess().add(InRequestInterceptorEjbCdiUnit.class);
            getClassesToProcess().add(InSessionInterceptorEjbCdiUnit.class);
            getClassesToProcess().add(InConversationInterceptor.class);
            getClassesToProcess().add(MockServletContextImpl.class);
            getClassesToProcess().add(MockHttpSessionImpl.class);
            getClassesToProcess().add(MockHttpServletRequestImpl.class);
            getClassesToProcess().add(MockHttpServletResponseImpl.class);


            // If this is an old version of weld then add the producers
            try {
                Class.forName("org.jboss.weld.bean.AbstractSyntheticBean");
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                getClassesToProcess().add(ServletObjectsProducerEjbCdiUnit.class);
            }

        } catch (ClassNotFoundException e) {}

        // Add Interceptors here, to make sure the sequence is handled right
        getClassesToProcess().add(AsynchronousMethodInterceptor.class);
        if (weldVersion.charAt(0) - '2' >= 1) {
            getClassesToProcess().add(SimulatedUserTransaction.class);
        }
        getClassesToProcess().add(TransactionalInterceptor.class);
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
