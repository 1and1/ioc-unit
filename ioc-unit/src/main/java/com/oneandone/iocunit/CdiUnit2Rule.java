package com.oneandone.iocunit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.naming.InitialContext;

import com.oneandone.iocunit.analyzer.ConfigCreator;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.iocunit.analyzer.InitialConfiguration;
import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;
import com.oneandone.cdi.weldstarter.spi.WeldStarter;

/**
 * @author aschoerk
 */
public class CdiUnit2Rule implements TestRule {
    private static Logger logger = LoggerFactory.getLogger(CdiUnit2Rule.class);
    private final Object instance;
    private final InitialConfiguration initialConfiguration;
    private WeldSetupClass weldSetup;
    private WeldStarter weldStarter;
    private final List<TestExtensionService> testExtensionServices = new ArrayList<>();
    private Method method;
    private ConfigCreator cdiConfigCreator = null;

    public CdiUnit2Rule(final Object instance) {
        this(instance, new InitialConfiguration());
    }

    public CdiUnit2Rule(final Object instance, final InitialConfiguration initialConfiguration) {
        this.instance = instance;
        this.initialConfiguration = initialConfiguration;
        if (testExtensionServices.size() == 0) {
            ServiceLoader<TestExtensionService> loader = ServiceLoader.load(TestExtensionService.class);
            final Iterator<TestExtensionService> testExtensionServiceIterator = loader.iterator();
            while (testExtensionServiceIterator.hasNext()) {
                testExtensionServices.add(testExtensionServiceIterator.next());
            }
        }
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
                weldStarter = WeldSetupClass.getWeldStarter();
                String version = weldStarter.getVersion();
                if ("2.2.8 (Final)".equals(version) || "2.2.7 (Final)".equals(version)) {
                    startupException = new Exception("Weld 2.2.8 and 2.2.7 are not supported. Suggest upgrading to 2.2.9");
                }


                if (cdiConfigCreator == null) {
                    InitialConfiguration cfg = initialConfiguration;
                    cfg.testClass = clazz;
                    cfg.testMethod = method;
                    cfg.initialClasses.add(BeanManager.class);
                    cdiConfigCreator = new ConfigCreator();
                    cdiConfigCreator.create(cfg);
                }


                weldSetup = cdiConfigCreator.buildWeldSetup(method);
                if (testExtensionServices != null) {
                    for (TestExtensionService te : testExtensionServices) {
                        te.preStartupAction(weldSetup);
                    }
                }
                weldStarter.start(weldSetup);


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
                URL url = CdiUnit2Rule.class.getClassLoader().getResource(offendingClass + ".class");

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
            if (weldSetup == null)
                initWeld();
            if (startupException != null) {
                if (method != null && method.getAnnotation(Test.class).expected() == startupException.getClass()) {
                    return;
                }
                throw startupException;
            }
            final BeanManager beanManager = weldStarter.get(BeanManager.class);
            System.setProperty("java.naming.factory.initial", "com.oneandone.iocunit.naming.CdiTesterContextFactory");
            InitialContext initialContext = new InitialContext();
            initialContext.bind("java:comp/BeanManager", beanManager);
            try (CreationalContexts creationalContexts = new CreationalContexts(beanManager)) {
                if (testExtensionServices != null) {
                    for (TestExtensionService te : testExtensionServices) {
                        te.postStartupAction(creationalContexts);
                    }
                }
                Object test = creationalContexts.create(clazz, ApplicationScoped.class);
                initWeldFields(test, test.getClass());
                next.evaluate();
            } finally {
                initialContext.close();
                weldStarter.tearDown();
                weldStarter = null;
            }


        }

        public void initWeld() {
            if (startupException == null) {
                try {
                    weldStarter = WeldSetupClass.getWeldStarter();
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