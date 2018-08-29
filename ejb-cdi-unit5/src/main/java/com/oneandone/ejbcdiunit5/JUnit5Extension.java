package com.oneandone.ejbcdiunit5;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.interceptor.InterceptorBinding;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.CDI11Bootstrap;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.jboss.weld.transaction.spi.TransactionServices;
import org.jboss.weld.util.reflection.Formats;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestInstanceFactory;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.api.extension.TestInstantiationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.CdiTestConfig;
import com.oneandone.ejbcdiunit.CreationalContexts;
import com.oneandone.ejbcdiunit.EjbUnitBeanInitializerClass;
import com.oneandone.ejbcdiunit.EjbUnitRule;
import com.oneandone.ejbcdiunit.EjbUnitTransactionServices;
import com.oneandone.ejbcdiunit.SupportEjbExtended;
import com.oneandone.ejbcdiunit.cdiunit.Weld11TestUrlDeployment;
import com.oneandone.ejbcdiunit.cdiunit.WeldTestConfig;
import com.oneandone.ejbcdiunit.cdiunit.WeldTestUrlDeployment;
import com.oneandone.ejbcdiunit.internal.EjbInformationBean;

public class JUnit5Extension implements TestInstancePostProcessor,
        BeforeEachCallback, AfterEachCallback, AfterAllCallback,
        TestExecutionExceptionHandler
        , TestInstanceFactory // TODO: 5.3
{

    private static Logger logger = LoggerFactory.getLogger(JUnit5Extension.class);
    // global system property
    private static final String ABSENT_CODE_PREFIX = "Absent Code attribute in method that is not native or abstract in class file ";
    protected Weld weld;
    protected WeldContainer container;
    protected Throwable startupException;
    // The TestCase instance
    private Class<?> clazz;
    private Object testInstance;
    private WeldTestConfig cdiTestConfig;
    CreationalContexts creationalContexts;
    InitialContext initialContext;


    @Override
    public void afterAll(final ExtensionContext extensionContext) throws Exception {
        logger.trace("---->after All execution {} {}\n", extensionContext.getDisplayName(), this);
        if (determineTestLifecycle(extensionContext).equals(PER_CLASS)) {
            shutdownWeldIfRunning(false);
        }
    }

    private void shutdownWeldIfRunning(boolean ignoreException) throws NamingException {
        if (weld != null) {
            logger.trace("----> shutting down Weld");
            logger.info("----> shutting down Weld");
            if (initialContext != null)
                initialContext.close();
            if (ignoreException) {
                try {
                    weld.shutdown();
                } catch (Throwable thw) {
                    logger.debug("Ignored {}", thw);
                }
            } else {
                weld.shutdown();
            }
            initialContext = null;
            weld = null;
            container = null;
        }
    }

    @Override
    public void beforeEach(final ExtensionContext extensionContext) throws Exception {
        logger.trace("---->before Each execution {} {} {}\n", extensionContext.getDisplayName(), this, extensionContext.getTestInstanceLifecycle());
    }

    private Object createTestInstance(final ExtensionContext extensionContext) throws Exception {
        startupException = null;
        Optional<TestInstance.Lifecycle> lifecycle = extensionContext.getTestInstanceLifecycle();
        if (lifecycle.isPresent() && lifecycle.get().equals(PER_METHOD) || !lifecycle.isPresent())
            shutdownWeldIfRunning(false);
        if (weld == null) {
            logger.trace("----> starting up Weld.");
            try {
                String version = Formats.version(WeldBootstrap.class.getPackage());
                if ("2.2.8 (Final)".equals(version) || "2.2.7 (Final)".equals(version)) {
                    startupException = new Exception("Weld 2.2.8 and 2.2.7 are not supported. Suggest upgrading to 2.2.9");
                }

                final WeldTestConfig weldTestConfig =
                        new WeldTestConfig(clazz, extensionContext.getTestMethod().orElse(null), null)
                                .addClass(SupportEjbExtended.class)
                                .addServiceConfig(new CdiTestConfig.ServiceConfig(TransactionServices.class,
                                        new EjbUnitTransactionServices()));

                this.cdiTestConfig = weldTestConfig;

                weld = new Weld() {

                    protected org.jboss.weld.bootstrap.spi.Deployment createDeployment(ResourceLoader resourceLoader, CDI11Bootstrap bootstrap) {
                        try {
                            return new Weld11TestUrlDeployment(resourceLoader, bootstrap, weldTestConfig);
                        } catch (IOException e) {
                            startupException = e;
                            throw new RuntimeException(e);
                        }
                    }

                    protected org.jboss.weld.bootstrap.spi.Deployment createDeployment(ResourceLoader resourceLoader, Bootstrap bootstrap) {
                        try {
                            return new WeldTestUrlDeployment(resourceLoader, bootstrap, weldTestConfig);
                        } catch (IOException e) {
                            startupException = e;
                            throw new RuntimeException(e);
                        }
                    }

                    ;

                };


            } catch (ClassFormatError e) {

                startupException = parseClassFormatError(e);
            } catch (Throwable e) {
                startupException = new Exception("Unable to start weld", e);
            } // store info about explicit param injection, either from global settings or from annotation on the test class


            if (container == null)
                initWeld();
            if (startupException != null) {
                if (extensionContext.getTestMethod().isPresent()
                        && extensionContext.getTestMethod().get().isAnnotationPresent(ExpectedStartupException.class)) {
                    ExpectedStartupException ann = extensionContext.getTestMethod().get().getAnnotation(ExpectedStartupException.class);
                    if (ann.value().isAssignableFrom(startupException.getClass())) {
                        shutdownWeldIfRunning(true);
                        return null;
                    }
                }
                if (startupException instanceof Exception)
                    throw (Exception) startupException;
                else
                    throw new RuntimeException(startupException);
            }
            System.setProperty("java.naming.factory.initial", "com.oneandone.cdiunit.internal.naming.CdiUnitContextFactory");
            initialContext = new InitialContext();
            final BeanManager beanManager = container.getBeanManager();
            initialContext.bind("java:comp/BeanManager", beanManager);
            creationalContexts = new CreationalContexts(beanManager);
            try {

                Class.forName("javax.ejb.EJBContext");
                creationalContexts.create(EjbUnitBeanInitializerClass.class, ApplicationScoped.class);
                EjbInformationBean ejbInformationBean =
                        (EjbInformationBean) creationalContexts.create(EjbInformationBean.class, ApplicationScoped.class);
                ejbInformationBean.setApplicationExceptionDescriptions(cdiTestConfig.getApplicationExceptionDescriptions());

                Class<?> enclosingClazz = findEnclosingClass();
                Object test = creationalContexts.create(enclosingClazz, ApplicationScoped.class);

                logger.trace("---->Found testinstance {}\n", test);
                // initialize the instance used for testing by fields initialized by cdi-container
                return test;

            } catch (ClassNotFoundException e) {
                logger.warn("Expected EJB to be present, when using EjbUnitRunner, therefore: "
                        + "could not init Startups and Jms-Objects.");
            }
        }
        return null;
    }

    private Class<?> findEnclosingClass() {
        Class<?> enclosingClazz = clazz;
        while (enclosingClazz.getEnclosingClass() != null)
            enclosingClazz = enclosingClazz.getEnclosingClass();
        return enclosingClazz;
    }

    @Override
    public void afterEach(final ExtensionContext extensionContext) throws Exception {
        logger.trace("---->after Each execution {} {}\n", extensionContext.getDisplayName(), this);

        if (determineTestLifecycle(extensionContext).equals(PER_METHOD)) {
            shutdownWeldIfRunning(startupException != null);
        }
    }

    private void checkInterceptor(Annotation[] annotations, Set<Annotation> handled) throws InterceptorBindingAtJUnit5TestInstanceException {
        for (Annotation ann : annotations) {
            if (ann.annotationType().isAnnotationPresent(InterceptorBinding.class)) {
                throw new InterceptorBindingAtJUnit5TestInstanceException();
            } else {
                if (handled.contains(ann))
                    continue;
                handled.add(ann);
                Annotation[] annotationsOfAnn = ann.annotationType().getAnnotations();
                checkInterceptor(annotationsOfAnn, handled);
            }
        }
    }


    private void checkForTopLevelAndInnerClasses(Class<?> testInstanceClass, Set<Annotation> handled) throws Exception {
        if (testInstanceClass == null)
            return;
        Annotation[] annotations = testInstanceClass.getAnnotations();
        if (handled == null)
            handled = new HashSet<>();
        checkInterceptor(annotations, handled);
        for (Method m : testInstanceClass.getDeclaredMethods()) {
            checkInterceptor(m.getAnnotations(), handled);
            if (m.isAnnotationPresent(Produces.class))
                logger.warn("Producer Method in testInstance of class {}", testInstanceClass);
            if (m.isAnnotationPresent(PostConstruct.class) || m.isAnnotationPresent(PreDestroy.class))
                throw new CdiLifecycleMgmtAtJUnit5TestInstanceException();
        }
        for (Constructor c : testInstanceClass.getDeclaredConstructors()) {
            checkInterceptor(c.getAnnotations(), handled);
            if (c.isAnnotationPresent(Produces.class))
                logger.warn("Producer Constructor in testInstance of class {}", testInstanceClass);
        }
        checkForTopLevelAndInnerClasses(testInstanceClass.getSuperclass(), handled);
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) throws Exception {
        Class<?> currentClazz = testInstance.getClass();
        TestInstance.Lifecycle lifeCycle = getLifecycle(currentClazz);
        logger.trace("---->postProcessTestInstance {} Lifecycle: {} {} {}\n", extensionContext.getDisplayName(),
                lifeCycle,
                this, testInstance);
        initClazz(testInstance, currentClazz);
    }

    private void initClazz(final Object testInstance, final Class<?> currentClazz) {
        if (this.clazz == null || testInstance != null && this.clazz.equals(testInstance.getClass())) {
            this.clazz = currentClazz;
            this.testInstance = testInstance;
        } else {
            logger.trace("---->testinstance not overwritten\n");
        }
    }

    private TestInstance.Lifecycle getLifecycle(final Class<?> currentClazz) {
        TestInstance currentLifeCycle = currentClazz.getAnnotation(TestInstance.class);
        TestInstance.Lifecycle lifeCycle = currentLifeCycle != null ? currentLifeCycle.value() : TestInstance.Lifecycle.PER_METHOD;;
        return lifeCycle;
    }

    public void initWeld() {
        if (startupException == null) {
            try {
                container = weld.initialize();
            } catch (Throwable e) {
                if (startupException == null) {
                    startupException = e;
                }
                if (e instanceof ClassFormatError) {
                    throw e;
                }
            }
        }
    }

    private void initWeldFieldsOfTestInstance(Object newTestInstance, Class<?> clazzP) throws IllegalAccessException {
        if (clazzP.equals(Object.class)) {
            return;
        }
        Object currentTestInstance = this.testInstance;
        Class<?> testInstanceClass;

        while (!clazzP.isAssignableFrom(currentTestInstance.getClass())) {
            testInstanceClass = currentTestInstance.getClass();
            if (testInstanceClass.getEnclosingClass() != null) {
                for (Field f : testInstanceClass.getDeclaredFields()) {
                    if (f.getType().equals(testInstanceClass.getDeclaringClass())) {
                        f.setAccessible(true);
                        currentTestInstance = f.get(currentTestInstance);
                        break;
                    }
                }
            } else {
                throw new RuntimeException("no fitting enclosing class found");
            }
        }
        for (Field f : clazzP.getDeclaredFields()) {
            f.setAccessible(true);
            if (f.getAnnotation(Inject.class) != null) {
                f.set(currentTestInstance, f.get(newTestInstance));
            } else {
                if (f.get(newTestInstance) != null && f.get(this.testInstance) == null) {
                    f.set(currentTestInstance, f.get(newTestInstance));
                }
            }

        }
        initWeldFieldsOfTestInstance(newTestInstance, clazzP.getSuperclass());
    }

    private ClassFormatError parseClassFormatError(ClassFormatError e) {
        if (e.getMessage().startsWith(ABSENT_CODE_PREFIX)) {
            String offendingClass = e.getMessage().substring(ABSENT_CODE_PREFIX.length());
            URL url = EjbUnitRule.class.getClassLoader().getResource(offendingClass + ".class");

            return new ClassFormatError("'" + offendingClass.replace('/', '.')
                    + "' is an API only class. You need to remove '"
                    + url.toString().substring(9, url.toString().indexOf("!")) + "' from your classpath");
        } else {
            return e;
        }
    }

    private List<Annotation> resolveQualifiers(ParameterContext pc, BeanManager bm) {
        List<Annotation> qualifiers = new ArrayList<>();
        if (pc.getParameter().getAnnotations().length == 0) {
            return Collections.emptyList();
        } else {
            for (Annotation annotation : pc.getParameter().getAnnotations()) {
                // use BeanManager.isQualifier to be able to detect custom qualifiers which don't need to have @Qualifier
                if (bm.isQualifier(annotation.annotationType())) {
                    qualifiers.add(annotation);
                }
            }
        }
        return qualifiers;
    }

    private TestInstance.Lifecycle determineTestLifecycle(ExtensionContext ec) {
        // check the test for import org.junit.jupiter.api.TestInstance annotation
        TestInstance annotation = ec.getRequiredTestClass().getAnnotation(TestInstance.class);
        if (annotation != null) {
            return annotation.value();
        } else {
            return PER_METHOD;
        }
    }


    @Override
    public void handleTestExecutionException(ExtensionContext extensionContext, Throwable throwable) throws Throwable {
        if (startupException != null) {
            logger.info("\"{}\" Ignored because of StartupException \"{}\"", throwable, startupException.getMessage());
            return;
        }
        throw throwable;
    }

     @Override public Object createTestInstance(final TestInstanceFactoryContext testInstanceFactoryContext, final ExtensionContext
     extensionContext) throws TestInstantiationException
     {
         try {
            initClazz(null, testInstanceFactoryContext.getTestClass());
             logger.trace("---->createTestInstance {} {}", testInstanceFactoryContext.getTestClass(), testInstanceFactoryContext.getOuterInstance());
            Object test = this.createTestInstance(extensionContext);
            return test; // testInstanceFactoryContext.getTestClass().newInstance();
        } catch (Exception e) {
             throw new RuntimeException(e);
         }
     }
}
