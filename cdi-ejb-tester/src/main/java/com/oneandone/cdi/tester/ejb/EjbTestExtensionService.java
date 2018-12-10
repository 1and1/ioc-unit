package com.oneandone.cdi.tester.ejb;

import com.oneandone.cdi.tester.ejb.jms.JmsSingletons;
import com.oneandone.cdi.tester.ejb.persistence.SimulatedEntityTransaction;
import com.oneandone.cdi.tester.ejb.persistence.SimulatedTransactionManager;
import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.cdi.weldstarter.WeldSetup;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;
import org.jboss.weld.transaction.spi.TransactionServices;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Extension;
import javax.persistence.Entity;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.*;

/**
 * @author aschoerk
 */
public class EjbTestExtensionService implements TestExtensionService {
    private static List<ApplicationExceptionDescription> applicationExceptions;

    @Override
    public void initAnalyze() {
        applicationExceptions = null;
    }

    @Override
    public Collection<Extension> getExtensions() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<Class<? extends Annotation>> extraClassAnnotations() {
        return Arrays.asList(EjbJarClasspath.class);
    }

    @Override
    public void handleExtraClassAnnotation(final Annotation annotation, Class<?> c) {
        if (annotation.annotationType().equals(EjbJarClasspath.class)) {
            Class<?> ejbJarClasspathExample = ((EjbJarClasspath) annotation).value();
            if (ejbJarClasspathExample != null) {
                final URL path = ejbJarClasspathExample.getProtectionDomain().getCodeSource().getLocation();
                try {
                    applicationExceptions = new EjbJarParser(path).invoke();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                applicationExceptions = null;
            }
        }

    }

    @Override
    public boolean isSutClass(Class<?> c) {
        return c.getAnnotation(Entity.class) != null;
    }

    @Override
    public Collection<Class<? extends Annotation>> injectAnnotations() {
        return Arrays.asList(Resource.class, EJB.class, PersistenceContext.class);
    }

    @Override
    public Collection<Class<?>> testClasses() {
        HashSet<Class<?>> result = new HashSet<Class<?>>() {
            private static final long serialVersionUID = -1661631254833065243L;

            {
                add(EjbJarClasspath.class);
                add(EjbExtensionExtended.class);
                add(EjbInformationBean.class);
                // add(WeldSEBeanRegistrant.class);
                // ProducerConfigExtension.class,
                add(TransactionalInterceptor.class);
                add(SimulatedTransactionManager.class);
                add(SimulatedEntityTransaction.class);
                add(EjbUnitBeanInitializerClass.class);
                add(EjbUnitTransactionServices.class);
                add(JmsSingletons.class);
                add(SessionContextFactory.class);
                add(AsynchronousManager.class);
                add(AsynchronousMethodInterceptor.class);
                add(AsynchronousMessageListenerProxy.class);
            }
        };
        return result;
    }

    @Override
    public void preStartupAction(WeldSetupClass weldSetup) {
        weldSetup.addService(new WeldSetup.ServiceConfig(TransactionServices.class, new EjbUnitTransactionServices()));
    }

    @Override
    public void postStartupAction(CreationalContexts creationalContexts) {

        creationalContexts.create(EjbUnitBeanInitializerClass.class, ApplicationScoped.class);
        if (applicationExceptions != null) {
            EjbInformationBean ejbInformationBean =
                    (EjbInformationBean) creationalContexts.create(EjbInformationBean.class, ApplicationScoped.class);
            ejbInformationBean.setApplicationExceptionDescriptions(applicationExceptions);
        }
    }
}
