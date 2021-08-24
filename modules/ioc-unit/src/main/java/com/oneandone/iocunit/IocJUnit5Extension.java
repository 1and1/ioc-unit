package com.oneandone.iocunit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;

import javax.enterprise.context.ApplicationScoped;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestInstanceFactory;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
import org.junit.jupiter.api.extension.TestInstantiationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IocJUnit5Extension implements BeforeEachCallback,
        AfterAllCallback, TestExecutionExceptionHandler, TestInstanceFactory {

    private static Logger logger = LoggerFactory.getLogger(IocJUnit5Extension.class);
    // global system property
    private static final String ABSENT_CODE_PREFIX = "Absent Code attribute in method that is not native or abstract in class file ";
    IocUnitAnalyzeAndStarter analyzeAndStarter = new IocUnitAnalyzeAndStarter();

    protected Throwable startupException;
    private Method testMethod;
    private Class<?> testClass;


    public IocJUnit5Extension() {

    }

    @Override
    public void afterAll(final ExtensionContext extensionContext) throws Exception {
        logger.trace("---->after All execution {} {}\n", extensionContext.getDisplayName(), this);
        analyzeAndStarter.shutdownWeldIfRunning(false);
    }


    private Object createTestInstance(Class<?> clazz) throws Exception {
        startupException = null;
        analyzeAndStarter.shutdownWeldIfRunning(false);
        if(!analyzeAndStarter.isRunning()) {
            logger.trace("----> starting up Weld.");

            startupException = analyzeAndStarter.checkVersion();
            System.setProperty("java.naming.factory.initial", "com.oneandone.iocunit.naming.CdiTesterContextFactory");

            try {
                if (startupException == null)
                    analyzeAndStarter.analyzeAndStart(clazz, null);
            } catch (ClassFormatError e) {
                startupException = parseClassFormatError(e);
            } catch (Throwable e) {
                if(startupException == null) {
                    startupException = e;
                }
            } // store info about explicit param injection, either from global settings or from annotation on the test class
            if(startupException != null) {
                try {
                    Constructor<?> c = clazz.getDeclaredConstructor();
                    c.setAccessible(true);
                    return c.newInstance();
                } catch (Exception e) {
                    logger.error("Exception during Startup: ", startupException);
                    throw e;
                }
            }
            analyzeAndStarter.initContexts();
            Object test = analyzeAndStarter.getCreationalContexts().create(clazz, ApplicationScoped.class);
            logger.trace("---->Found testinstance {}\n", test);
            return test;

        }
        return null;
    }




    private ClassFormatError parseClassFormatError(ClassFormatError e) {
        if(e.getMessage().startsWith(ABSENT_CODE_PREFIX)) {
            String offendingClass = e.getMessage().substring(ABSENT_CODE_PREFIX.length());
            URL url = IocJUnit5Extension.class.getClassLoader().getResource(offendingClass + ".class");

            return new ClassFormatError("'" + offendingClass.replace('/', '.')
                                        + "' is an API only class. You need to remove '"
                                        + url.toString().substring(9, url.toString().indexOf("!")) + "' from your classpath");
        }
        else {
            return e;
        }
    }

    @Override
    public void handleTestExecutionException(ExtensionContext extensionContext, Throwable throwable) throws Throwable {
        if(startupException != null) {
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
            if(!testInstanceFactoryContext.getOuterInstance().isPresent()) {
                Object test = this.createTestInstance(testInstanceFactoryContext.getTestClass());
                return test;
            }
            else {
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
        if(startupException != null) {
            this.testMethod = extensionContext.getTestMethod().orElse(null);
            this.testClass = extensionContext.getTestClass().orElse(null);
            if(extensionContext.getTestMethod().isPresent()
               && extensionContext.getTestMethod().get().isAnnotationPresent(ExpectedStartupException.class)) {
                ExpectedStartupException ann = extensionContext.getTestMethod().get().getAnnotation(ExpectedStartupException.class);
                if(ann.value().isAssignableFrom(startupException.getClass())) {
                    analyzeAndStarter.shutdownWeldIfRunning(true);
                    return;
                }
            }
            if(startupException instanceof Exception) {
                throw (Exception) startupException;
            }
            else {
                throw new RuntimeException(startupException);
            }
        }
    }

}
