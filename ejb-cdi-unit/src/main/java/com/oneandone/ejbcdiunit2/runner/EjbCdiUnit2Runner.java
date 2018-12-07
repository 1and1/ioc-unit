package com.oneandone.ejbcdiunit2.runner;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;

import org.jboss.weld.transaction.spi.TransactionServices;
import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.testanalyzer.CdiConfigCreator;
import com.oneandone.cdi.testanalyzer.InitialConfiguration;
import com.oneandone.cdi.tester.ProducesAlternative;
import com.oneandone.cdi.tester.contexts.internal.InitialListenerProducer;
import com.oneandone.cdi.tester.ejb.AsynchronousManager;
import com.oneandone.cdi.tester.ejb.EjbExtensionExtended;
import com.oneandone.cdi.tester.ejb.EjbInformationBean;
import com.oneandone.cdi.tester.ejb.EjbUnitBeanInitializerClass;
import com.oneandone.cdi.tester.ejb.EjbUnitTransactionServices;
import com.oneandone.cdi.tester.ejb.SupportEjbExtended;
import com.oneandone.cdi.tester.ejb.persistence.SimulatedTransactionManager;
import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.cdi.weldstarter.WeldSetup;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.WrappedDeploymentException;
import com.oneandone.cdi.weldstarter.spi.WeldStarter;

public class EjbCdiUnit2Runner extends BlockJUnit4ClassRunner {
    private static Logger logger = LoggerFactory.getLogger(EjbCdiUnit2Runner.class);
    private final Class<?> clazz;
    private FrameworkMethod frameworkMethod;
    private Throwable startupException;

    public EjbCdiUnit2Runner(Class<?> clazz) throws InitializationError {
        super(clazz);
        this.clazz = clazz;
    }

    @Override
    protected Statement methodBlock(final FrameworkMethod frameworkMethodP) {
        this.frameworkMethod = frameworkMethodP;
        final Statement defaultStatement = super.methodBlock(frameworkMethodP);
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {

                if (startupException != null) {
                    if (frameworkMethodP.getAnnotation(Test.class).expected() == startupException.getClass()) {
                        return;
                    }
                    throw startupException;
                }
                System.setProperty("java.naming.factory.initial", "com.oneandone.cdiunit.internal.naming.CdiUnitContextFactory");
                InitialContext initialContext = new InitialContext();
                final BeanManager beanManager = weldStarter.get(BeanManager.class);
                initialContext.bind("java:comp/BeanManager", beanManager);
                try (CreationalContexts creationalContexts = new CreationalContexts(beanManager)) {
                    try {
                        Class.forName("javax.ejb.EJBContext");
                        creationalContexts.create(EjbUnitBeanInitializerClass.class, ApplicationScoped.class);
                    } catch (ClassNotFoundException e) {
                        logger.warn("Expected EJB to be present, when using EjbUnitRunner, therefore: " +
                                "could not init Startups and Jms-Objects.");
                    }

                    defaultStatement.evaluate();
                } finally {
                    initialContext.close();
                    weldStarter.tearDown();
                }

            }
        };
    }

    WeldSetupClass weldSetup = null;
    WeldStarter weldStarter = null;
    CdiConfigCreator cdiConfigCreator = null;

    @Override
    protected Object createTest() throws Exception {

        try {
            weldStarter = WeldSetupClass.getWeldStarter();
            String version = weldStarter.getVersion();
            if ("2.2.8 (Final)".equals(version) || "2.2.7 (Final)".equals(version)) {
                startupException = new Exception("Weld 2.2.8 and 2.2.7 are not supported. Suggest upgrading to 2.2.9");
            }
            System.setProperty("java.naming.factory.initial", "com.oneandone.cdiunit.internal.naming.CdiUnitContextFactory");

            try {
                if (cdiConfigCreator == null) {
                    InitialConfiguration cfg = new InitialConfiguration();
                    cfg.testClass = clazz;
                    cfg.testMethod = frameworkMethod.getMethod();
                    cfg.initialClasses.add(InitialListenerProducer.class);
                    cfg.initialClasses.add(SupportEjbExtended.class);
                    cfg.initialClasses.add(ProducesAlternative.class);
                    cfg.initialClasses.add(EjbInformationBean.class);
                    cfg.initialClasses.add(EjbUnitBeanInitializerClass.class);
                    cfg.initialClasses.add(AsynchronousManager.class);
                    cfg.initialClasses.add(EjbExtensionExtended.class);
                    cfg.initialClasses.add(SimulatedTransactionManager.class);
                    cfg.initialClasses.add(BeanManager.class);
                    cdiConfigCreator = new CdiConfigCreator();
                    cdiConfigCreator.create(cfg);
                }

                weldSetup = cdiConfigCreator.buildWeldSetup(frameworkMethod.getMethod());
                weldSetup.addService(new WeldSetup.ServiceConfig(TransactionServices.class, new EjbUnitTransactionServices()));
                weldStarter.start(weldSetup);
                InitialContext initialContext = new InitialContext();
                final BeanManager beanManager = weldStarter.get(BeanManager.class);
                initialContext.bind("java:comp/BeanManager", beanManager);
                try (CreationalContexts creationalContexts = new CreationalContexts(beanManager)) {
                    EjbInformationBean ejbInformationBean =
                            (EjbInformationBean) creationalContexts.create(EjbInformationBean.class, ApplicationScoped.class);
                    // TODO: ejbInformationBean.setApplicationExceptionDescriptions(weldTestConfig.getApplicationExceptionDescriptions());
                } finally {
                    initialContext.close();
                }
            } catch (Throwable e) {
                if (e instanceof WrappedDeploymentException)
                    e = e.getCause();
                if (startupException == null) {
                    startupException = e;
                }
            }
        } catch (Throwable e) {
            startupException = new Exception("Unable to start weld", e);
        }

        return weldStarter.get(clazz);
    }




}