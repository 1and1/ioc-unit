package com.oneandone.cdi.tester.ejb;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Extension;
import javax.persistence.Entity;
import javax.persistence.PersistenceContext;

import org.jboss.weld.transaction.spi.TransactionServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.tester.ejb.jms.JmsSingletons;
import com.oneandone.cdi.tester.ejb.persistence.SimulatedEntityTransaction;
import com.oneandone.cdi.tester.ejb.persistence.SimulatedTransactionManager;
import com.oneandone.cdi.tester.ejb.resourcesimulators.SimulatedUserTransaction;
import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.cdi.weldstarter.WeldSetup;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;

/**
 * @author aschoerk
 */
public class EjbTestExtensionService implements TestExtensionService {

    static class EjbTestExtensionServiceData {
        List<ApplicationExceptionDescription> applicationExceptions = new ArrayList<>();
        HashSet<Class<?>> candidatesToStart = new HashSet<>();
        HashSet<Class<?>> excludedClasses = new HashSet<>();

    }

    private static ThreadLocal<EjbTestExtensionServiceData> ejbTestExtensionServiceData = new ThreadLocal<>();


    private static Logger logger = LoggerFactory.getLogger("EjbTestExtensionService");

    @Override
    public void initAnalyze() {
        if (ejbTestExtensionServiceData.get() == null)
            ejbTestExtensionServiceData.set(new EjbTestExtensionServiceData());
    }

    @Override
    public List<Class<? extends Annotation>> extraClassAnnotations() {
        return Arrays.asList(EjbJarClasspath.class);
    }

    @Override
    public void handleExtraClassAnnotation(final Annotation annotation, Class<?> c) {
        if (annotation.annotationType().equals(EjbJarClasspath.class)) {
            Class<?> ejbJarClasspathExample = ((EjbJarClasspath) annotation).value();
            if (ejbJarClasspathExample != null) {
                final URL path = ejbJarClasspathExample.getProtectionDomain().getCodeSource().getLocation();
                try {
                    ejbTestExtensionServiceData.get().applicationExceptions = new EjbJarParser(path).invoke();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                ejbTestExtensionServiceData.get().applicationExceptions.clear();
            }
        }

    }

    @Override
    public boolean candidateToStart(Class<?> c) {
        if (c.getAnnotation(Entity.class) != null
                || c.getAnnotation(MessageDriven.class) != null
                || c.getAnnotation(Startup.class) != null) {
            ejbTestExtensionServiceData.get().candidatesToStart.add(c);
        }

        return c.getAnnotation(Entity.class) != null;
    }

    @Override
    public void explicitlyExcluded(Class<?> c) {
        ejbTestExtensionServiceData.get().excludedClasses.add(c);
    }

    @Override
    public Collection<Class<? extends Annotation>> injectAnnotations() {
        return Arrays.asList(Resource.class, EJB.class, PersistenceContext.class);
    }

    @Override
    public List<Class<?>> testClasses() {
        List<Class<?>> result = new ArrayList<Class<?>>() {
            private static final long serialVersionUID = -1661631254833065243L;

            {
                add(EjbJarClasspath.class);
                add(EjbExtensionExtended.class);
                add(EjbInformationBean.class);
                // add(WeldSEBeanRegistrant.class);
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
        for (Class<?> c : ejbTestExtensionServiceData.get().candidatesToStart) {
            if (!ejbTestExtensionServiceData.get().excludedClasses.contains(c))
                if (!weldSetup.getBeanClasses().contains(c.getName())) {
                    logger.warn("Entity, Mdb or Startup Candidate: {} not started", c.getSimpleName());
                }
        }
        ejbTestExtensionServiceData.get().candidatesToStart.clear(); // show only once
        if (weldSetup.isWeld3()) {
            if (!weldSetup.getBeanClasses().contains(SimulatedUserTransaction.class.getName())) {
                weldSetup.getBeanClasses().add(SimulatedUserTransaction.class.getName());
            }
        }
        weldSetup.addService(new WeldSetup.ServiceConfig(TransactionServices.class, new EjbUnitTransactionServices()));
    }

    @Override
    public void postStartupAction(CreationalContexts creationalContexts) {

        creationalContexts.create(EjbUnitBeanInitializerClass.class, ApplicationScoped.class);
        if (ejbTestExtensionServiceData.get().applicationExceptions.size() > 0) {
            EjbInformationBean ejbInformationBean =
                    (EjbInformationBean) creationalContexts.create(EjbInformationBean.class, ApplicationScoped.class);
            ejbInformationBean.setApplicationExceptionDescriptions(ejbTestExtensionServiceData.get().applicationExceptions);
        }
    }
}
