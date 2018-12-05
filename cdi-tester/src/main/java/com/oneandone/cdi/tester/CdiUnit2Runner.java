package com.oneandone.cdi.tester;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;

import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oneandone.cdi.testanalyzer.CdiConfigCreator;
import com.oneandone.cdi.testanalyzer.InitialConfiguration;
import com.oneandone.cdi.weldstarter.CreationalContexts;
import com.oneandone.cdi.weldstarter.WeldSetupClass;
import com.oneandone.cdi.weldstarter.WrappedDeploymentException;
import com.oneandone.cdi.weldstarter.spi.TestExtensionService;
import com.oneandone.cdi.weldstarter.spi.WeldStarter;

/**
 * @author aschoerk
 */
public class CdiUnit2Runner extends BlockJUnit4ClassRunner {
    private static Logger logger = LoggerFactory.getLogger(CdiUnit2Runner.class);
    private final Class<?> clazz;
    private final List<TestExtensionService> testExtensionServices = new ArrayList<>();
    private FrameworkMethod frameworkMethod;
    private Throwable startupException;

    public CdiUnit2Runner(Class<?> clazz) throws InitializationError {
        super(clazz);
        if (testExtensionServices.size() == 0) {
            ServiceLoader<TestExtensionService> loader = ServiceLoader.load(TestExtensionService.class);
            final Iterator<TestExtensionService> testExtensionServiceIterator = loader.iterator();
            while (testExtensionServiceIterator.hasNext()) {
                testExtensionServices.add(testExtensionServiceIterator.next());
            }
        }
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
                System.setProperty("java.naming.factory.initial", "com.oneandone.cdi.tester.naming.CdiTesterContextFactory");
                InitialContext initialContext = new InitialContext();
                final BeanManager beanManager = weldStarter.get(BeanManager.class);
                initialContext.bind("java:comp/BeanManager", beanManager);
                try (CreationalContexts creationalContexts = new CreationalContexts(beanManager)) {
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
            System.setProperty("java.naming.factory.initial", "com.oneandone.cdi.tester.naming.CdiTesterContextFactory");

            try {
                if (cdiConfigCreator == null) {
                    InitialConfiguration cfg = new InitialConfiguration();
                    cfg.testClass = clazz;
                    cfg.testMethod = frameworkMethod.getMethod();
                    cfg.initialClasses.add(BeanManager.class);
                    cdiConfigCreator = new CdiConfigCreator();
                    cdiConfigCreator.create(cfg);
                }

                weldSetup = cdiConfigCreator.buildWeldSetup(frameworkMethod.getMethod());
                if (testExtensionServices != null) {
                    for (TestExtensionService te : testExtensionServices) {
                        te.preStartupAction(weldSetup);
                    }
                }
                weldStarter.start(weldSetup);

                InitialContext initialContext = new InitialContext();
                final BeanManager beanManager = weldStarter.get(BeanManager.class);
                initialContext.bind("java:comp/BeanManager", beanManager);
                System.setProperty("java.naming.factory.initial", "com.oneandone.cdi.tester.naming.CdiTesterContextFactory");
                initialContext.bind("java:comp/BeanManager", beanManager);
                try (CreationalContexts creationalContexts = new CreationalContexts(beanManager)) {
                    if (testExtensionServices != null) {
                        for (TestExtensionService te : testExtensionServices) {
                            te.postStartupAction(creationalContexts);
                        }
                    }
                }
                initialContext.close();
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

