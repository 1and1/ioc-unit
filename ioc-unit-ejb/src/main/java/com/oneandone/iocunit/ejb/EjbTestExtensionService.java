package com.oneandone.iocunit.ejb;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.SessionContext;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Extension;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.weld.bootstrap.spi.Metadata;
import org.jboss.weld.transaction.spi.TransactionServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.cdi.weldstarter.WeldSetup;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;
import com.oneandone.cdi.weldstarter.spi.WeldStarter;
import com.oneandone.iocunit.ejb.jms.JmsMocksFactory;
import com.oneandone.iocunit.ejb.jms.JmsProducers;
import com.oneandone.iocunit.ejb.jms.JmsSingletons;
import com.oneandone.iocunit.ejb.persistence.PersistenceFactory;
import com.oneandone.iocunit.ejb.persistence.PersistenceFactoryResources;
import com.oneandone.iocunit.ejb.persistence.SimulatedEntityTransaction;
import com.oneandone.iocunit.ejb.persistence.SimulatedTransactionManager;
import com.oneandone.iocunit.ejb.resourcesimulators.SimulatedUserTransaction;
import com.oneandone.iocunit.ejb.trainterceptors.TransactionalInterceptorBase;
import com.oneandone.iocunit.ejb.trainterceptors.TransactionalInterceptorEjb;
import com.oneandone.iocunit.ejb.trainterceptors.TransactionalInterceptorMandatory;
import com.oneandone.iocunit.ejb.trainterceptors.TransactionalInterceptorNever;
import com.oneandone.iocunit.ejb.trainterceptors.TransactionalInterceptorNotSupported;
import com.oneandone.iocunit.ejb.trainterceptors.TransactionalInterceptorRequired;
import com.oneandone.iocunit.ejb.trainterceptors.TransactionalInterceptorRequiresNew;
import com.oneandone.iocunit.ejb.trainterceptors.TransactionalInterceptorSupports;

/**
 * @author aschoerk
 */
public class EjbTestExtensionService implements TestExtensionService {

    private boolean foundPersistenceFactory = false;

    static class EjbTestExtensionServiceData {
        List<ApplicationExceptionDescription> applicationExceptions = new ArrayList<>();
        HashSet<Class<?>> candidatesToStart = new HashSet<>();
        HashSet<Class<?>> excludedClasses = new HashSet<>();

    }

    private static ThreadLocal<EjbTestExtensionServiceData> ejbTestExtensionServiceData = new ThreadLocal<>();


    private static Logger logger = LoggerFactory.getLogger("EjbTestExtensionService");

    @Override
    public void initAnalyze() {
        if(ejbTestExtensionServiceData.get() == null) {
            ejbTestExtensionServiceData.set(new EjbTestExtensionServiceData());
        }
    }

    @Override
    public List<Class<? extends Annotation>> extraClassAnnotations() {
        return Arrays.asList(EjbJarClasspath.class);
    }

    @Override
    public void handleExtraClassAnnotation(final Annotation annotation, Class<?> c) {
        if(annotation.annotationType().equals(EjbJarClasspath.class)) {
            Class<?> ejbJarClasspathExample = ((EjbJarClasspath) annotation).value();
            if(ejbJarClasspathExample != null) {
                final URL path = ejbJarClasspathExample.getProtectionDomain().getCodeSource().getLocation();
                try {
                    ejbTestExtensionServiceData.get().applicationExceptions = new EjbJarParser(path).invoke();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                ejbTestExtensionServiceData.get().applicationExceptions.clear();
            }
        }

    }

    @Override
    public boolean candidateToStart(Class<?> c) {
        if(c.getAnnotation(Entity.class) != null
           || c.getAnnotation(MappedSuperclass.class) != null
           || c.getAnnotation(MessageDriven.class) != null
           || c.getAnnotation(Startup.class) != null) {
            ejbTestExtensionServiceData.get().candidatesToStart.add(c);
        }

        if(PersistenceFactory.class.isAssignableFrom(c)) {
            this.foundPersistenceFactory = true;
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
                add(TransactionalInterceptorEjb.class);
                add(TransactionalInterceptorRequired.class);
                add(TransactionalInterceptorRequiresNew.class);
                add(TransactionalInterceptorMandatory.class);
                add(TransactionalInterceptorNever.class);
                add(TransactionalInterceptorNotSupported.class);
                add(TransactionalInterceptorSupports.class);
                add(SimulatedTransactionManager.class);
                add(SimulatedEntityTransaction.class);
                add(EjbUnitBeanInitializerClass.class);
                add(EjbUnitTransactionServices.class);
                add(JmsSingletons.class);
                add(JmsMocksFactory.class);
                add(JmsProducers.class);
                add(SessionContextFactory.class);
                add(AsynchronousManager.class);
                add(AsynchronousMethodInterceptor.class);
                add(AsynchronousMessageListenerProxy.class);
                add(PersistenceFactoryResources.class);
            }
        };
        return result;
    }

    @Override
    public void preStartupAction(WeldSetupClass weldSetup) {
        for (Class<?> c : ejbTestExtensionServiceData.get().candidatesToStart) {
            if(!ejbTestExtensionServiceData.get().excludedClasses.contains(c)) {
                if(!weldSetup.getBeanClasses().contains(c.getName())) {
                    logger.warn("Entity, Mdb or Startup candidate: {} found "
                                + " while scanning availables, but not in testconfiguration included.", c.getSimpleName());
                }
            }
        }
        ejbTestExtensionServiceData.get().candidatesToStart.clear(); // show only once
        if(weldSetup.isWeld3()) {
            if(!weldSetup.getBeanClasses().contains(SimulatedUserTransaction.class.getName())) {
                weldSetup.getBeanClasses().add(SimulatedUserTransaction.class.getName());
            }
        }
        if(!foundPersistenceFactory) {
            for (Metadata<Extension> x : weldSetup.getExtensions()) {
                if(EjbExtensionExtended.class.isAssignableFrom(x.getValue().getClass())) {
                    logger.warn("Using ioc-unit-ejb-Extension without IOC-Unit-PersistenceFactory: "
                                 + "no simulation of EntityManager and Transactions supported");
                }
            }
        }
        weldSetup.addService(new WeldSetup.ServiceConfig(TransactionServices.class, new EjbUnitTransactionServices()));
    }

    @Override
    public void postStartupAction(CreationalContexts creationalContexts, WeldStarter weldStarter) {
        creationalContexts.create(EjbUnitBeanInitializerClass.class, ApplicationScoped.class);
        if(ejbTestExtensionServiceData.get().applicationExceptions.size() > 0) {
            EjbInformationBean ejbInformationBean =
                    (EjbInformationBean) creationalContexts.create(EjbInformationBean.class, ApplicationScoped.class);
            ejbInformationBean.setApplicationExceptionDescriptions(ejbTestExtensionServiceData.get().applicationExceptions);
        }
    }

    @Override
    public Collection<? extends Class<?>> excludeFromIndexScan() {
        return Arrays.asList(
                JmsMocksFactory.class,
                EjbUnitBeanInitializerClass.class,
                AsynchronousManager.class,
                SessionContextFactory.class,
                TransactionalInterceptorBase.class);
    }

    @Override
    public Collection<? extends Class<?>> excludeAsInjects() {
        return Arrays.asList(
                SessionContext.class,
                UserTransaction.class);
    }

    @Override
    public void addQualifiers(Field f, Collection<Annotation> qualifiers) {
        Resource resource = f.getAnnotation(Resource.class);
        if(resource != null) {
            ArrayList<Annotation> annotations = new ArrayList<Annotation>();
            String typeName = f.getType().getName();
            try {
                Class literal = Class.forName("com.oneandone.iocunit.ejb.ResourceQualifier$ResourceQualifierLiteral");
                Constructor[] cs = literal.getConstructors();

                if(f.getAnnotation(Resource.class) != null) {
                    switch (typeName) {
                        case "java.lang.String":
                            qualifiers.add((Annotation) (cs[0].newInstance(resource.name(), resource.lookup(), resource.mappedName())));
                            break;
                        case "java.sql.DataSource":
                            doesResourceQualifyIfNecessary(f, qualifiers, resource, cs);
                            break;
                        case "javax.ejb.EJBContext":
                            qualifiers.add((Annotation) (cs[0].newInstance("javax.ejb.EJBContext", "", "")));
                            break;
                        case "javax.transaction.UserTransaction":
                        case "javax.ejb.SessionContext":
                        case "javax.ejb.MessageDrivenContext":
                        case "javax.ejb.EntityContext":
                            // no resource-qualifier necessary, type specifies enough
                            break;
                        default:
                            doesResourceQualifyIfNecessary(f, qualifiers, resource, cs);
                            break;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void doesResourceQualifyIfNecessary(final Field f, final Collection<Annotation> qualifiers, final Resource resource, final Constructor[] cs) throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        if(f.getAnnotation(Produces.class) == null) {
            if(resource != null && !(resource.name().isEmpty() && resource.mappedName().isEmpty() && resource.lookup().isEmpty())) {
                qualifiers.add((Annotation) (cs[0].newInstance(resource.name(), resource.lookup(), resource.mappedName())));
            }
        }
    }
}
