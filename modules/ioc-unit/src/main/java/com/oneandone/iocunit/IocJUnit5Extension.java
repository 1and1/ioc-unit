package com.oneandone.iocunit;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestInstanceFactory;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
import org.junit.jupiter.api.extension.TestInstantiationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IocJUnit5Extension implements //
        BeforeEachCallback, //
        BeforeAllCallback, //
        AfterAllCallback, //
        TestExecutionExceptionHandler, //
        TestInstanceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(IocJUnit5Extension.class);

    // global system property
    private static final String ABSENT_CODE_PREFIX = "Absent Code attribute in method that is not native or abstract in class file ";

    private IocUnitAnalyzeAndStarter analyzeAndStarter;
    protected Throwable startupException;

    @Override
    public void beforeAll(ExtensionContext context) {
        LOGGER.trace("---->before all execution {} {}\n", context.getDisplayName(), this);
        if (Objects.isNull(analyzeAndStarter)) {
            analyzeAndStarter = new IocUnitAnalyzeAndStarter();
        }
    }

    @Override
    public void afterAll(final ExtensionContext context) throws Exception {
        LOGGER.trace("---->after all execution {} {}\n", context.getDisplayName(), this);
        if (Objects.nonNull(analyzeAndStarter)) {
            analyzeAndStarter.shutdownWeldIfRunning(false);
            analyzeAndStarter = null;
        }
    }

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        if (startupException != null) {
            if (context.getTestMethod().isPresent() && context.getTestMethod().get().isAnnotationPresent(ExpectedStartupException.class)) {
                ExpectedStartupException ann = context.getTestMethod().get().getAnnotation(ExpectedStartupException.class);
                if (ann.value().isAssignableFrom(startupException.getClass())) {
                    analyzeAndStarter.shutdownWeldIfRunning(true);
                    return;
                }
            }
            if (startupException instanceof Exception) {
                throw (Exception) startupException;
            } else {
                throw new RuntimeException(startupException);
            }
        }
    }

    @Override
    public Object createTestInstance(final TestInstanceFactoryContext testInstanceFactoryContext, final ExtensionContext context)
            throws TestInstantiationException {
        try {
            LOGGER.trace("---->createTestInstance {} {}", testInstanceFactoryContext.getTestClass(), testInstanceFactoryContext.getOuterInstance());
            if (!testInstanceFactoryContext.getOuterInstance().isPresent()) {
                return this.createTestInstance(testInstanceFactoryContext.getTestClass());
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
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        if (startupException != null) {
            LOGGER.info("\"{}\" Ignored because of StartupException \"{}\"", throwable, startupException.getMessage());
            return;
        }
        throw throwable;
    }


    private Object createTestInstance(Class<?> clazz) throws Exception {
        startupException = null;
        if (Objects.isNull(analyzeAndStarter)) {
            analyzeAndStarter = new IocUnitAnalyzeAndStarter();

        }

        analyzeAndStarter.shutdownWeldIfRunning(false);
        if (!analyzeAndStarter.isRunning()) {
            LOGGER.trace("----> starting up Weld.");

            startupException = analyzeAndStarter.checkVersion();
            System.setProperty("java.naming.factory.initial", "com.oneandone.iocunit.naming.CdiTesterContextFactory");

            try {
                if (startupException == null) {
                    analyzeAndStarter.analyzeAndStart(clazz, null);
                }
            } catch (ClassFormatError e) {
                startupException = parseClassFormatError(e);
            } catch (Throwable e) {
                if (startupException == null) {
                    startupException = e;
                }
            }

            // store info about explicit param injection, either from global settings or from annotation on the test class
            if (startupException != null) {
                try {
                    Constructor<?> c = clazz.getDeclaredConstructor();
                    c.setAccessible(true);
                    return c.newInstance();
                } catch (Exception e) {
                    LOGGER.error("Exception during Startup: ", startupException);
                    throw e;
                }
            }
            analyzeAndStarter.initContexts();
            Object test = analyzeAndStarter.getCreationalContexts().create(clazz, ApplicationScoped.class);
            LOGGER.trace("---->Found test instance {}\n", test);
            return test;

        }
        return null;
    }


    private ClassFormatError parseClassFormatError(ClassFormatError e) {
        if (e.getMessage().startsWith(ABSENT_CODE_PREFIX)) {
            String offendingClass = e.getMessage().substring(ABSENT_CODE_PREFIX.length());
            URL url = IocJUnit5Extension.class.getClassLoader().getResource(offendingClass + ".class");
            String fqdn = url.toString().substring(9, url.toString().indexOf("!"));
            String m = String.format("'%s' is an API only class. You need to remove '%s' from classpath", offendingClass.replace('/', '.'), fqdn);
            return new ClassFormatError(m);
        } else {
            return e;
        }
    }
}
