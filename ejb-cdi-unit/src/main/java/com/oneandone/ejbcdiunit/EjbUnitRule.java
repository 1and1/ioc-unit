package com.oneandone.ejbcdiunit;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.naming.InitialContext;

import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.CDI11Bootstrap;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.jboss.weld.transaction.spi.TransactionServices;
import org.jboss.weld.util.reflection.Formats;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.ejbcdiunit.CdiTestConfig.ServiceConfig;
import com.oneandone.ejbcdiunit.cdiunit.Weld11TestUrlDeployment;
import com.oneandone.ejbcdiunit.cdiunit.WeldTestConfig;
import com.oneandone.ejbcdiunit.cdiunit.WeldTestUrlDeployment;
import com.oneandone.ejbcdiunit.internal.EjbInformationBean;

/**
 * @author aschoerk
 */
public class EjbUnitRule implements TestRule {
    private static Logger logger = LoggerFactory.getLogger(EjbUnitRunner.class);
    private final Object instance;
    private CdiTestConfig cdiTestConfig;
    private Method method;

    public EjbUnitRule(final Object instance, CdiTestConfig cdiTestConfig) {
        this.instance = instance;
        this.cdiTestConfig = cdiTestConfig;
    }

    public EjbUnitRule(final Object instance) {
        this(instance, null);
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        try {
            method = description.getTestClass().getMethod(description.getMethodName());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        Deployment result = new Deployment(instance, base, method, cdiTestConfig);
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
        protected Weld weld;
        protected WeldContainer container;
        protected Throwable startupException;
        // The TestCase instance
        private Class<?> clazz;
        private Object testInstance;

        public Deployment(final Object instance, final Statement next, final Method method, CdiTestConfig cdiTestConfig) {
            this.clazz = instance.getClass();
            this.testInstance = instance;
            this.next = next;
            try {
                String version = Formats.version(WeldBootstrap.class.getPackage());
                if ("2.2.8 (Final)".equals(version) || "2.2.7 (Final)".equals(version)) {
                    startupException = new Exception("Weld 2.2.8 and 2.2.7 are not supported. Suggest upgrading to 2.2.9");
                }


                final WeldTestConfig weldTestConfig =
                        new WeldTestConfig(clazz, method, cdiTestConfig)
                                .addClass(SupportEjbExtended.class)
                                .addServiceConfig(new ServiceConfig(TransactionServices.class,
                                        new EjbUnitTransactionServices()))
                ;
                EjbUnitRule.this.cdiTestConfig = weldTestConfig;


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
                    };

                };


            } catch (ClassFormatError e) {

                startupException = parseClassFormatError(e);
            } catch (Throwable e) {
                startupException = new Exception("Unable to start weld", e);
            }
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

        /**
         * Run the action, throwing a {@code Throwable} if anything goes wrong.
         */
        @Override
        public void evaluate() throws Throwable {
            if (container == null)
                initWeld();
            if (startupException != null) {
                if (method != null && method.getAnnotation(Test.class).expected() == startupException.getClass()) {
                    return;
                }
                throw startupException;
            }
            System.setProperty("java.naming.factory.initial", "com.oneandone.cdiunit.internal.naming.CdiUnitContextFactory");
            InitialContext initialContext = new InitialContext();
            final BeanManager beanManager = container.getBeanManager();
            initialContext.bind("java:comp/BeanManager", beanManager);
            try (CreationalContexts creationalContexts = new CreationalContexts(beanManager)) {
                try {
                    Class.forName("javax.ejb.EJBContext");
                    creationalContexts.create(EjbUnitBeanInitializerClass.class, ApplicationScoped.class);
                    EjbInformationBean ejbInformationBean =
                            (EjbInformationBean) creationalContexts.create(EjbInformationBean.class, ApplicationScoped.class);
                    ejbInformationBean.setApplicationExceptionDescriptions(cdiTestConfig.getApplicationExceptionDescriptions());
                    Object test = creationalContexts.create(clazz, ApplicationScoped.class);

                    initWeldFields(test, test.getClass());

                } catch (ClassNotFoundException e) {
                    logger.warn("Expected EJB to be present, when using EjbUnitRunner, therefore: "
                            + "could not init Startups and Jms-Objects.");
                }

                next.evaluate();
            } finally {
                initialContext.close();
                weld.shutdown();
                container = null;
            }


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