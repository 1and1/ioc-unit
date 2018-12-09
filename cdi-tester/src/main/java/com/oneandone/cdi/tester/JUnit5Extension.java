package com.oneandone.cdi.tester;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestInstanceFactory;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
import org.junit.jupiter.api.extension.TestInstantiationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.testanalyzer.CdiConfigCreator;
import com.oneandone.cdi.testanalyzer.InitialConfiguration;
import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.WrappedDeploymentException;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;
import com.oneandone.cdi.weldstarter.spi.WeldStarter;

public class JUnit5Extension implements BeforeEachCallback,
        AfterAllCallback, TestExecutionExceptionHandler, TestInstanceFactory {

    private static Logger logger = LoggerFactory.getLogger(JUnit5Extension.class);
    // global system property
    private static final String ABSENT_CODE_PREFIX = "Absent Code attribute in method that is not native or abstract in class file ";
    protected WeldStarter weldStarter;
    private WeldSetupClass weldSetup;
    CdiConfigCreator cdiConfigCreator = null;
    private final List<TestExtensionService> testExtensionServices = new ArrayList<>();


    protected Throwable startupException;
    private Method testMethod;
    private Class<?> testClass;

    CreationalContexts creationalContexts;
    InitialContext initialContext;

    public JUnit5Extension() {
        if (testExtensionServices.size() == 0) {
            ServiceLoader<TestExtensionService> loader = ServiceLoader.load(TestExtensionService.class);
            final Iterator<TestExtensionService> testExtensionServiceIterator = loader.iterator();
            while (testExtensionServiceIterator.hasNext()) {
                testExtensionServices.add(testExtensionServiceIterator.next());
            }
        }
    }

    @Override
    public void afterAll(final ExtensionContext extensionContext) throws Exception {
        logger.trace("---->after All execution {} {}\n", extensionContext.getDisplayName(), this);
        shutdownWeldIfRunning(false);
    }

    private void shutdownWeldIfRunning(boolean ignoreException) throws NamingException {
        if (weldStarter != null) {
            logger.trace("----> shutting down Weld");
            if (ignoreException) {
                try {
                    weldStarter.tearDown();
                } catch (Throwable thw) {
                    logger.debug("Ignored {}", thw);
                }
            } else {
                weldStarter.tearDown();
            }
            weldStarter = null;
        }
    }

    private Object createTestInstance(Class<?> clazz) throws Exception {
        startupException = null;
        shutdownWeldIfRunning(false);
        if (weldStarter == null) {
            logger.trace("----> starting up Weld.");

            weldStarter = WeldSetupClass.getWeldStarter();
            String version = weldStarter.getVersion();
            if ("2.2.8 (Final)".equals(version) || "2.2.7 (Final)".equals(version)) {
                startupException = new Exception("Weld 2.2.8 and 2.2.7 are not supported. Suggest upgrading to 2.2.9");
            }

            System.setProperty("java.naming.factory.initial", "com.oneandone.cdi.tester.naming.CdiTesterContextFactory");

            try {
                if (cdiConfigCreator == null) {
                    InitialConfiguration cfg = new InitialConfiguration();
                    cfg.testClass = clazz;
                    cfg.testMethod = testMethod;
                    cfg.initialClasses.add(BeanManager.class);
                    cdiConfigCreator = new CdiConfigCreator();
                    cdiConfigCreator.create(cfg);
                }

                weldSetup = cdiConfigCreator.buildWeldSetup(testMethod);
                if (testExtensionServices != null) {
                    for (TestExtensionService te : testExtensionServices) {
                        te.preStartupAction(weldSetup);
                    }
                }
                weldStarter.start(weldSetup);
            } catch (ClassFormatError e) {

                startupException = parseClassFormatError(e);
            } catch (Throwable e) {
                if (e instanceof WrappedDeploymentException)
                    e = e.getCause();
                if (startupException == null) {
                    startupException = e;
                }
            } // store info about explicit param injection, either from global settings or from annotation on the test class
            if (startupException != null) {
                return clazz.newInstance(); // prepare default, to allow beforeEach to handle exception.
            }
            System.setProperty("java.naming.factory.initial", "com.oneandone.cdi.tester.naming.CdiTesterContextFactory");
            initialContext = new InitialContext();
            final BeanManager beanManager = weldStarter.get(BeanManager.class);
            initialContext.bind("java:comp/BeanManager", beanManager);
            creationalContexts = new CreationalContexts(beanManager);
            if (testExtensionServices != null) {
                for (TestExtensionService te : testExtensionServices) {
                    te.postStartupAction(creationalContexts);
                }
            }
            Object test = creationalContexts.create(clazz, ApplicationScoped.class);
            logger.trace("---->Found testinstance {}\n", test);
            return test;

        }
        return null;
    }

    private Class<?> findEnclosingClass(Class<?> clazz) {
        Class<?> enclosingClazz = clazz;
        while (enclosingClazz.getEnclosingClass() != null)
            enclosingClazz = enclosingClazz.getEnclosingClass();
        return enclosingClazz;
    }

    public void initWeld() {
        if (startupException == null) {
            try {
                weldStarter.start(weldSetup);
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

    private ClassFormatError parseClassFormatError(ClassFormatError e) {
        if (e.getMessage().startsWith(ABSENT_CODE_PREFIX)) {
            String offendingClass = e.getMessage().substring(ABSENT_CODE_PREFIX.length());
            URL url = JUnit5Extension.class.getClassLoader().getResource(offendingClass + ".class");

            return new ClassFormatError("'" + offendingClass.replace('/', '.')
                    + "' is an API only class. You need to remove '"
                    + url.toString().substring(9, url.toString().indexOf("!")) + "' from your classpath");
        } else {
            return e;
        }
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

    @Override
    public Object createTestInstance(final TestInstanceFactoryContext testInstanceFactoryContext, final ExtensionContext extensionContext)
            throws TestInstantiationException {
        try {
            logger.trace("---->createTestInstance {} {}", testInstanceFactoryContext.getTestClass(), testInstanceFactoryContext.getOuterInstance());
            if (!testInstanceFactoryContext.getOuterInstance().isPresent()) {
                Object test = this.createTestInstance(testInstanceFactoryContext.getTestClass());
                return test;
            } else {
                final Object outerInstance = testInstanceFactoryContext.getOuterInstance().get();
                Constructor<?> c = testInstanceFactoryContext.getTestClass().getDeclaredConstructor(outerInstance.getClass());
                c.setAccessible(true);
                return c.newInstance(outerInstance);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void beforeEach(final ExtensionContext extensionContext) throws Exception {
        if (startupException != null) {
            this.testMethod = extensionContext.getTestMethod().orElse(null);
            this.testClass = extensionContext.getTestClass().orElse(null);
            if (extensionContext.getTestMethod().isPresent()
                    && extensionContext.getTestMethod().get().isAnnotationPresent(ExpectedStartupException.class)) {
                ExpectedStartupException ann = extensionContext.getTestMethod().get().getAnnotation(ExpectedStartupException.class);
                if (ann.value().isAssignableFrom(startupException.getClass())) {
                    shutdownWeldIfRunning(true);
                    return;
                }
            }
            if (startupException instanceof Exception)
                throw (Exception) startupException;
            else
                throw new RuntimeException(startupException);
        }
    }

}
