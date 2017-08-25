package com.oneandone.ejbcdiunit.cfganalyzer;

import java.lang.reflect.Method;

import org.jboss.weld.environment.se.WeldSEBeanRegistrant;
import org.jglue.cdiunit.ProducesAlternative;
import org.jglue.cdiunit.internal.CdiUnitInitialListener;
import org.jglue.cdiunit.internal.InConversationInterceptor;
import org.jglue.cdiunit.internal.InRequestInterceptor;
import org.jglue.cdiunit.internal.InSessionInterceptor;
import org.jglue.cdiunit.internal.ProducerConfigExtension;
import org.jglue.cdiunit.internal.TestScopeExtension;
import org.jglue.cdiunit.internal.easymock.EasyMockExtension;
import org.jglue.cdiunit.internal.jsf.ViewScopeExtension;
import org.jglue.cdiunit.internal.mockito.MockitoExtension;
import org.jglue.cdiunit.internal.servlet.MockHttpServletRequestImpl;
import org.jglue.cdiunit.internal.servlet.MockHttpServletResponseImpl;
import org.jglue.cdiunit.internal.servlet.MockHttpSessionImpl;
import org.jglue.cdiunit.internal.servlet.MockServletContextImpl;
import org.jglue.cdiunit.internal.servlet.ServletObjectsProducer;

import com.oneandone.ejbcdiunit.CdiTestConfig;
import com.oneandone.ejbcdiunit.internal.AsynchronousMethodInterceptor;
import com.oneandone.ejbcdiunit.internal.TransactionalInterceptor;

/**
 * @author aschoerk
 */
public class CdiUnitAnalyzer extends TestConfigAnalyzer {

    @Override
    protected void init(Class<?> testClass, CdiTestConfig config) {
        config.addExcluded(CdiUnitInitialListener.class);
        super.init(testClass, config);
    }

    @Override
    protected void initContainerSpecific(Class<?> testClass, Method testMethod) {

        extensions.add(createMetadata(new TestScopeExtension(testClass), TestScopeExtension.class.getName()));
        if (testMethod != null) {
            extensions.add(createMetadata(new ProducerConfigExtension(testMethod), ProducerConfigExtension.class.getName()));
        }

        try {
            Class.forName("javax.faces.view.ViewScoped");
            extensions.add(createMetadata(new ViewScopeExtension(), ViewScopeExtension.class.getName()));
        } catch (ClassNotFoundException e) {

        }

        try {
            Class.forName("javax.servlet.http.HttpServletRequest");
            classesToProcess.add(InRequestInterceptor.class);
            classesToProcess.add(InSessionInterceptor.class);
            classesToProcess.add(InConversationInterceptor.class);
            discoveredClasses.add(CdiUnitInitialListener.class.getName());
            classesToProcess.add(MockServletContextImpl.class);
            classesToProcess.add(MockHttpSessionImpl.class);
            classesToProcess.add(MockHttpServletRequestImpl.class);
            classesToProcess.add(MockHttpServletResponseImpl.class);


            // If this is an old version of weld then add the producers
            try {
                Class.forName("org.jboss.weld.bean.AbstractSyntheticBean");
            } catch (ClassNotFoundException e) {
                classesToProcess.add(ServletObjectsProducer.class);
            }

        } catch (ClassNotFoundException e) {}

        // Add Interceptors here, to make sure the sequence is handled right
        classesToProcess.add(AsynchronousMethodInterceptor.class);
        classesToProcess.add(TransactionalInterceptor.class);
        enabledAlternativeStereotypes.add(
                createMetadata(ProducesAlternative.class.getName(), ProducesAlternative.class.getName()));
        try {
            Class.forName("org.mockito.Mock");
            extensions.add(createMetadata(new MockitoExtension(), MockitoExtension.class.getName()));
        } catch (ClassNotFoundException e) {

        }

        try {
            Class.forName("org.easymock.EasyMockRunner");
            extensions.add(createMetadata(new EasyMockExtension(), EasyMockExtension.class.getName()));
        } catch (ClassNotFoundException e) {

        }

        extensions.add(createMetadata(new WeldSEBeanRegistrant(), WeldSEBeanRegistrant.class.getName()));

    }
}
