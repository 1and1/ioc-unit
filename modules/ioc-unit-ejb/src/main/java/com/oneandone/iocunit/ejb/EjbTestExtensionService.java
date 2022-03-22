package com.oneandone.iocunit.ejb;

import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.V1_5;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
import javax.ejb.SessionContext;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;

import org.jboss.weld.transaction.spi.TransactionServices;
import org.objectweb.asm.ClassWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.cdi.weldstarter.WeldSetup;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;
import com.oneandone.cdi.weldstarter.spi.WeldStarter;
import com.oneandone.iocunit.ejb.jms.AsynchronousMessageListenerProxy;
import com.oneandone.iocunit.ejb.jms.EjbJmsInitializer;
import com.oneandone.iocunit.ejb.jms.EjbJmsMocksFactory;
import com.oneandone.iocunit.ejb.persistence.IocUnitTransactionSynchronizationRegistry;
import com.oneandone.iocunit.ejb.persistence.PersistenceFactory;
import com.oneandone.iocunit.ejb.persistence.PersistenceFactoryResources;
import com.oneandone.iocunit.ejb.persistence.SimulatedTransactionManager;
import com.oneandone.iocunit.ejb.resourcesimulators.WebServiceContextSimulation;
import com.oneandone.iocunit.ejb.trainterceptors.TransactionalInterceptorBase;
import com.oneandone.iocunit.ejb.trainterceptors.TransactionalInterceptorEjb;
import com.oneandone.iocunit.ejb.trainterceptors.TransactionalInterceptorMandatory;
import com.oneandone.iocunit.ejb.trainterceptors.TransactionalInterceptorNever;
import com.oneandone.iocunit.ejb.trainterceptors.TransactionalInterceptorNotSupported;
import com.oneandone.iocunit.ejb.trainterceptors.TransactionalInterceptorRequired;
import com.oneandone.iocunit.ejb.trainterceptors.TransactionalInterceptorRequiresNew;
import com.oneandone.iocunit.ejb.trainterceptors.TransactionalInterceptorSupports;
import com.oneandone.iocunit.jms.JmsProducers;

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
    public List<Class<?>> handleExtraClassAnnotation(final Annotation annotation, Class<?> c) {
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

        return Collections.EMPTY_LIST;
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

    public static void checkCreateMessageContextInterface() {
        try {
            Class.forName("javax.xml.rpc.handler.MessageContext").getDeclaredMethods();
            return;
        } catch (ClassNotFoundException ncdfe) {
            ClassWriter cw = new ClassWriter(0);
            cw.visit(V1_5, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, "javax/xml/rpc/handler/MessageContext",
                    null, "java/lang/Object", new String[]{});
            cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "setProperty", "(Ljava/lang/String;Ljava/lang/Object;)V", null, null);
            cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getProperty", "(Ljava/lang/String;)Ljava/lang/Object;", null, null);
            cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "removeProperty", "(Ljava/lang/String;)V", null, null);
            cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "containsProperty", "(Ljava/lang/String;)B", null, null);
            cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "getPropertyNames", "()Ljava/util/Iterator;", null, null);
            cw.visitEnd();
            byte[] ba = cw.toByteArray();
            Object o = EjbTestExtensionService.class.getClassLoader();
            Class c = o.getClass();
            Method m = null;
            while (!c.equals(Object.class)) {
                try {
                    m = c.getDeclaredMethod("defineClass", String.class, ba.getClass(), Integer.TYPE, Integer.TYPE);
                    break;
                } catch (NoSuchMethodException nsme) {
                    c = c.getSuperclass();
                }
            }
            m.setAccessible(true);
            try {
                m.invoke(o, "javax.xml.rpc.handler.MessageContext", ba, 0, ba.length);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static List<Class<?>> testClasses = new ArrayList<Class<?>>() {
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
            add(IocUnitTransactionSynchronizationRegistry.class);
            add(SimulatedTransactionManager.class);
            add(EjbUnitBeanInitializerClass.class);
            add(EjbUnitTransactionServices.class);
            checkCreateMessageContextInterface();
            add(SessionContextFactory.class);
            add(AsynchronousManager.class);
            add(AsynchronousMethodInterceptor.class);
            try {
                javax.xml.ws.handler.MessageContext.class.getMethods();
                HttpServletResponse.class.getMethods();
                add(WebServiceContextSimulation.class);
            } catch (NoClassDefFoundError e) {
                logger.trace("No WebServiceContextSimulation because of {}", e.getMessage());
            }
            try {
                add(AsynchronousMessageListenerProxy.class);
                add(EjbJmsInitializer.class);
                add(JmsExtension.class);
                add(EjbJmsMocksFactory.class);
                add(JmsProducers.class);
            } catch (NoClassDefFoundError e) {
                logger.trace("no Jms because of {}", e.getMessage());
            }
            add(PersistenceFactoryResources.class);
        }
    };

    @Override
    public List<Class<?>> testClasses() {
        return testClasses;
    }

    @Override
    public void preStartupAction(WeldSetupClass weldSetup, Class clazz, Method method) {
        for (Class<?> c : ejbTestExtensionServiceData.get().candidatesToStart) {
            if(!ejbTestExtensionServiceData.get().excludedClasses.contains(c)) {
                if(!weldSetup.getBeanClasses().contains(c.getName())) {
                    logger.warn("Entity, Mdb or Startup candidate: {} found "
                                + " while scanning availables, but not in testconfiguration included.", c.getSimpleName());
                }
            }
        }
        ejbTestExtensionServiceData.get().candidatesToStart.clear(); // show only once
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

        List<Class<?>> result = new ArrayList<Class<?>>() {
            private static final long serialVersionUID = -2079977943206299793L;

            {
                add(SessionContextFactory.class);
                add(EjbUnitBeanInitializerClass.class);
                add(AsynchronousManager.class);
                add(TransactionalInterceptorBase.class);
            }
        };
        return result;
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
                Class literal = Class.forName("com.oneandone.iocunit.resource.ResourceQualifier$ResourceQualifierLiteral");
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
