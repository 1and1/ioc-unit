package com.oneandone.ejbcdiunit5;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
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
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
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

public class JUnit5Extension implements TestInstancePostProcessor, AfterTestExecutionCallback, BeforeTestExecutionCallback,
        BeforeEachCallback, AfterEachCallback, BeforeAllCallback, AfterAllCallback {

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
    public void beforeTestExecution(final ExtensionContext extensionContext) throws Exception {
        logger.trace("---->before test execution {} {}\n", extensionContext.getDisplayName(), this);
    }

    @Override
    public void beforeAll(final ExtensionContext extensionContext) throws Exception {
        logger.trace("---->before All execution {} {}\n", extensionContext.getDisplayName(), this);
    }

    @Override
    public void afterAll(final ExtensionContext extensionContext) throws Exception {
        logger.trace("---->after All execution {} {}\n", extensionContext.getDisplayName(), this);
        if (determineTestLifecycle(extensionContext).equals(PER_CLASS)) {
            shutdownWeldIfRunning();
        }
    }

    private void shutdownWeldIfRunning() throws NamingException {
        if (weld != null) {
            logger.trace("----> shutting down Weld");
            initialContext.close();
            weld.shutdown();
            initialContext = null;
            weld = null;
            container = null;
        }
    }

    @Override
    public void beforeEach(final ExtensionContext extensionContext) throws Exception {
        logger.trace("---->before Each execution {} {} {}\n", extensionContext.getDisplayName(), this, extensionContext.getTestInstanceLifecycle());
        Optional<TestInstance.Lifecycle> lifecycle = extensionContext.getTestInstanceLifecycle();
        if (lifecycle.isPresent() && lifecycle.get().equals(PER_METHOD) || !lifecycle.isPresent())
            shutdownWeldIfRunning();
        if (weld == null) {
            logger.trace("----> starting up Weld.");
            try {
                String version = Formats.version(WeldBootstrap.class.getPackage());
                if ("2.2.8 (Final)".equals(version) || "2.2.7 (Final)".equals(version)) {
                    startupException = new Exception("Weld 2.2.8 and 2.2.7 are not supported. Suggest upgrading to 2.2.9");
                }


                final WeldTestConfig weldTestConfig =
                        new WeldTestConfig(clazz, null, null)
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

                Class<?> enclosingClazz = clazz;
                while (enclosingClazz.getEnclosingClass() != null)
                    enclosingClazz = enclosingClazz.getEnclosingClass();
                Object test = creationalContexts.create(enclosingClazz, ApplicationScoped.class);

                logger.trace("---->Found testinstance {}\n", test);
                // initialize the instance used for testing by fields initialized by cdi-container
                initWeldFieldsOfTestInstance(test, enclosingClazz);

            } catch (ClassNotFoundException e) {
                logger.warn("Expected EJB to be present, when using EjbUnitRunner, therefore: "
                        + "could not init Startups and Jms-Objects.");
            }
        }
    }

    @Override
    public void afterEach(final ExtensionContext extensionContext) throws Exception {
        logger.trace("---->after Each execution {} {}\n", extensionContext.getDisplayName(), this);
        if (determineTestLifecycle(extensionContext).equals(PER_METHOD)) {
            shutdownWeldIfRunning();
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
        logger.trace("---->after test execution {} {}\n", extensionContext.getDisplayName(), this);
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext extensionContext) throws Exception {
        Class<?> currentClazz = testInstance.getClass();
        TestInstance.Lifecycle lifeCycle = getLifecycle(currentClazz);
        logger.trace("---->postProcessTestInstance {} Lifecycle: {} {} {}\n", extensionContext.getDisplayName(),
                lifeCycle,
                this, testInstance);
        if (this.clazz == null || this.clazz.equals(testInstance.getClass())) {
            this.clazz = currentClazz;
            this.testInstance = testInstance;
        } else {
            logger.trace("---->not overwritten\n");
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

        while (!currentTestInstance.getClass().isAssignableFrom(clazzP)) {
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


}
