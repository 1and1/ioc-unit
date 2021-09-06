package com.oneandone.cdi.discoveryrunner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.discoveryrunner.internal.AnnotationInterpreter;
import com.oneandone.cdi.discoveryrunner.internal.WeldDiscoveryCdiExtension;
import com.oneandone.cdi.discoveryrunner.internal.WeldInfo;
import com.oneandone.cdi.discoveryrunner.naming.CdiUnitContext;

/**
 * @author aschoerk
 */
public class WeldDiscoveringRule implements TestRule {
    private static Logger logger = LoggerFactory.getLogger(WeldDiscoveringRule.class);
    private final Object instance;
    private Method method;

    Weld weld;
    WeldContainer container;
    WeldInfo weldInfo;
    private CreationalContexts creationalContexts;
    private InitialContext initialContext;

    public WeldDiscoveringRule(final Object instance) {
        this.instance = instance;
        weldInfo = new WeldInfo();
        AnnotationInterpreter.prepareWeldInfo(instance.getClass(), weldInfo);
    }

    private void initWeld() throws NamingException {
        if(weld != null) {
            try {
                exitWeld();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        weld = new Weld()
                .addExtension(new WeldDiscoveryCdiExtension(weldInfo))
                .alternatives(weldInfo.getAlternatives().toArray(new Class[weldInfo.getAlternatives().size()]))
                .beanClasses(weldInfo.getToAdd().toArray(new Class[weldInfo.getToAdd().size()]));
        this.initialContext = new InitialContext();
        container = weld.initialize();
        final BeanManager beanManager = container.getBeanManager();
        initialContext.rebind("java:comp/BeanManager", beanManager);
        this.creationalContexts = new CreationalContexts(beanManager);
    }

    private void exitWeld() throws Exception {
        if(weld != null) {
            creationalContexts.close();
            initialContext.close();
            CdiUnitContext.init();
            weld.shutdown();
            weld = null;
            container = null;
            creationalContexts = null;
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
        if(result.startupException != null) {
            if(!method.getAnnotation(Test.class).expected().equals(result.startupException.getClass())) {
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
                initWeld();
            } catch (ClassFormatError e) {
                startupException = parseClassFormatError(e);
            } catch (Throwable e) {
                if(startupException == null) {
                    startupException = e;
                }
            }

        }

        private ClassFormatError parseClassFormatError(ClassFormatError e) {
            if(e.getMessage().startsWith(ABSENT_CODE_PREFIX)) {
                String offendingClass = e.getMessage().substring(ABSENT_CODE_PREFIX.length());
                URL url = WeldDiscoveringRule.class.getClassLoader().getResource(offendingClass + ".class");

                return new ClassFormatError("'" + offendingClass.replace('/', '.')
                                            + "' is an API only class. You need to remove '"
                                            + url.toString().substring(9, url.toString().indexOf("!")) + "' from your classpath");
            }
            else {
                return e;
            }
        }

        /**
         * Run the action, throwing a {@code Throwable} if anything goes wrong.
         */
        @Override
        public void evaluate() throws Throwable {
            if(startupException != null) {
                if(method != null && method.getAnnotation(Test.class).expected() == startupException.getClass()) {
                    return;
                }
                throw startupException;
            }
            try {
                if(WeldDiscoveringRule.this.creationalContexts == null) {
                    initWeld();
                }
                Object test = creationalContexts.create(clazz, ApplicationScoped.class);
                initWeldFields(test, test.getClass());
                next.evaluate();
            } finally {
                exitWeld();
            }


        }

        public void initWeld() {
            if(startupException == null) {
                try {
                    WeldDiscoveringRule.this.initWeld();
                } catch (Throwable e) {
                    if(startupException == null) {
                        startupException = e;
                    }
                    if(e instanceof ClassFormatError) {
                        throw new RuntimeException(e);
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
            if(clazzP.equals(Object.class)) {
                return;
            }
            for (Field f : clazzP.getDeclaredFields()) {
                f.setAccessible(true);
                if(f.getAnnotation(Inject.class) != null) {
                    f.set(testInstance, f.get(newTestInstance));
                }
                else {
                    if(f.get(newTestInstance) != null && f.get(testInstance) == null) {
                        f.set(testInstance, f.get(newTestInstance));
                    }
                }

            }
            initWeldFields(newTestInstance, clazzP.getSuperclass());
        }

    }

}