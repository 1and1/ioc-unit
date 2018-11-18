package com.oneandone.ejbcdiunit5;

import com.oneandone.cdi.weldstarter.WeldSetup;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.spi.WeldStarter;
import com.oneandone.ejbcdiunit.*;
import com.oneandone.ejbcdiunit.cfganalyzer.TestConfigAnalyzer;
import com.oneandone.ejbcdiunit.internal.EjbInformationBean;
import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.transaction.spi.TransactionServices;
import org.jboss.weld.util.reflection.Formats;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.lang.reflect.Constructor;
import java.net.URL;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD;

public class JUnit5Extension implements BeforeEachCallback,
        AfterAllCallback, TestExecutionExceptionHandler, TestInstanceFactory
{

    private static Logger logger = LoggerFactory.getLogger(JUnit5Extension.class);
    // global system property
    private static final String ABSENT_CODE_PREFIX = "Absent Code attribute in method that is not native or abstract in class file ";
    protected WeldStarter weldStarter;
    private WeldSetupClass weldSetup;
    protected Throwable startupException;
    // The TestCase instance
    private CdiTestConfig cdiTestConfig;
    CreationalContexts creationalContexts;
    InitialContext initialContext;


    @Override
    public void afterAll(final ExtensionContext extensionContext) throws Exception {
        logger.trace("---->after All execution {} {}\n", extensionContext.getDisplayName(), this);
        shutdownWeldIfRunning(false);
    }

    private void shutdownWeldIfRunning(boolean ignoreException) throws NamingException {
        if (weldStarter != null) {
            logger.trace("----> shutting down Weld");
            if (initialContext != null)
                initialContext.close();
            if (ignoreException) {
                try {
                    weldStarter.tearDown();
                } catch (Throwable thw) {
                    logger.debug("Ignored {}", thw);
                }
            } else {
                weldStarter.tearDown();
            }
            initialContext = null;
            weldStarter = null;
        }
    }

    private Object createTestInstance(Class<?> clazz) throws Exception {
        startupException = null;
        shutdownWeldIfRunning(false);
        if (weldStarter == null) {
            logger.trace("----> starting up Weld.");
            try {
                String version = Formats.version(WeldBootstrap.class.getPackage());
                if ("2.2.8 (Final)".equals(version) || "2.2.7 (Final)".equals(version)) {
                    startupException = new Exception("Weld 2.2.8 and 2.2.7 are not supported. Suggest upgrading to 2.2.9");
                }

                final CdiTestConfig weldTestConfig =
                        new CdiTestConfig(clazz, null, null)
                                .addClass(SupportEjbExtended.class)
                                .addServiceConfig(new CdiTestConfig.ServiceConfig(TransactionServices.class,
                                        new EjbUnitTransactionServices()));

                this.cdiTestConfig = weldTestConfig;
                weldStarter = WeldSetupClass.getWeldStarter();

                if (weldSetup == null) {
                    TestConfigAnalyzer cdiUnitAnalyzer = new TestConfigAnalyzer();
                    cdiUnitAnalyzer.analyze(weldTestConfig);
                    weldSetup = new WeldSetupClass();
                    weldSetup.setBeanClassNames(weldTestConfig.getDiscoveredClasses());

                    weldSetup.setAlternativeClasses(weldTestConfig.getAlternatives());
                    weldSetup.setEnabledAlternativeStereotypeMetadatas(weldTestConfig.getEnabledAlternativeStereotypes());
                    weldSetup.setEnabledDecorators(weldTestConfig.getEnabledDecorators());
                    weldSetup.setEnabledInterceptors(weldTestConfig.getEnabledInterceptors());

                    weldSetup.setExtensionMetadata(weldTestConfig.getExtensions());
                    weldSetup.addService(new WeldSetup.ServiceConfig(TransactionServices.class, new EjbUnitTransactionServices()));
                }
                weldStarter.start(weldSetup);


            } catch (ClassFormatError e) {

                startupException = parseClassFormatError(e);
            } catch (Throwable e) {
                startupException = new Exception("Unable to start weld", e);
            } // store info about explicit param injection, either from global settings or from annotation on the test class


            if (weldStarter == null)
                initWeld();
            if (startupException != null) {
                return clazz.newInstance(); // prepare default, to allow beforeEach to handle exception.
            }
            System.setProperty("java.naming.factory.initial", "com.oneandone.cdiunit.internal.naming.CdiUnitContextFactory");
            initialContext = new InitialContext();
            final BeanManager beanManager = weldStarter.get(BeanManager.class);
            initialContext.bind("java:comp/BeanManager", beanManager);
            creationalContexts = new CreationalContexts(beanManager);
            try {

                Class.forName("javax.ejb.EJBContext");
                creationalContexts.create(EjbUnitBeanInitializerClass.class, ApplicationScoped.class);
                EjbInformationBean ejbInformationBean =
                        (EjbInformationBean) creationalContexts.create(EjbInformationBean.class, ApplicationScoped.class);
                ejbInformationBean.setApplicationExceptionDescriptions(cdiTestConfig.getApplicationExceptionDescriptions());

                Object test = creationalContexts.create(clazz, ApplicationScoped.class);

                logger.trace("---->Found testinstance {}\n", test);
                // create the instance used for testing by fields initialized by cdi-container
                return test;

            } catch (ClassNotFoundException e) {
                logger.warn("Expected EJB to be present, when using EjbUnitRunner, therefore: "
                        + "could not init Startups and Jms-Objects.");
            }
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
            URL url = EjbUnitRule.class.getClassLoader().getResource(offendingClass + ".class");

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

     @Override public Object createTestInstance(final TestInstanceFactoryContext testInstanceFactoryContext, final ExtensionContext
     extensionContext) throws TestInstantiationException
     {
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
