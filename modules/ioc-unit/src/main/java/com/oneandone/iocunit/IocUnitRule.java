package com.oneandone.iocunit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import javax.naming.InitialContext;

import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.analyzer.InitialConfiguration;
import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;

/**
 * @author aschoerk
 */
public class IocUnitRule implements TestRule {
    private static Logger logger = LoggerFactory.getLogger(IocUnitRule.class);
    private final Object instance;
    private final InitialConfiguration initialConfiguration;
    private final List<TestExtensionService> testExtensionServices = new ArrayList<>();
    private Method method;
    IocUnitAnalyzeAndStarter analyzeAndStarter;

    public IocUnitRule(final Object instance) {
        this(instance, new InitialConfiguration());
    }

    public IocUnitRule(final Object instance, final InitialConfiguration initialConfiguration) {
        this.instance = instance;
        this.initialConfiguration = initialConfiguration;
        analyzeAndStarter = new IocUnitAnalyzeAndStarter(initialConfiguration);
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        try {
            method = description.getTestClass().getMethod(description.getMethodName());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        Deployment result = new Deployment(instance, base, method);
        if (result.startupException != null) {
            if (!method.getAnnotation(Test.class).expected().equals(result.startupException.getClass())) {
                throw new RuntimeException(result.startupException);
            }
        }
        return result;
    }

    public class Deployment extends Statement {
        private static final String ABSENT_CODE_PREFIX = "Absent Code attribute in method that is not native or abstract in class file ";
        private final Statement next;
        protected Throwable startupException;
        // The TestCase instance
        private Class<?> clazz;
        private Object testInstance;

        public Deployment(final Object instance, final Statement next, final Method method) {
            this.clazz = instance.getClass();
            this.testInstance = instance;
            this.next = next;
            try {
                startupException = analyzeAndStarter.checkVersion();
                analyzeAndStarter.analyzeAndStart(clazz, method);

            } catch (ClassFormatError e) {

                startupException = parseClassFormatError(e);
            } catch (Throwable e) {
                if (startupException == null) {
                    startupException = e;
                }
            }

        }

        private ClassFormatError parseClassFormatError(ClassFormatError e) {
            if (e.getMessage().startsWith(ABSENT_CODE_PREFIX)) {
                String offendingClass = e.getMessage().substring(ABSENT_CODE_PREFIX.length());
                URL url = IocUnitRule.class.getClassLoader().getResource(offendingClass + ".class");

                return new ClassFormatError("'" + offendingClass.replace('/', '.')
                        + "' is an API only class. You need to remove '"
                        + url.toString().substring(9, url.toString().indexOf("!")) + "' from your classpath");
            } else {
                return e;
            }
        }

        /**
         * Run the action, throwing a {@code Throwable} if anything goes wrong.
         */
        @Override
        public void evaluate() throws Throwable {
            if (analyzeAndStarter.weldSetup == null)
                initWeld();
            if (startupException != null) {
                if (method != null && method.getAnnotation(Test.class).expected() == startupException.getClass()) {
                    return;
                }
                throw startupException;
            }
            try {
                analyzeAndStarter.initContexts();
                Object test = analyzeAndStarter.getCreationalContexts().create(clazz, ApplicationScoped.class);
                initWeldFields(test, test.getClass());
                next.evaluate();
            } finally {
                analyzeAndStarter.tearDown();
            }


        }

        public void initWeld() {
            if (startupException == null) {
                try {
                    analyzeAndStarter.getWeldStarter();
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

        /**
         * Since it is not possible to intercept the Test-Class-Instance creation in JUnitRules, here the Testclass as created by Weld used to fill
         * the old Testclass-Instance by the injected values.
         *
         * @param newTestInstance
         * @param clazzP
         * @throws IllegalAccessException
         */
        private void initWeldFields(Object newTestInstance, Class<?> clazzP) throws IllegalAccessException {
            if (clazzP.equals(Object.class)) {
                return;
            }
            for (Field f : clazzP.getDeclaredFields()) {
                f.setAccessible(true);
                if (f.getAnnotation(Inject.class) != null) {
                    f.set(testInstance, f.get(newTestInstance));
                } else {
                    if (f.get(newTestInstance) != null && f.get(testInstance) == null) {
                        f.set(testInstance, f.get(newTestInstance));
                    }
                }

            }
            initWeldFields(newTestInstance, clazzP.getSuperclass());
        }

    }

}