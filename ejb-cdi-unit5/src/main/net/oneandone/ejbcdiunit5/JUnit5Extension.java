package net.oneandone.ejbcdiunit5;

import com.oneandone.ejbcdiunit.*;
import com.oneandone.ejbcdiunit.cdiunit.Weld11TestUrlDeployment;
import com.oneandone.ejbcdiunit.cdiunit.WeldTestConfig;
import com.oneandone.ejbcdiunit.cdiunit.WeldTestUrlDeployment;
import com.oneandone.ejbcdiunit.internal.EjbInformationBean;
import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.api.Bootstrap;
import org.jboss.weld.bootstrap.api.CDI11Bootstrap;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.jboss.weld.resources.spi.ResourceLoader;
import org.jboss.weld.transaction.spi.TransactionServices;
import org.jboss.weld.util.reflection.Formats;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.naming.InitialContext;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD;

public class JUnit5Extension implements TestInstancePostProcessor, AfterTestExecutionCallback {

    private static Logger logger = LoggerFactory.getLogger(JUnit5Extension.class);
    // global system property
    public static final String GLOBAL_EXPLICIT_PARAM_INJECTION = "org.jboss.weld.junit5.explicitParamInjection";
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
    public void afterTestExecution(ExtensionContext context) throws Exception {
        if (determineTestLifecycle(context).equals(PER_METHOD)) {
            initialContext.close();
            weld.shutdown();
            container = null;
        }
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        this.clazz = testInstance.getClass();
        this.testInstance = testInstance;

        Optional<Method> method = context.getTestMethod();
        try {
            String version = Formats.version(WeldBootstrap.class.getPackage());
            if ("2.2.8 (Final)".equals(version) || "2.2.7 (Final)".equals(version)) {
                startupException = new Exception("Weld 2.2.8 and 2.2.7 are not supported. Suggest upgrading to 2.2.9");
            }


            final WeldTestConfig weldTestConfig =
                    new WeldTestConfig(clazz, null, null)
                            .addClass(SupportEjbExtended.class)
                            .addServiceConfig(new CdiTestConfig.ServiceConfig(TransactionServices.class,
                                    new EjbUnitTransactionServices()))
                    ;

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
                };

            };


        } catch (ClassFormatError e) {

            startupException = parseClassFormatError(e);
        } catch (Throwable e) {
            startupException = new Exception("Unable to start weld", e);
        } // store info about explicit param injection, either from global settings or from annotation on the test class


        if (container == null)
            initWeld();
        if (startupException != null) {
            if (method.isPresent() && method.get().getAnnotation(Test.class).expected() == startupException.getClass()) {
                return;
            }
            if (startupException instanceof Exception)
                throw (Exception)startupException;
            else
                throw new RuntimeException(startupException);
        }
        System.setProperty("java.naming.factory.initial", "org.jglue.cdiunit.internal.naming.CdiUnitContextFactory");
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
            Object test = creationalContexts.create(clazz, ApplicationScoped.class);

            initWeldFields(test, test.getClass());

        } catch (ClassNotFoundException e) {
            logger.warn("Expected EJB to be present, when using EjbUnitRunner, therefore: "
                    + "could not init Startups and Jms-Objects.");
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
